import re
import csv


class Continuous:
    def __init__(self, name, amount, missing, cardinality, min, max, quartile1, quartile3, average, median, standardDeviation):
        self.name= name;
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


index = 0
dataList = []
nameList = []
continuousData = {}
with open('transcoding_mesurment.tsv') as tsvfile:
  reader = csv.DictReader(tsvfile, dialect='excel-tab')
  for row in reader:
    # print(row)
    dataList.append(row)

medianCheck = False
medianIndex = 0
# initialization
for row in dataList:
    for names in row:
        continuousData[names] = Continuous(names, len(dataList), 0, 0, 0, 0, 0, 0, 0, 0, 0)
        nameList.append(names)

    if len(dataList) % 2 == 0:
        medianCheck = True
        medianIndex = int(len(dataList) / 2)
    else:
        medianIndex = int(len(dataList) / 2)
        medianIndex += 1
    break

for name in nameList:
    maxVal = 0.0
    minVal = 0.0
    index = 0

    if name != "id" and name != "codec" and name != "o_codec":
        cardinalityTemp = []
        for row in dataList:
            if not cardinalityTemp.__contains__(row[name]):
                cardinalityTemp.append(row[name])

            if maxVal < float(row[name]):
                maxVal = float(row[name])

            if minVal > float(row[name]):
                minVal = float(row[name])

            continuousData[name].average += float(row[name])

            if medianCheck is True and medianIndex <= index <= medianIndex + 1:
                continuousData[name].median += float(row[name])

            if medianCheck is True and index > medianIndex + 1:
                continuousData[name].median = continuousData[name].median / 2

            index += 1

        continuousData[name].average = continuousData[name].average / continuousData[name].amount
        continuousData[name].max = maxVal
        continuousData[name].min = minVal
        continuousData[name].cardinality = len(cardinalityTemp)
        

