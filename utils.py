import numpy as np


def goal_concat(obs, goal):
    return np.concatenate([obs, goal], axis=0)


def goal_based_process(obs):
    return goal_concat(obs['observation'], obs['desired_goal'])
