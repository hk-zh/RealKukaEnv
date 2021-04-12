import numpy as np
from envs import make_env
from utils import goal_based_process
from common import get_args
import tensorflow as tf
import os
from gym.wrappers.monitoring.video_recorder import VideoRecorder


class Player:
    def __init__(self, args):
        # initialize environment
        self.args = args
        self.env = make_env(args)
        self.args.timesteps = self.env.max_episode_steps
        # self.env_test = make_env(args)
        self.info = []
        self.test_rollouts = 5

        # get current policy from path (restore tf session + graph)
        self.play_dir = args.play_path
        self.play_epoch = args.play_epoch
        self.meta_path = os.path.join(self.play_dir, "saved_policy-{}.meta".format(self.play_epoch))
        self.sess = tf.Session()
        self.saver = tf.train.import_meta_graph(self.meta_path)
        self.saver.restore(self.sess, tf.train.latest_checkpoint(self.play_dir))
        graph = tf.get_default_graph()
        self.raw_obs_ph = graph.get_tensor_by_name("raw_obs_ph:0")
        self.pi = graph.get_tensor_by_name("main/policy/net/pi/Tanh:0")

    def my_step_batch(self, obs):
        # compute actions from obs based on current policy by running tf session initialized before
        actions = self.sess.run(self.pi, {self.raw_obs_ph: obs})
        return actions

    def play(self):
        # play policy on env
        env = self.env
        acc_sum, obs = 0.0, []
        for i in range(self.test_rollouts):
            obs.append(goal_based_process(env.reset()))
            print(env.goal)
            for timestep in range(self.args.timesteps):
                actions = self.my_step_batch(obs)
                obs, infos = [], []
                ob, _, _, info = env.step(actions[0])
                obs.append(goal_based_process(ob))
                infos.append(info)

    def toRecordPush(self):
        env = self.env
        acc_sum, obs = 0.0, []
        file1 = open("log/KukaPushJointsTrajectory{}.txt".format(env.order), 'w')
        file2 = open("log/KukaPushPositionsTrajectory{}.txt".format(env.order), 'w')
        env.reset()
        joints = []
        positions = []
        obs.append(goal_based_process(env._get_obs()))
        print(env.goal)
        for timestep in range(self.args.timesteps):
            actions = self.my_step_batch(obs)
            obs, infos = [], []
            ob, _, _, info = env.step(actions[0])
            positions.append(list(ob['achieved_goal']))
            joints.append(list(env.getCurrentJointsPosition()))
            obs.append(goal_based_process(ob))
            infos.append(info)
        file1.write(str(joints) + "\n")
        file2.write(str(positions) + "\n")
        # print(str(joints))
        file1.close()
        file2.close()

    def toRecordReach(self):
        env = self.env
        goals = [[0.84604588, 0.14732964, 1.35766576], [0.79483348, -0.14184732,  1.20930532],
                 [0.919015, -0.15907337, 1.18060975], [7.11554270e-01, 1.51756884e-03, 1.34433537e+00],
                 [0.70905836, 0.13042637, 1.19320888]]
        acc_sum, obs = 0.0, []
        file = open('KukaPushJointsTrajectory.txt', 'w')
        for i in range(5):
            env.reset()
            obst = []
            env._set_goal(np.array(goals[i]))
            obs.append(goal_based_process(env._get_obs()))
            print(env.goal)

            for timestep in range(self.args.timesteps):
                actions = self.my_step_batch(obs)
                obs, infos = [], []
                ob, _, _, info = env.step(actions[0])
                obst.append(list(env.getCurrentJointsPosition()))
                obs.append(goal_based_process(ob))
                infos.append(info)
            file.write(str(obst) + "\n")
        file.close()

    def toRecordPickNoObstacle(self):
        env = self.env
        acc_sum, obs = 0.0, []
        goals = [[0.80948876, -0.24847823, 1.15], [0.90204398, -0.24176245, 1.15], [0.72934716, -0.19637749, 1.15], [0.8429464, -0.20765762, 1.15], [0.6970663, -0.18643907, 1.15]]
        env.reset()
        obst = []
        i = 4
        env.set_goal(np.array(goals[i]))
        obs.append(goal_based_process(env._get_obs()))
        print(env.goal)
        for timestep in range(self.args.timesteps):
            actions = self.my_step_batch(obs)
            obs, infos = [], []
            ob, _, _, info = env.step(actions[0])
            obst.append(list(env.getCurrentJointsPosition()))
            obs.append(goal_based_process(ob))
            infos.append(info)

    def toRecordPickAndPlaceObstacle(self):
        env = self.env
        acc_sum, obs = 0.0, []
        goals = [[0.80948876, -0.24847823, 0.85], [0.90204398, -0.24176245, 0.85], [0.72934716, -0.19637749, 0.85], [0.7029464, -0.18765762, 0.85], [0.6970663, -0.25643907, 0.85]]
        env.reset()
        obst = []
        i = 3
        env.set_goal(np.array(goals[i]))
        obs.append(goal_based_process(env._get_obs()))
        print(env.goal)
        for timestep in range(self.args.timesteps):
            actions = self.my_step_batch(obs)
            obs, infos = [], []
            ob, _, _, info = env.step(actions[0])
            obst.append(list(env.getCurrentJointsPosition()))
            obs.append(goal_based_process(ob))
            infos.append(info)


    def Test(self):
        env = self.env
        env.step(np.array([0.0, 1.0, 1.0, -1.0]))



if __name__ == "__main__":
    # Call play.py in order to see current policy progress
    args = get_args()
    player = Player(args)
    # player.play()
    player.toRecordPickAndPlaceObstacle()
    # player.toRecordPickNoObstacle()
    # player.Test()
    # player.toRecordReach()
    player.env.close()
