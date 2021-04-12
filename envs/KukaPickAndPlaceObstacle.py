import numpy as np
from envs import KukaEnv
from envs.utils import goal_distance
from kinect.camera import Camera


class KukaPickAndPlaceObstacle(KukaEnv):
    def __init__(self, reward_type='sparse'):
        initial_qpos = {
            'kuka_joint_1': 0.326,
            'kuka_joint_2': 1.07,
            'kuka_joint_3': 0.0,
            'kuka_joint_4': -1.44,
            'kuka_joint_5': 0.0,
            'kuka_joint_6': 0.607,
            'kuka_joint_7': 0.336,
            'r_gripper_finger_joint': 0.0,
            'l_gripper_finger_joint': 0.0
        }

        self.target_range_x = 0.2
        self.target_range_y = 0.1
        self.has_object = True
        self.target_in_the_air = False
        self.distance_threshold = 0.06
        self.sim_env_coord = np.array([0.7619, 0.1899, 0.966])  # todo modify this position
        self.reward_type = reward_type
        self.gripperOffset = 0.07  # the difference between sizes of gripper in sim and real world
        self.target_range_x = 0.06
        self.target_range_y = 0.06
        self.target_center = np.array([0.75, -0.2, 0.85])

        self.objectOffset_x = 0.02
        self.objectOffset_y = 0.01

        self.height_offset = 0.87
        self.camera = Camera()
        self.isSuccess = False
        super(KukaPickAndPlaceObstacle, self).__init__(
            initial_qpos=initial_qpos, n_actions=4, n_substeps=20
        )

    def compute_reward(self, achieved_goal: np.ndarray, desired_goal: np.ndarray):
        d = goal_distance(achieved_goal, desired_goal)
        if self.reward_type == 'sparse':
            return -(d > self.distance_threshold).astype(np.float32)
        else:
            return -d

    def _sample_goal(self):
        goal = self.target_center.copy()

        goal[1] += self.np_random.uniform(-self.target_range_y, self.target_range_y)
        goal[0] += self.np_random.uniform(-self.target_range_x, self.target_range_x)
        return goal.copy()

    def reset(self):
        self.env.resetInitialPosition()
        self.goal = self._sample_goal().copy()
        obs = self._get_obs()
        return obs

    def _set_action(self, action):
        assert action.shape == (4,)
        action = action.copy()
        pos_ctrl, gripper_ctrl = action[:3], action[3]
        pos_ctrl *= 0.04
        gripper_pos = self.env.getCurrentFrame() - self.gripperOffset
        if self.env.hasObject() and gripper_pos[2] >= 0.89 and gripper_pos[1] < 0:
            gripper_ctrl = -0.05
        if self.isSuccess:
            gripper_ctrl = 0.05
            pos_ctrl[0] = 0.0
            pos_ctrl[1] = 0.0
            pos_ctrl[2] = 0.001
        self.env.setAction(pos_ctrl)
        self.env.setGripperAction(gripper_ctrl)

    def _get_obs(self):
        grip_pos = self.env.getCurrentFrame()
        grip_pos[2] = grip_pos[2] - self.gripperOffset
        grip_velp = self.env.getCurrentFrameVelocity() * 0.04
        if self.has_object:
            if self.env.hasObject():
                object_pos = grip_pos
                object_velp = grip_velp
            else:
                object_pos = self.camera.getObjectPosition()
                object_velp = self.camera.getObjectVelocity()
                object_pos[0] = object_pos[0] + self.objectOffset_x
                object_pos[1] = object_pos[1] + self.objectOffset_y

            object_rot = np.zeros(3)
            object_velr = np.zeros(3)
            object_rel_pos = object_pos - grip_pos
            object_velp -= grip_velp

        else:
            object_pos = object_rot = object_velp = object_velr = object_rel_pos = np.zeros(0)

        if self.env.hasObject():
            gripper_state = np.array([0, 0])
        else:
            gripper_state = self.env.getCurrentGripperPosition()
        gripper_vel = np.array([0, 0])
        achieved_goal = grip_pos.copy()
        obs = np.concatenate([
            grip_pos, object_pos.ravel(), object_rel_pos.ravel(), gripper_state, object_rot.ravel(),
            object_velp.ravel(), object_velr.ravel(), grip_velp, gripper_vel,
        ])
        return {
            'observation': obs.copy(),
            'achieved_goal': achieved_goal.copy(),
            'desired_goal': self.goal.copy(),
        }

    def _env_setup(self):
        # self.env.stepJoints(np.array(joints))
        offset = self.env.getCurrentFrame() - self.sim_env_coord
        self.env.setOffset(offset)
        self.camera.setOffset(offset)
        self.initial_needle_xpos = self.env.getCurrentFrame().copy()
        self.target_center[2] = self.height_offset

    def _step_callback(self):
        pass

    def _set_goal(self, goal):
        self.goal = goal.copy()

    def set_goal(self, goal):
        self.goal = goal.copy()

    def _is_success(self, achieved_goal, desired_goal):
        d = goal_distance(achieved_goal, desired_goal)
        self.isSuccess = (d < self.distance_threshold).astype(np.float32)
        return self.isSuccess

    def close(self):
        self.env.dispose()
        self.env.shutdown()

    def getCurrentJointsPosition(self):
        return self.env.getCurrentJoints()

