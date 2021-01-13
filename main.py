# This is a sample Python script.

# Press ⌃R to execute it or replace it with your code.
# Press Double ⇧ to search everywhere for classes, files, tool windows, actions, and settings.

import jpype
import os
from jpype.types import *
from interface import KukaInterface
import time
import numpy as np


def step(t):
    j = np.array([-0.000, 0.125, 0.225, -0.749, -0.000, -0.000, 0.527])
    joints = JArray(JDouble)(j)
    t.step(joints)

def step1(t):
    j = np.array([-0.200, 0.125, -0.225, -0.449, -0.000, -0.000, 0.127])
    joints = JArray(JDouble)(j)
    t.step(joints)

def getCurrentJoints(t):
    joints = np.array(t.getCurrentJoints())
    print(joints)





def test():
    env = KukaInterface()
    position = env.getCurrentFrame()

    pos = JArray(JDouble)(position+[200,200,200])
    env.stepFrame(pos)

    env.step()
    env.step()
    env.step()
    env.step()
    print(position)




if __name__ == '__main__':
    test()
