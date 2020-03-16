import numpy as np

def calcDirivAndE(matrix, dirivative=False):
    if dirivative:
        return matrix * (1 - matrix)
    else:
        return 1/(1+np.exp(matrix))

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

np.random.seed(1)
syn0 = 2 * np.random.random((4, 4)) - 1
syn1 = 2 * np.random.random((4, 1)) - 1

for i in range(600000):

    # Feed forward through layers 0, 1, and 2
    l0 = np.array(x)
    l1 = calcDirivAndE(np.dot(l0, syn0))  # matrix multiplication and then e^x for all elements
    l2 = calcDirivAndE(np.dot(l1, syn1))  # calculating value second connection same as for first one

    # how much did we miss the target value?
    l2_error = y - l2

    if (i % 10000) == 0:
        print("Error:" + str(np.mean(np.abs(l2_error))))

    # in what direction is the target value?
    # were we really sure? if so, don't change too much.
    l2_delta = l2_error * calcDirivAndE(l2, True)

    # how much did each l1 value contribute to the l2 error (according to the weights)?
    l1_error = l2_delta.dot(syn1.T)

    # in what direction is the target l1?
    # were we really sure? if so, don't change too much.
    l1_delta = l1_error * calcDirivAndE(l1, True)

    syn1 += l1.T.dot(l2_delta)
    syn0 += l0.T.dot(l1_delta)

