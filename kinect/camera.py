import freenect
import cv2
import numpy as np
import time
import multiprocessing
import math
rectangle_world = np.float32([[358.26, -360.78], [358.56, 350.00], [815, -360.78], [815, 350.00]])
rectangle_screen = np.float32([[0, 0], [1200, 0], [0, 800], [1200, 800]])


def getTransformation(src, dest):
    return cv2.getPerspectiveTransform(src, dest)


def redFilter(array):
    hsv = cv2.cvtColor(array, cv2.COLOR_RGB2HSV)
    lower_red = np.array([0, 110, 110])
    upper_red = np.array([0, 255, 255])
    mask0 = cv2.inRange(hsv, lower_red, upper_red)
    lower_red = np.array([172, 120, 120])
    upper_red = np.array([180, 255, 255])
    mask1 = cv2.inRange(hsv, lower_red, upper_red)
    return mask0 + mask1


def blackFilter(array):
    hsv = cv2.cvtColor(array, cv2.COLOR_RGB2HSV)
    lower_black = np.array([0, 0, 0])
    upper_black = np.array([255, 255, 20])
    mask = cv2.inRange(hsv, lower_black, upper_black)
    return mask


def blueFilter(array):
    hsv = cv2.cvtColor(array, cv2.COLOR_RGB2HSV)
    lower_blue = np.array([100, 150, 0])
    upper_blue = np.array([[140, 255, 255]])
    mask = cv2.inRange(hsv, lower_blue, upper_blue)
    return mask


def greenFilter(array):
    hsv = cv2.cvtColor(array, cv2.COLOR_RGB2HSV)
    lower_green = np.array([50, 100, 100])
    upper_green = np.array([[100, 255, 255]])
    mask = cv2.inRange(hsv, lower_green, upper_green)
    return mask


def myKey(e):
    return e[1]


def getMarkersPositions(original):
    mask = greenFilter(original)
    cnts = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    cnts = cnts[0] if len(cnts) == 2 else cnts[1]
    num = 0
    quadrilateral = []
    for c in cnts:
        x, y, w, h = cv2.boundingRect(c)
        if w * h > 50:
            num = num + 1
            quadrilateral.append([x + w / 2, y + h / 2])
    if num == 4:
        quadrilateral.sort(key=myKey)
        if quadrilateral[0][0] > quadrilateral[1][0]:
            t = quadrilateral[0]
            quadrilateral[0] = quadrilateral[1]
            quadrilateral[1] = t
        if quadrilateral[2][0] > quadrilateral[3][0]:
            t = quadrilateral[2]
            quadrilateral[2] = quadrilateral[3]
            quadrilateral[3] = t
        return np.float32(quadrilateral)
    return None


def getObjectImageRecrangle(original):
    mask = redFilter(original)
    cnts = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    cnts = cnts[0] if len(cnts) == 2 else cnts[1]
    pos = np.zeros(2)
    num = 0
    rx, ry, rw, rh = 0, 0, 0, 0
    for c in cnts:
        x, y, w, h = cv2.boundingRect(c)
        if w * h > 30:
            num = num + 1
            rx = x
            ry = y
            rw = w
            rh = h

    if num == 1:
        return [rx, ry, rw, rh]
    else:
        return None


def getObjectImagePosition(original):
    mask = redFilter(original)
    cnts = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    cnts = cnts[0] if len(cnts) == 2 else cnts[1]
    pos = np.zeros(2)
    num = 0
    for c in cnts:
        x, y, w, h = cv2.boundingRect(c)
        if w * h > 50:
            num = num + 1
            pos = pos + np.array([x + w / 2, y + h / 2])
    if num == 1:
        return list(pos)
    elif num == 2:
        return list(pos/2)
    else:
        return None


def getObjectPosition(M, objectImagePosition):
    objectImagePosition.append(1)
    pos = np.dot(M, np.float32(objectImagePosition).T)
    pos[0] = pos[0] / pos[2]
    pos[1] = pos[1] / pos[2]
    return pos[:2].copy()

def getObjectDepth():
    pass

def markGreen(original):
    mask = greenFilter(original)
    cnts = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    cnts = cnts[0] if len(cnts) == 2 else cnts[1]
    num = 0
    for c in cnts:
        x, y, w, h = cv2.boundingRect(c)
        if w * h > 50:
            num = num + 1
    if num == 4:
        for c in cnts:
            x, y, w, h = cv2.boundingRect(c)
            if w * h > 100:
                cv2.rectangle(original, (x, y), (x + w, y + h), (36, 255, 12), 2)
    return original


def markRed(original):
    mask = redFilter(original)
    cnts = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    cnts = cnts[0] if len(cnts) == 2 else cnts[1]
    for c in cnts:
        x, y, w, h = cv2.boundingRect(c)
        if w * h > 50:
            cv2.rectangle(original, (x, y), (x + w, y + h), (255, 50, 4), 2)
    return original


def RGB2BGR(array):
    return cv2.cvtColor(array, cv2.COLOR_RGB2BGR)


def getVideo():
    original, _ = freenect.sync_get_video()
    return original


def getDepth():
    array, _ = freenect.sync_get_depth()
    return array


def showImage():
    while 1:
        img = getVideo()
        bgr = RGB2BGR(markRed(markGreen(img)))
        # cv2.imshow('Depth image', depth)
        cv2.imshow('BGR image', bgr)
        positions = getMarkersPositions(img)
        if positions is not None:
            M = getTransformation(positions, rectangle_screen)
            img = cv2.warpPerspective(img, M, (1200, 800))
            cv2.imshow('trans image', RGB2BGR(img))
        k = cv2.waitKey(5) & 0xFF
        if k == 27:
            break
    cv2.destroyWindow('BGR image')
    cv2.destroyWindow('trans image')


class Camera:
    def __init__(self):
        self.offset = np.zeros(3)
        self.unit = 1e3
        self.heightOffset = 0.87
        # self.video = multiprocessing.Process(target=showImage)
        # self.video.start()
        self.M = None

    def setOffset(self, offset):
        self.offset = offset

    def real2sim(self, pos):
        return np.append((pos / self.unit - self.offset[:2]), self.heightOffset)

    def objectInAir(self):
        pass

    def getObjectVelocity(self):
        p1 = self.getObjectPosition()
        time.sleep(0.02)
        p2 = self.getObjectPosition()
        return (p2 - p1) / 0.02


    # def getObjectDepth(self):
    #     ObjectImageRectangle = getObjectImageRecrangle(getVideo())
    #     while ObjectImageRectangle is None:
    #         ObjectImageRectangle = getObjectImageRecrangle(getVideo())
    #     [x, y, w, h] = ObjectImageRectangle
    #
    #     array = getDepth()
    #     num = 0
    #     depthTotal = 0
    #     for i in range(int(x + w/4), int(x+3*w/4)):
    #         for j in range(int(y + h / 4), int(y + 3*h/4)):
    #             num += 1
    #             depthTotal += array[j][i]
    #
    #     depthAvg = depthTotal / num
    #
    #     return depthAvg


    def getObjectPosition(self):
        if self.M is None:
            MarkersPositions = getMarkersPositions(getVideo())
            objectImagePosition = getObjectImagePosition(getVideo())
            while objectImagePosition is None or MarkersPositions is None:
                objectImagePosition = getObjectImagePosition(getVideo())
                MarkersPositions = getMarkersPositions(getVideo())
            self.M = getTransformation(MarkersPositions, rectangle_world)
        else:
            objectImagePosition = getObjectImagePosition(getVideo())
            while objectImagePosition is None:
                objectImagePosition = getObjectImagePosition(getVideo())
        return self.real2sim(getObjectPosition(self.M, objectImagePosition))
