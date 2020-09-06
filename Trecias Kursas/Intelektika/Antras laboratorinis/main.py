import numpy as np

# equals as np.dot, this is for calculating answer, output node
def dotCustom(matrix, weights):
    answ = []
    for col in range(len(matrix)):
        answ.append([])
        temp = 0
        for row in range(len(matrix[col])):
            temp += matrix[col][row] * weights[row][0]
        answ[col].append(temp)
    return answ

# sigmoid function without np array if deriv true it calculate delta(isvestine kitaip pokyti)
def nonlinWithoutNp(x, deriv=False):
    array = []
    for index in range(len(x)):
        array.append([])
        if (deriv == True):
            array[index].append(x[index][0] * (1-x[index][0]))
        else:
            array[index].append(1 / (1 + np.exp(-x[index][0])))  # e^(-element)
    return array

# sigmoid function using np array
def nonlin(x, deriv=False):
    if (deriv == True):
        return x * (1 - x)
    return 1 / (1 + np.exp(-x))

def normalizationFunction(list, min, max):
    data = []
    for element in list:
        temp = []
        for a in element:
            temp.append(((float(a) - min)/(max-min))*(1-(0))+(0))
        data.append(temp)
    return data

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


# input dataset
X = np.array([[0, 0, 1],
              [0, 1, 1],
              [1, 0, 1],
              [1, 1, 1]])

y = np.array([[0],
              [1],
              [1],
              [0]])

# np.random.seed(1)

# randomly initialize our weights with mean 0
syn0 = 2 * np.random.random((2, 200))-1
syn1 = 2 * np.random.random((200, 1))-1
bias = np.random.rand(1)
lr = 0.05

for epoch in range(200000):
    inputs = np.array(dataForTrainingNormalized)

    # feedforward step1
    XW = np.dot(inputs, syn0) + bias

    #feedforward step2
    z = nonlin(XW)


    # backpropagation step 1
    error = z - dataForTrainingAnswerNormalized

    print(error.sum())

    # backpropagation step 2
    dcost_dpred = error
    dpred_dz = nonlin(z, True)

    z_delta = dcost_dpred * dpred_dz

    inputs = inputs.T
    syn0 -= lr * np.dot(inputs, z_delta)

    for num in z_delta:
        bias -= lr * num[0]

print(np.average(syn0[0]))
print(np.average(syn0[1]))