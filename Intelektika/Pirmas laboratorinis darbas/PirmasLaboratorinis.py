import csv
from operator import itemgetter


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

    def __str__(self):
        return "{0},{1},{2},{3},{4},{5},{6},{7},{8},{9},{10}".format(self.name, self.amount, self.missing, self.cardinality, self.min, self.max, self.quartile1, self.quartile3, self.average, self.median, self.standardDeviation)

class Categorical:
    def __init__(self, name, amount, missing, cardinality, mod, modFrequency, modPercentage, mod2, modFrequency2, modPercentage2):
        self.name = name
        self.amount = amount
        self.missing = missing
        self.cardinality = cardinality
        self.mod = mod
        self.modFrequency = modFrequency
        self.modPercentage = modPercentage
        self.mod2 = mod2
        self.modFrequency2 = modFrequency2
        self.modPercentage2 = modPercentage2

    def __str__(self):
        return "{0},{1},{2},{3},{4},{5},{6},{7},{8},{9}".format(self.name, self.amount, self.missing, self.cardinality, self.mod, self.modFrequency, self.modPercentage, self.mod2, self.modFrequency2, self.modPercentage2)

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


def Deviation(datalistTmp):
    u = 0
    sum = 0

    for tmpRow in datalistTmp:
        u += tmpRow

    u = u / len(datalistTmp)

    for tmpRow in datalistTmp:
        sum += pow(tmpRow - u, 2)

    answer = pow(sum/len(datalistTmp), 0.5)
    return answer


def ContiniuosCalc(namedata, dataNoEmpty, data):
    for nameTemp in namedata:

        if nameTemp != "id" and nameTemp != "codec" and nameTemp != "o_codec":
            cardinalityTemp = set()
            for row in dataNoEmpty[nameTemp]:
                if row not in cardinalityTemp:
                    cardinalityTemp.add(row)

                data[nameTemp].average += float(row)

            sortedData = sorted(dataNoEmpty[nameTemp])

            median(sortedData, nameTemp)
            quartile1Calculation(sortedData, nameTemp)
            quartile3Calculation(sortedData, nameTemp)
            data[nameTemp].average = data[nameTemp].average / data[nameTemp].amount
            data[nameTemp].max = sortedData[len(sortedData) - 1]
            data[nameTemp].min = sortedData[0]
            data[nameTemp].cardinality = len(cardinalityTemp)
            data[nameTemp].missing = 100 / len(dataList) * dataEmpty[nameTemp]
            data[nameTemp].standardDeviation = Deviation(dataNoEmpty[nameTemp])
    return data


def CategoricalCalc(namedata, dataNoEmpty, data):
    for nameTemp in namedata:
        if nameTemp == "codec" or nameTemp == "o_codec":
            cardinalityTemp = set()
            modCount = {}
            count = 0
            for rowTemp in dataNoEmpty[nameTemp]:
                if rowTemp not in cardinalityTemp:
                    cardinalityTemp.add(rowTemp)
                    modCount[rowTemp] = 1
                    count += 1
                else:
                    modCount[rowTemp] += 1
                    count += 1


            sortMod = sorted(modCount.items(), key=itemgetter(1))

            data[nameTemp].cardinality = len(cardinalityTemp)
            data[nameTemp].mod = sortMod[len(sortMod)-1][0]
            data[nameTemp].modFrequency = sortMod[len(sortMod)-1][1]
            data[nameTemp].modPercentage = 100 / count * sortMod[len(sortMod)-1][1]
            data[nameTemp].mod2 = sortMod[len(sortMod)-2][0]
            data[nameTemp].modFrequency2 = sortMod[len(sortMod)-2][1]
            data[nameTemp].modPercentage2 = 100 / count * sortMod[len(sortMod)-2][1]

    return data



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
        elif names == "codec" or names == "o_codec":
            categoricalData[names] = Categorical(names, len(dataList), 0, 0, 0, 0, 0, 0, 0, 0)
        nameList.append(names)
    break

for name in nameList:
    dataEmpty[name] = 0
    dataWithoutEmpty[name] = []
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
                else:
                    dataWithoutEmpty[name].append(float(row[name]))

            elif (name == "id" or name == "codec" or name == "o_codec") and row[name] == "" and row[name] == "nan" and row[name] == "NaN" and row[name] == "NAN":
                dataEmpty[name] += 1
            elif name == "id" or name == "codec" or name == "o_codec":
                dataWithoutEmpty[name].append(row[name])


        else:
            dataWithoutEmpty[name].append(int(row[name]))


continuousData = ContiniuosCalc(nameList, dataWithoutEmpty, continuousData)
categoricalData = CategoricalCalc(nameList, dataWithoutEmpty, categoricalData)

with open("Result.csv", "w+", encoding="utf-8-sig", newline='') as csv_file:
    spamwriter = csv.writer(csv_file, delimiter=',')
    file = open("Result.csv", mode="a+", encoding="utf-8-sig")
    # print(nameList)
    spamwriter.writerow(csv_file)
    spamwriter.writerow(
        ["Atributo pavadiniams", "Kiekis (Eilučių sk.)", "Trūkstamos reikšmės, %", "Kardinalumas", "Minimali reikšmė",
         "Maksimali reikšmė", "1-asis kvartilis", "3-asis kvartilis", "Vidurkis", "Mediana", "Standartinis nuokrypis"])
    for nameTemp in nameList:
        if nameTemp != "id" and nameTemp != "codec" and nameTemp != "o_codec":
            file.write(str(continuousData[nameTemp]))
            file.write("\n")
    file.close()
    csv_file.close()
with open("Result.csv", "a+", encoding="utf-8-sig", newline='') as csv_file:
    spamwriter = csv.writer(csv_file, delimiter=',')
    spamwriter.writerow(csv_file)
    spamwriter.writerow(
        ["Atributo pavadiniams", "Kiekis (Eilučių sk.)", "Trūkstamos reikšmės, %", "Kardinalumas", "Moda",
         "Modos dažnumas", "Moda, %", "2-oji Moda", "2-osios Modos dažnumas", "2-oji Moda, %"])
    file = open("Result.csv", mode="a+", encoding="utf-8-sig")
    for nameTemp in nameList:
        if nameTemp != "id" and (nameTemp == "codec" or nameTemp == "o_codec"):
            file.write(str(categoricalData[nameTemp]))
            file.write("\n")

