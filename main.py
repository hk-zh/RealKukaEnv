# This is a sample Python script.

# Press ⌃R to execute it or replace it with your code.
# Press Double ⇧ to search everywhere for classes, files, tool windows, actions, and settings.

import jpype
import os
import re
from ast import literal_eval
import freenect
from jpype.types import *
from interface import KukaInterface
import time
import numpy as np
from kinect.camera import Camera
import multiprocessing
import time

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


def testCamera():
    camera = Camera()
    print(camera.getObjectPosition())
    # print(1)


def test():
    env = KukaInterface([0.415, 1.13, 0.0, -1.44, 0.0, 0.565, 0.458, 0.0, 0.0])
    position = env.getCurrentFrame()

    pos = JArray(JDouble)(position+[200, 200, 200])

    env.step()
    env.step()
    env.step()
    env.step()
    print(position)


def foo(name):
    time.sleep(2)
    for i in range(10):
        print('s', name)

if __name__ == '__main__':

    # testCamera()
    testCamera()
    # a = """[[0, 1], [2, 3], [3, 4]]"""
    # a = re.sub(r"([^[])\s+([^]])", r"\1 \2", a)
    # a = np.array(literal_eval(a))
    # print(a)

    # print("Number of cpu: ", multiprocessing.cpu_count())
    # multiprocessing.set_start_method('spawn')
    # p = multiprocessing.Process(target=foo, args=('Bob', ))
    # p.start()
    # for i in range(10):
    #     print('b')
    # p.join()
