import numpy as np
import Drawing as draw
from sklearn import datasets, linear_model, metrics
year = []
sunSpotActivity = []

def ReadFile(fileName):
    tempYear = []
    sunSpotActivityTemp = []
    file = open(fileName, "r")
    for line in file.readlines():
        lineWithoutSpecial = line.strip()
        split = lineWithoutSpecial.split("\t")
        tempYear.append(int(split[0]))
        sunSpotActivityTemp.append(int(split[1]))
    return tempYear, sunSpotActivityTemp

def calcDirivAndE(matrix, dirivative=False):
    if dirivative:
        return matrix * (1 - matrix)
    else:
        return 1/(1+np.exp(-matrix))

def normalizationFunction(list, min, max):
    data = []
    for element in list:
        temp = []
        for a in element:
            temp.append(((float(a) - min)/(max-min))*(1-(0))+(0))
        data.append(temp)
    return data

def deNormalization(list, min, max):
    data = []
    for element in list:
        temp = []
        for a in element:
            temp.append(float(a) * (max-min) + min)
        data.append(temp)
    return data

def mse(errors):
    if(len(errors) == 0):
        return None
    mseSum = 0
    for e in errors:
        mseSum += e*e
    return mseSum / len(errors)

def mad(errors):
    if(len(errors) < 2):
        return None
    absErrors = list(map(lambda x: abs(x), errors))
    absErrors.sort()
    index = int((len(absErrors) + 1) / 2)
    if(len(absErrors) % 2 == 0):
        result = absErrors[index - 1] + absErrors[index]
        return result / 2
    else:
        return absErrors[index - 1]

year, sunSpotActivity = ReadFile("sunspot.txt")
L = len(sunSpotActivity)
sunSpotActivityDataUsage = []
for a in range(L-2):
    sunSpotActivityDataUsage.append([int(sunSpotActivity[a]), int(sunSpotActivity[a+1])])

answerForSunActivity = []
answerForSunActivityGraphic = []

for element in sunSpotActivity[2:]:
    answerForSunActivity.append([element])
    answerForSunActivityGraphic.append(element)

answerForSunActivityGraphic = []
answerForSunActivityGraphic.append([])
answerForSunActivityGraphic.append([])
for element in range(len(sunSpotActivityDataUsage)):
    answerForSunActivityGraphic[0].append(sunSpotActivityDataUsage[element][0])
    answerForSunActivityGraphic[1].append(sunSpotActivityDataUsage[element][1])

# print(np.array(answerForSunActivity))
# print(np.array(sunSpotActivityDataUsage))

# draw.DrowPlot(year, sunSpotActivity)

dataForTraining = sunSpotActivityDataUsage[0:200]
dataForTrainingAnswer = answerForSunActivity[0:200]

# for element in dataForTrainingAnswer[0]

dataForTrainingNormalized = normalizationFunction(dataForTraining, min(sunSpotActivityDataUsage)[0], max(sunSpotActivityDataUsage)[0])
dataNormalized = normalizationFunction(sunSpotActivityDataUsage, min(sunSpotActivityDataUsage)[0], max(sunSpotActivityDataUsage)[0])
dataForTrainingAnswerNormalized = normalizationFunction(dataForTrainingAnswer, min(answerForSunActivity)[0], max(answerForSunActivity)[0])

dataRes = []
dataRes.append([])
dataRes.append([])
for element in dataNormalized:
    dataRes[0].append(element[0])
    dataRes[1].append(element[1])

print(dataForTrainingNormalized)
draw.DrowPlot3D(dataRes[0][0:200], dataRes[1][0:200], dataForTrainingAnswerNormalized)

#
# x = [
#     [0, 0, 0, 1],
#     [1, 1, 0, 1],
#     [1, 0, 1, 0],
#     [0, 1, 0, 1]
#     ]
#
# y = [
#     [0],
#     [1],
#     [1],
#     [1]
#     ]
#

net = linear_model.LinearRegression()
net.fit(np.array(dataForTraining), np.array(dataForTrainingAnswer))

w1 = net.coef_[0][0]
w2 = net.coef_[0][1]
b = net.intercept_

Tsu = net.predict(dataForTraining)
draw.DrawDiff(year[2:202], dataForTrainingAnswer, Tsu)


Ts = net.predict(sunSpotActivityDataUsage)
draw.DrawDiff(year[2:], sunSpotActivity[2:], Ts)

eVector = list()
for real, predicted in zip(answerForSunActivity, Ts):
    e = real - predicted
    eVector.append(e)

draw.DrowPlot(year[2:], eVector)
draw.DrowHist(eVector)

predictionMSE = mse(eVector)
predictionMAD = mad(eVector)
print('MSE = {}'.format(predictionMSE))
print('MAD = {}'.format(predictionMAD))

# draw.DrowPlot3D(dataRes[0][0:200], dataRes[1][0:200], Tsu)
# draw.DrowPlot3D(dataRes[0][2:len(sunSpotActivityDataUsage)], dataRes[1][2:len(sunSpotActivityDataUsage)], Ts)
# model = net.fit(dataForTrainingNormalized, dataForTrainingAnswerNormalized)

# np.random.seed(1)

# # syn0 = np.random.randn(2,1)-1
# # print(syn0[0])
# syn0 = 2 * np.random.random((2,1)) - 1
# bias = np.random.randn()
# lr = 0.01 # geriausias kai bias naudojamas lr yra 0.1
#
# data = np.array(dataForTrainingNormalized)
# answerData = np.array(dataForTrainingAnswerNormalized)
# # data = np.array(dataForTraining)
# # answerData = np.array(dataForTrainingAnswer)

# for iter in range(100000):

    # # forward propagation
    # l0 = np.dot(data,syn0) + bias # privalo buti atskirta
    # l1 = calcDirivAndE(l0)
    # how much did we miss?
    # l1_error = l1 - answerData
    # # print(l1_error[0][0])
    # if (iter % 10000) == 0:
    #     print("Error:" + str(np.average(np.abs(l1_error))))
    #     print(syn0)
    # # multiply how much we missed by the
    # # slope of the sigmoid at the values in l1
    # l1_delta = l1_error * calcDirivAndE(l1,True)
    # # update weights
    # syn0 = syn0 - lr * np.dot(data.T, l1_delta)
    # bias = bias - np.sum(lr * l1_delta)

# weights = []
# weights.append([])
# weights.append([])

# # w1 = np.average(syn0[0])
# # w2 = np.average(syn0[1])
# weight = []
# weight.append([-0.6761])
# weight.append([1.3715])
# # print(syn0)
# # print("Output After Training:")
# Tsu = np.dot(data, syn0) + bias
# # print(val)
# TsuRes = calcDirivAndE(Tsu)
# answerDeNormalized = deNormalization(TsuRes, min(answerForSunActivity)[0], max(answerForSunActivity)[0])
# dataForTrainingAnswerGen = []
# for el in range(len(answerDeNormalized)):
#     dataForTrainingAnswerGen.append(answerDeNormalized[el][0])
# # print(data[3])
# # print(dataForTrainingAnswerNormalized[3])
# # print(dataForTrainingAnswerGen)
# draw.DrowPlot3D(dataRes[0][0:200], dataRes[1][0:200], dataForTrainingAnswerGen)
#
#
# data10Years = sunSpotActivityDataUsage[2:len(sunSpotActivityDataUsage)]
# data10YearsNormalized = normalizationFunction(data10Years, min(sunSpotActivityDataUsage)[0], max(sunSpotActivityDataUsage)[0])
# data10YearsRes = np.dot(data10YearsNormalized, syn0) + bias
#
# data10YearsResDenormalize = deNormalization(data10YearsRes, min(answerForSunActivity)[0], max(answerForSunActivity)[0])
#
# data10YearsResDenormalizeGraphic = []
# for el in range(len(data10YearsResDenormalize)):
#     data10YearsResDenormalizeGraphic.append(data10YearsResDenormalize[el][0])
#
# print(len(dataRes[0][2:len(sunSpotActivityDataUsage)]))
# print(len(dataRes[1][2:len(sunSpotActivityDataUsage)]))
# print(len(data10Years))
#
# draw.DrowPlot3D(dataRes[0][2:len(sunSpotActivityDataUsage)], dataRes[1][2:len(sunSpotActivityDataUsage)], data10YearsResDenormalizeGraphic)

