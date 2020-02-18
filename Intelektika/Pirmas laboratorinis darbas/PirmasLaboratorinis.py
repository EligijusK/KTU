import re
import csv
from operator import itemgetter, attrgetter


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
            continuousData[name].median += float(tmpRow)

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
            continuousData[name].quartile1 += float(tmpRow)

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
            continuousData[name].quartile3 += float(tmpRow)

        if check is True and index == quartile3 + 2:
            continuousData[name].quartile3 = continuousData[name].quartile3 / 2

        index += 1

def deviation(datalistTmp, name):
    u = 0
    sum = 0

    for tmpRow in datalistTmp:
        u += tmpRow

    u = u / len(datalistTmp)

    for tmpRow in datalistTmp:
        sum += pow(tmpRow - u, 2)

    ats = pow((1/len(datalistTmp)) * sum, 0.5)


def myFunc(e):
  return e['year']



index = 0
dataList = []
nameList = []
continuousData = {}
categoricalData = {}
dataEmpty = {}
dataWithoutEmpty = {}
with open('transcoding_mesurment.tsv') as tsvfile:
  reader = csv.DictReader(tsvfile, dialect='excel-tab')
  for row in reader:
        dataList.append(row)


# initialization
for row in dataList:
    for names in row:
        if names != "codec" and names != "o_codec" and names != "id":
            continuousData[names] = Continuous(names, len(dataList), 0, 0, 0, 0, 0, 0, 0, 0, 0)
        elif names == "codec" and names == "o_codec":
            categoricalData[names] = Categorical(names, len(dataList), 0, 0, 0, 0, 0, 0, 0, 0)
        nameList.append(names)
    break

for name in nameList:
    dataEmpty[name] = 0
    dataWithoutEmpty[name] = []
    for row in dataList:
        try:
            f = int(row[name])
        except ValueError:
            if name in dataEmpty and name != "id" and name != "codec" and name != "o_codec":
                try:
                    f = float(row[name])
                except:
                    dataEmpty[name] += 1
                else:
                    dataWithoutEmpty[name].append(float(row[name]))

            elif name == "id" or name == "codec" or name == "o_codec" and row[name] == "" and row[name] == "nan" and row[name] == "NaN" and row[name] == "NAN":
                dataEmpty[name] += 1
        else:
            dataWithoutEmpty[name].append(int(row[name]))


for name in nameList:

    if name != "id" and name != "codec" and name != "o_codec":
        cardinalityTemp = set()
        for row in dataWithoutEmpty[name]:
            if row not in cardinalityTemp:
                cardinalityTemp.add(row)

            continuousData[name].average += float(row)

        sortedData = sorted(dataWithoutEmpty[name])

        median(sortedData, name)
        quartile1Calculation(sortedData, name)
        quartile3Calculation(sortedData, name)
        continuousData[name].average = continuousData[name].average / continuousData[name].amount
        continuousData[name].max = sortedData[len(sortedData)-1]
        continuousData[name].min = sortedData[0]
        continuousData[name].cardinality = len(cardinalityTemp)
        continuousData[name].missing = 100 / len(dataList) * dataEmpty[name]
        # print(continuousData[name].max)

for name in nameList:

    if name == "codec" and name == "o_codec":
        cardinalityTemp = set()
        modCount = []
        for row in dataWithoutEmpty[name]:
            if row not in cardinalityTemp:
                cardinalityTemp.add(row)
                modCount[name].append({row:0})
                modCount[name][row] = 0
            else:
                modCount[row] += 1

            continuousData[name].average += float(row)

        sortedData = sorted(dataWithoutEmpty[name])

        median(sortedData, name)
        quartile1Calculation(sortedData, name)
        quartile3Calculation(sortedData, name)
        continuousData[name].average = continuousData[name].average / continuousData[name].amount
        continuousData[name].cardinality = len(cardinalityTemp)
        continuousData[name].missing = 100 / len(dataList) * dataEmpty[name]