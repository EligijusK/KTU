import numpy as np
import Drawing as draw

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


def normalizationFunction(list, min, max):
    data = []
    for element in list:
        temp = []
        for a in element:
            temp.append(((float(a) - min)/(max-min))*(1-(0))+(0))
        data.append(temp)
    return data


year, sunSpotActivity = ReadFile("sunspot.txt")
L = len(sunSpotActivity)
sunSpotActivityDataUsage = []
for a in range(L-1):
    sunSpotActivityDataUsage.append([int(sunSpotActivity[a]), int(sunSpotActivity[a+1])])

answerForSunActivity = []

for element in sunSpotActivity[2:L]:
    answerForSunActivity.append([element])

# print(np.array(AnswerForSunActivity))
# print(np.array(sunSpotActivityDataUsage))
# draw.DrowPlot3D(sunSpotActivityDataUsage[0], sunSpotActivityDataUsage[1], AnswerForSunActivity)

# draw.DrowPlot(year, sunSpotActivity)

dataForTraining = sunSpotActivityDataUsage[0:200]
dataForTrainingAnswer = answerForSunActivity[0:200]

dataForTrainingNormalized = normalizationFunction(dataForTraining, min(sunSpotActivityDataUsage)[0], max(sunSpotActivityDataUsage)[0])
dataForTrainingAnswerNormalized = normalizationFunction(dataForTrainingAnswer, min(dataForTrainingAnswer)[0], max(dataForTrainingAnswer)[0])
# print(np.array(DataForTrainingAnswerNormalized))

def calcDirivAndE(matrix, dirivative=False):
    if dirivative:
        return matrix * (1 - matrix)
    else:
        return 1/(1+np.exp(-matrix))

x = [
    [0, 0, 0, 1],
    [1, 1, 0, 1],
    [1, 0, 1, 0],
    [0, 1, 0, 1]
    ]

y = [
    [0],
    [1],
    [1],
    [1]
    ]

# np.random.seed(1)
syn0 = 2 * np.random.random((2, 200))-1
syn1 = 2 * np.random.random((200, 1))-1
bias = np.random.rand(1)
lr = 0.05

for iter in range(600000):

    # forward propagation
    l0 = np.array(dataForTrainingNormalized)
    l1 = calcDirivAndE(np.dot(l0,syn0) + bias)
    # how much did we miss?
    l1_error = np.array(dataForTrainingAnswerNormalized) - l1

    # print(l1_error[0][0])
    if (iter % 10000) == 0:
        print("Error:" + str(np.mean(np.abs(l1_error))))
    # multiply how much we missed by the
    # slope of the sigmoid at the values in l1
    l1_delta = l1_error * calcDirivAndE(l1,True)

    # update weights
    syn0 -= lr * np.dot(l0.T,l1_delta)

    for num in l1_delta:
        bias -= lr * num[0]

# print("Output After Training:")
print(np.average(syn0[0]))
print(np.average(syn0[1]))
# print(l1)