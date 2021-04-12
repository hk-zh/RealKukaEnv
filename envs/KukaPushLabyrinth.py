import numpy as np
from envs import KukaPush
from envs.utils import goal_distance
from kinect.camera import Camera


class KukaPushLabyrinth(KukaPush):
    def __init__(self, reward_type='sparse'):
        initial_qpos = {
            'kuka_joint_1': 0.326,
            'kuka_joint_2': 0.942,
            'kuka_joint_3': 0.0,
            'kuka_joint_4': -1.74,
            'kuka_joint_5': 0.0,
            'kuka_joint_6': 0.461,
            'kuka_joint_7': 0.336
        }
        self.has_object = True
        self.target_in_the_air = False
        self.distance_threshold = 0.06
        self.reward_type = reward_type
        self.pos = np.zeros(3)
        self.sim_env_coord = np.array([0.674, 0.160, 0.973])
        # initial needle Cartesian position according to joints position in simulation
        self.gripperOffset = 0.07
        self.target_range_x = 0.06
        self.target_range_y = 0.06
        self.target_center = np.array([0.65, -0.2, 0])
        self.height_offset = 0.87
        self.camera = Camera(self.height_offset)

        super(KukaPushLabyrinth, self).__init__()



