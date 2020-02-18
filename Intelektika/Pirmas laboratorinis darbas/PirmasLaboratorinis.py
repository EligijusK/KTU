import re
import csv


class Continuous:
    def __init__(self, name, amount, missing, cardinality, min, max, quartile1, quartile3, average, median, standardDeviation):
        self.name = name
        self.amount = amount
        self.missing = missing
        self.cardinality = cardinality
        self.min = min
        self.max = max
        self.quartile1 = quartile1
        self.quartile3 = quartile3
        self.average = average
        self.median = median
        self.standardDeviation = standardDeviation


class Categorical:
    def __init__(self, name, amount, missing, cardinality, mod, modFrequency, modPercentage, mod2, modFrequency2, modPercentage2):
        self.name = name
        self.amount = amount
        self.missing = missing
        self.cardinality = cardinality
        self.mod = mod
        self.modFrequency = modFrequency
        self.modPercentage = modPercentage
        self.modFrequency2 = modFrequency2
        self.modPercentage2 = modPercentage2


def median(datalistTmp, name):
    medianIndex = 0
    check = False
    index = 0
    if len(datalistTmp) % 2 == 0:
        medianIndex = int(len(datalistTmp) / 2)
        check = True
    else:
        medianIndex = round(len(datalistTmp) / 2, 0)

    for tmpRow in datalistTmp:
        if check is True and medianIndex <= index <= medianIndex + 1:
            continuousData[name].median += float(tmpRow[name])

        if check is True and index == medianIndex + 2:
            continuousData[name].median = continuousData[name].median / 2
        index += 1


def quartile1Calculation(datalistTmp, name):
    quartile1 = 0
    medianIndex = 0
    index = 0
    check = False
    if len(datalistTmp) % 2 == 0:
        check = True
        medianIndex = int(len(datalistTmp) / 2)
        quartile1 = medianIndex / 2
    else:
        medianIndex = round(len(datalistTmp) / 2, 0)
        quartile1 = round(medianIndex / 2, 0)

    for tmpRow in datalistTmp:
        if check is True and quartile1 <= index <= quartile1 + 1:
            continuousData[name].quartile1 += float(tmpRow[name])

        if check is True and index == quartile1 + 2:
            continuousData[name].quartile1 = continuousData[name].quartile1 / 2

        index += 1


def quartile3Calculation(datalistTmp, name):
    quartile1 = 0
    medianIndex = 0
    index = 0
    check = False
    if len(datalistTmp) % 2 == 0:
        check = True
        medianIndex = int(len(datalistTmp) / 2)
        quartile3 = medianIndex + int(medianIndex / 2)
    else:
        medianIndex = round(len(datalistTmp) / 2, 0)
        quartile3 = medianIndex + int(medianIndex / 2)

    for tmpRow in datalistTmp:
        if check is True and quartile3 <= index <= quartile3 + 1:
            continuousData[name].quartile3 += float(tmpRow[name])

        if check is True and index == quartile3 + 2:
            continuousData[name].quartile3 = continuousData[name].quartile3 / 2

        index += 1

def deviation(datalistTmp, name):
    u = 0
    sum = 0

    for tmpRow in datalistTmp:
        u += tmpRow[name]

    u = u / len(datalistTmp)

    for tmpRow in datalistTmp:
        sum += pow(tmpRow[name] - u, 2)

    ats = pow((1/len(datalistTmp)) * sum, 0.5)






index = 0
dataList = []
nameList = []
continuousData = {}
with open('transcoding_mesurment.tsv') as tsvfile:
  reader = csv.DictReader(tsvfile, dialect='excel-tab')
  for row in reader:
    # print(row)
    dataList.append(row)

# initialization
for row in dataList:
    for names in row:
        continuousData[names] = Continuous(names, len(dataList), 0, 0, 0, 0, 0, 0, 0, 0, 0)
        nameList.append(names)
    break

for name in nameList:
    maxVal = 0.0
    minVal = 0.0

    if name != "id" and name != "codec" and name != "o_codec":
        cardinalityTemp = set()
        for row in dataList:
            if row[name] not in cardinalityTemp:
                cardinalityTemp.add(row[name])

            if maxVal < float(row[name]):
                maxVal = float(row[name])

            if minVal > float(row[name]):
                minVal = float(row[name])

            continuousData[name].average += float(row[name])

        median(dataList, name)
        quartile1Calculation(dataList, name)
        quartile3Calculation(dataList, name)

        continuousData[name].average = continuousData[name].average / continuousData[name].amount
        continuousData[name].max = maxVal
        continuousData[name].min = minVal
        continuousData[name].cardinality = len(cardinalityTemp)