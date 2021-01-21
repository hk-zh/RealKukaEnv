import numpy as np
from envs import KukaEnv
from envs.utils import goal_distance


class KukaReach(KukaEnv):
    def __init__(self, reward_type='sparse'):
        initial_qpos = {
            'kuka_joint_1': 0.0,
            'kuka_joint_2': 0.712,
            'kuka_joint_3': 0.0,
            'kuka_joint_4': -1.26,
            'kuka_joint_5': 0.0,
            'kuka_joint_6': 1.17,
            'kuka_joint_7': 0.0
        }
        self.has_object = False
        self.target_in_the_air = True
        self.distance_threshold = 0.06
        self.reward_type = reward_type
        self.pos = np.zeros(3)
        self.sim_env_coord = np.array([0.8294, 0.0, 1.2437])

        super(KukaReach, self).__init__(
            initial_qpos=initial_qpos, n_actions=4, n_substeps=20
        )

    def compute_reward(self, achieved_goal: np.ndarray, desired_goal: np.ndarray):
        d = goal_distance(achieved_goal, desired_goal)
        if self.reward_type == 'sparse':
            return -(d > self.distance_threshold).astype(np.float32)
        else:
            return -d

    def reset(self):
        self.env.resetInitialPosition()
        self.goal = self._sample_goal().copy()
        obs = self._get_obs()
        return obs

    def _set_action(self, action):
        assert action.shape == (4,)
        action = action.copy()
        pos_ctrl = action[:3]
        pos_ctrl *= 0.05
        self.env.setAction(pos_ctrl)

    def _get_obs(self):
        grip_pos = self.env.getCurrentFrame()
        grip_velp = self.env.getCurrentFrameVelocity()
        object_pos = object_rot = object_velp = object_velr = object_rel_pos = np.zeros(0)
        gripper_state = np.zeros(0)
        gripper_vel = np.zeros(0)
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

    def _sample_goal(self):
        goal = self.initial_needle_xpos[:3] + self.np_random.uniform(-0.15, 0.15, size=3)
        return goal.copy()

    def _env_setup(self):
        # self.env.stepJoints(np.array(joints))
        currentFrame = self.env.getCurrentFrame()
        offset = self.env.getCurrentFrame() - self.sim_env_coord
        self.env.setOffset(offset)
        self.initial_needle_xpos = self.env.getCurrentFrame().copy()

    def _step_callback(self):
        pass

    def _set_goal(self, goal):
        self.goal = goal.copy()

    def _is_success(self, achieved_goal, desired_goal):
        d = goal_distance(achieved_goal, desired_goal)
        return (d < self.distance_threshold).astype(np.float32)

    def close(self):
        self.env.dispose()
        self.env.shutdown()