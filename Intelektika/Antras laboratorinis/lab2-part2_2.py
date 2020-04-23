import numpy as np
import Drawing as draw
import csv
from math import log

def filetrCheck(value, name):
    if name == "duration" and value >= 31.08 and value <= 25844.086:
        return True
    elif name == "width" and value >= 176 and value <= 1920:
        return True
    elif name == "height" and value >= 144 and value <= 1080:
        return True
    elif name == "bitrate" and value >= 8384 and value <= 7628466:
        return True
    elif name == "framerate" and value >= 5.7057524 and value <= 48:
        return True
    elif name == "i" and value >= 7 and value <= 5170:
        return True
    elif name == "p" and value >= 175 and value <= 304959:
        return True
    elif name == "frames" and value >= 192 and value <= 310129:
        return True
    elif name == "i_size" and value >= 11648 and value <= 90828552:
        return True
    elif name == "p_size" and value >= 33845 and value <= 768996980:
        return True
    elif name == "size" and value >= 191879 and value <= 806711069:
        return True
    elif name == "o_bitrate" and value >= 56000 and value <= 5000000:
        return True
    elif name == "o_framerate" and value >= 12 and value <= 29.97:
        return True
    elif name == "o_width" and value >= 176 and value <= 1920:
        return True
    elif name == "o_height" and value >= 144 and value <= 1080:
        return True
    elif name == "umem" and value >= 22508 and value <= 711824:
        return True
    elif name == "utime" and value >= 0.184 and value <= 224.574:
        return True
    else:
        return False

def Correct(value, name):
    if name == "duration" and value < 31.08:
        return 31.08
    elif name == "duration" and value > 25844.086:
        return 25844.086
    elif name == "width" and value < 176:
        return 176
    elif name == "width" and value > 1920:
        return 1920
    elif name == "height" and value < 144:
        return 144
    elif name == "height" and value > 1080:
        return  1080
    elif name == "bitrate" and value < 8384:
        return 8384
    elif name == "bitrate" and value > 7628466:
        return 7628466
    elif name == "framerate" and value < 5.7057524:
        return 5.0
    elif name == "framerate" and value > 1000:
        return 920
    elif name == "i" and value < 7:
        return 7
    elif name == "i" and value > 5170:
        return 5170
    elif name == "p" and value < 175:
        return 175
    elif name == "p" and value > 304959:
        return 304959
    elif name == "frames" and value < 192:
        return 192
    elif name == "frames" and value > 310129:
        return 310129
    elif name == "i_size" and value < 11648:
        return 11648
    elif name == "i_size" and value > 90828552:
        return 90828552
    elif name == "p_size" and value < 33845:
        return 33845
    elif name == "p_size" and value > 768996980:
        return 768996980
    elif name == "size" and value < 191879:
        return 191879
    elif name == "size" and value > 806711069:
        return 806711069
    elif name == "o_bitrate" and value < 56000:
        return 56000
    elif name == "o_bitrate" and value > 5000000:
        return 5000000
    elif name == "o_framerate" and value < 12:
        return 12
    elif name == "o_framerate" and value > 29.97:
        return 29.97
    elif name == "o_width" and value < 176:
        return 176
    elif name == "o_width" and value > 1920:
        return 1920
    elif name == "o_height" and value < 144:
        return 144
    elif name == "o_height" and value > 1080:
        return 1080
    elif name == "umem" and value < 22508:
        return 22508
    elif name == "umem" and value > 711824:
        return 711824
    elif name == "utime" and value < 0.184:
        return 0.184
    elif name == "utime" and value > 224.574:
        return 224.574
    else:
        return 0

index = 0
dataList = []
nameList = []
continuousData = {}
categoricalData = {}
dataEmpty = {}
dataWithoutEmpty = {}
dataWithEmpty = {}
with open('transcoding_mesurment trukumas duomenu.tsv') as tsvfile:
  reader = csv.DictReader(tsvfile, dialect='excel-tab')
  for row in reader:
        dataList.append(row)

for row in dataList:
    for names in row:
        nameList.append(names)
    break
# OneHalf = 1.5 * (quartile3CalculationReturn(dataList, "utime") - quartile1CalculationReturn(dataList, "utime"))
# print(quartile1CalculationReturn(dataList, "utime"))

for name in nameList:
    dataEmpty[name] = 0
    dataWithoutEmpty[name] = []
    dataWithEmpty[name] = []
    for row in dataList:
        try:
            f = int(row[name])
            if row[name] == "NaN" or row[name] == "NAN" or row[name] == "nan" or row[name] == " " or  row[name] == "":
                f = int("hack")
        except ValueError:
            if name in dataEmpty and name != "id" and name != "codec" and name != "o_codec":
                try:
                    f = float(row[name])
                except:
                    dataEmpty[name] += 1
                    dataWithEmpty[name].append(0)
                else:
                    if filetrCheck(float(row[name]), name):
                        dataWithoutEmpty[name].append(float(row[name]))
                        dataWithEmpty[name].append(float(row[name]))
                    else:
                        dataWithoutEmpty[name].append(float(Correct(float(row[name]), name)))
                        dataWithEmpty[name].append(float(Correct(float(row[name]), name)))

            elif (name == "id" or name == "codec" or name == "o_codec") and row[name] == "" and row[name] == "nan" and row[name] == "NaN" and row[name] == "NAN":
                dataEmpty[name] += 1
                dataWithEmpty[name].append(None)

            elif name == "id" or name == "codec" or name == "o_codec":
                dataWithoutEmpty[name].append(row[name])
                dataWithEmpty[name].append(row[name])


        else:
            if filetrCheck(float(row[name]), name):
                dataWithoutEmpty[name].append(float(row[name]))
                dataWithEmpty[name].append(float(row[name]))
            else:
                dataWithoutEmpty[name].append(float(Correct(float(row[name]), name)))
                dataWithEmpty[name].append(float(Correct(float(row[name]), name)))


def binary_cross_entropy(actual, predicted):
	sum_score = 0.0
	for i in range(len(actual)):
		sum_score += actual[i] * log(1e-15 + predicted[i])
	mean_sum_score = 1.0 / len(actual) * sum_score
	return -mean_sum_score

def calcDirivAndE(matrix, dirivative=False):
    if dirivative:
        return matrix * (1 - matrix)
    else:
        return 1/(1+np.exp(-matrix))

def normalizationFunction(list, min, max):
    data = []
    for element in list:
        data.append(((float(element) - min)/(max-min))*(1-(0))+(0))
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

iDataNormalize = normalizationFunction(dataWithEmpty['i'], min(dataWithEmpty['i']), max(dataWithEmpty['i']))
framerateDataNormalize = normalizationFunction(dataWithEmpty['framerate'], min(dataWithEmpty['framerate']), max(dataWithEmpty['framerate']))
dataZip = zip(iDataNormalize, framerateDataNormalize) # maybe use with all of this p
dataNormalized = list(dataZip)
resData = dataWithEmpty['duration']
resDataNormalized = normalizationFunction(dataWithEmpty['duration'], min(dataWithEmpty['duration']), max(dataWithEmpty['duration']))
# print(dataNormalized[0:10])


# L = len(sunSpotActivity)
# sunSpotActivityDataUsage = []
# for a in range(L-2):
#     sunSpotActivityDataUsage.append([int(sunSpotActivity[a]), int(sunSpotActivity[a+1])])
#
# answerForSunActivity = []
# answerForSunActivityGraphic = []
#
# for element in sunSpotActivity[2:]:
#     answerForSunActivity.append([element])
#     answerForSunActivityGraphic.append(element)
#
# answerForSunActivityGraphic = []
# answerForSunActivityGraphic.append([])
# answerForSunActivityGraphic.append([])
# for element in range(len(sunSpotActivityDataUsage)):
#     answerForSunActivityGraphic[0].append(sunSpotActivityDataUsage[element][0])
#     answerForSunActivityGraphic[1].append(sunSpotActivityDataUsage[element][1])
#
# # print(np.array(answerForSunActivity))
# # print(np.array(sunSpotActivityDataUsage))
#
# # draw.DrowPlot(year, sunSpotActivity)
#
# dataForTraining = sunSpotActivityDataUsage[0:200]
# dataForTrainingAnswer = answerForSunActivity[0:200]
#
# # for element in dataForTrainingAnswer[0]
#
# dataForTrainingNormalized = normalizationFunction(dataForTraining, min(sunSpotActivityDataUsage)[0], max(sunSpotActivityDataUsage)[0])
# dataNormalized = normalizationFunction(sunSpotActivityDataUsage, min(sunSpotActivityDataUsage)[0], max(sunSpotActivityDataUsage)[0])
# dataForTrainingAnswerNormalized = normalizationFunction(dataForTrainingAnswer, min(answerForSunActivity)[0], max(answerForSunActivity)[0])
#
# dataRes = []
# dataRes.append([])
# dataRes.append([])
# for element in dataNormalized:
#     dataRes[0].append(element[0])
#     dataRes[1].append(element[1])
#
# print(dataForTrainingNormalized)
# draw.DrowPlot3D(dataRes[0][0:200], dataRes[1][0:200], dataForTrainingAnswerNormalized)
#
# #
# # x = [
# #     [0, 0, 0],
# #     [1, 1, 0],
# #     [1, 0, 1],
# #     [0, 1, 0]
# #     ]
# #
# # y = [
# #     [0],
# #     [1],
# #     [1],
# #     [1]
# #     ]
# #
#
np.random.seed(1)
# #
# # syn0 = np.random.randn(2,1)-1
# # print(syn0[0])
syn0 = 2 * np.random.random((2,1)) - 1
w_initialize = syn0
bias = np.random.randn()
b_initialize = bias
lr = 0.01 # geriausias kai bias naudojamas lr yra 0.1

data = np.array(dataNormalized[0:300])
answerData = np.array(resDataNormalized[0:300])
# data = np.array(dataForTraining)
# answerData = np.array(dataForTrainingAnswer)
#

ep = 2000

for iter in range(ep): # epochs padaryti atnaujinima pagal duota skaiciu batch
    #
    # forward propagation
    l0 = np.dot(data,syn0) + bias # privalo buti atskirta
    l1 = calcDirivAndE(l0)
    # how much did we miss?
    l1_error = binary_cross_entropy(l1, answerData)
    # print(l1_error[0][0])
    print("Error:" + str(np.average(np.abs(l1_error))))
    # multiply how much we missed by the
    # slope of the sigmoid at the values in l1
    l1_delta = l1_error * calcDirivAndE(l1,True)
    # update weights
    syn0 = syn0 - lr * np.dot(data.T, l1_delta)
    bias = bias - np.sum(lr * l1_delta)


print('Neurono svoriniai koeficientai prieš apmokymą:')
print("w1 = {}".format(w_initialize[0]))
print("w2 = {}".format(w_initialize[1]))
print("b  = {}".format(b_initialize))
print('----------')
print('Neurono svoriniai koeficientai:')
print("w1 = {}".format(syn0[0]))
print("w2 = {}".format(syn0[1]))
print("b  = {}".format(bias))
#
# # print(syn0)
# # print("Output After Training:")
Tsu = np.dot(data, syn0) + bias
# # print(val)
TsuRes = calcDirivAndE(Tsu)
# # year[2:202]
TsuDeNormalized = deNormalization(TsuRes, min(dataWithEmpty['duration']), max(dataWithEmpty['duration']))
TsuGraphicData = []
for el in TsuRes:
    TsuGraphicData.append(el[0])
draw.DrawDiff(iDataNormalize[0:300], resDataNormalized[0:300], TsuGraphicData)
# print(resDataNormalized[0:300])
# Ts = np.dot(dataNormalized, syn0) + bias
# TsRes = calcDirivAndE(Ts)
# TsDeNormalized = deNormalization(TsRes, min(answerForSunActivity)[0], max(answerForSunActivity)[0])
# print(len(TsDeNormalized))
# print(len(answerForSunActivity))
#
# eVector = list()
# for real, predicted in zip(answerForSunActivity, TsDeNormalized):
#     e = real[0] - predicted[0]
#     eVector.append(e)
#
# draw.DrowPlot(year[2:], eVector)
# draw.DrowHist(eVector)
#
# predictionMSE = mse(eVector)
# predictionMAD = mad(eVector)
# print('MSE = {}'.format(predictionMSE))
# print('MAD = {}'.format(predictionMAD))
