import numpy as np
import copy
from os_utils import get_arg_parser, get_logger, str2bool
from envs import Kuka_envs_id


def get_args():
    parser = get_arg_parser()

    parser.add_argument('--env', help='gym env id', type=str, default='FetchReach-v1', choices=Kuka_envs_id)
    parser.add_argument('--play_path', help='path to meta_file directory for play', type=str, default=None)
    parser.add_argument('--play_epoch', help='epoch to play', type=str, default='latest')

    args = parser.parse_args()
    return args
