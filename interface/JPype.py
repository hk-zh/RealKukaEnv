import jpype
import os
from jpype.types import *

import numpy as np





class KukaInterface:
    def __init__(self, initialPos):
        print(os.path.join(os.path.abspath('.'), 'kuka_interface/iiwa_workspace1.16/kuka.jar'))
        self.jarpath = os.path.join(os.path.abspath('.'), 'kuka_interface/iiwa_workspace1.16/kuka.jar')
        jpype.startJVM(classpath=[self.jarpath])
        Controller = jpype.JClass('Controller.Controller')
        self.controller = Controller(JArray(JDouble)(np.array(initialPos)))
        '''sim uses m while real kuka uses mm'''
        self.unit = 1e3
        '''offset of world coordinate between simulation and real world'''
        self.offset = np.zeros(3)

    def real2sim(self, coordinate):
        return coordinate/ self.unit - self.offset

    def sim2real(self, coordinate):
        return (coordinate + self.offset) * self.unit

    def setOffset(self, offset):
        self.offset = np.array(offset)

    def getCurrentJoint(self) -> np.ndarray:
        return np.array(self.controller.getCurrentJoints())

    def getCurrentFrame(self) -> np.ndarray:
        return self.real2sim(np.array(self.controller.getCurrentFrame()))

    def getCurrentFrameVelocity(self):
        return np.array(self.controller.getCurrentFrameVelocity()) / self.unit

    def setAction(self, action):
        self.controller.setAction(JArray(JDouble)(action * self.unit))

    def step(self):
        self.controller.step()

    def dispose(self):
        self.controller.dispose()

    def resetInitialPosition(self):
        self.controller.resetInitialPosition()

    @staticmethod
    def shutdown():
        jpype.shutdownJVM()







