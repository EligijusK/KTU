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


# input dataset
X = np.array([[0, 0, 1],
              [0, 1, 1],
              [1, 0, 1],
              [1, 1, 1]])

y = np.array([[0],
              [1],
              [1],
              [0]])

np.random.seed(1)

# randomly initialize our weights with mean 0
syn0 = 2 * np.random.random((3, 4)) - 1
syn1 = 2 * np.random.random((4, 1)) - 1

for j in range(60000):

    # Feed forward through layers 0, 1, and 2
    l0 = X
    l1 = nonlin(np.dot(l0, syn0))  # matrix multiplication and then e^x for all elements
    l2 = nonlin(np.dot(l1, syn1))  # calculating value second connection same as for first one

    # how much did we miss the target value?
    l2_error = y - l2

    if (j % 10000) == 0:
        print("Error:" + str(np.mean(np.abs(l2_error))))

    # in what direction is the target value?
    # were we really sure? if so, don't change too much.
    l2_delta = l2_error * nonlin(l2, deriv=True)

    # how much did each l1 value contribute to the l2 error (according to the weights)?
    l1_error = l2_delta.dot(syn1.T)

    # in what direction is the target l1?
    # were we really sure? if so, don't change too much.
    l1_delta = l1_error * nonlin(l1, deriv=True)

    syn1 += l1.T.dot(l2_delta)
    syn0 += l0.T.dot(l1_delta)
