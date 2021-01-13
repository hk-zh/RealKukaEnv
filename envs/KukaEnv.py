from gym import utils, spaces
from gym.utils import seeding
import numpy as np
from interface import KukaInterface

MAX_EPISODE_STEPS = 50


class KukaEnv():
    def __init__(self, initial_qpos, n_actions, n_substeps):
        initialPos = []
        for name, value in initial_qpos.items():
            initialPos.append(value)
        self.env = KukaInterface(initialPos)
        self._env_setup()
        self.initial_qpos = initial_qpos
        self.seed()
        self.goal = self._sample_goal()
        self.n_substeps = n_substeps
        obs = self._get_obs()
        self.max_episode_steps = MAX_EPISODE_STEPS
        self.action_space = spaces.Box(-1., 1., shape=(n_actions,), dtype='float32')

        self.observation_space = spaces.Dict(dict(
            desired_goal=spaces.Box(-np.inf, np.inf, shape=obs['achieved_goal'].shape, dtype='float32'),
            achieved_goal=spaces.Box(-np.inf, np.inf, shape=obs['achieved_goal'].shape, dtype='float32'),
            observation=spaces.Box(-np.inf, np.inf, shape=obs['observation'].shape, dtype='float32'),
        ))

    def seed(self, seed=None):
        self.np_random, seed = seeding.np_random(seed)
        return [seed]

    def reset(self):
        pass

    def compute_reward(self, achieved_goal: np.ndarray, desired_goal: np.ndarray):
        raise NotImplementedError()

    def _get_obs(self):
        raise NotImplementedError()

    def _env_setup(self, initial_qpos):
        raise NotImplementedError()

    def _is_success(self, achieved_goal, desired_goal):
        """Indicates whether or not the achieved goal successfully achieved the desired goal."""
        raise NotImplementedError()

    def _sample_goal(self):
        """Samples a new goal and returns it."""
        raise NotImplementedError()

    def _set_action(self, action):
        raise NotImplementedError()

    def _set_goal(self, goal):
        raise NotImplementedError()

    def _step_callback(self):
        raise NotImplementedError()

    def step(self, action):
        self._set_action(action)

        self.env.step()

        self._step_callback()
        obs = self._get_obs()

        done = False
        info = {
            'is_success': self._is_success(obs['achieved_goal'], self.goal),
        }
        reward = self.compute_reward(obs['achieved_goal'], self.goal)
        return obs, reward, done, info

