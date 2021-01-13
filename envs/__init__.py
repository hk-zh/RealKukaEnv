from .KukaEnv import KukaEnv
from .KukaReach import KukaReach

def make_env(args):
    return {
        'KukaReach-v1': KukaReach
    }[args.env]()

Kuka_envs_id = [
    'KukaReach-v1',
    'KukaPickAndPlaceObstacle-v1',
    'KukaPickNoObstacle-v1',
    'KukaPickThrow-v1',
    'KukaPushLabyrinth-v1',
    'KukaPushSlide-v1'
]