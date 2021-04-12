from .KukaEnv import KukaEnv
from .KukaReach import KukaReach
from .KukaPush import KukaPush
from .KukaReach import KukaReach
from .KukaPickNoObstacle import KukaPickNoObstacle
from .KukaPickAndPlaceObstacle import KukaPickAndPlaceObstacle
from .KukaReach import KukaReach
from .KukaPush import KukaPush


def make_env(args):
    return {
        'KukaReach-v1': KukaReach,
        'KukaPush-v1': KukaPush,
        'KukaPickNoObstacle-v1': KukaPickNoObstacle,
        'KukaPickAndPlaceObstacle-v1': KukaPickAndPlaceObstacle
    }[args.env]()


Kuka_envs_id = [
    'KukaReach-v1',
    'KukaPickAndPlaceObstacle-v1',
    'KukaPickNoObstacle-v1',
    'KukaPickThrow-v1',
    'KukaPushLabyrinth-v1',
    'KukaPushSlide-v1',
    'KukaPush-v1'
]