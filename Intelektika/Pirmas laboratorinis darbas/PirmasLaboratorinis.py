import csv
from operator import itemgetter
import Drawing as Draw


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

    def asList(self):
        return [self.name, self.amount, self.missing, self.cardinality, self.min, self.max, self.quartile1, self.quartile3, self.average, self.median, self.standardDeviation]

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

    def asList(self):
        return [self.name, self.amount, self.missing, self.cardinality, self.mod, self.modFrequency, self.modPercentage, self.mod2, self.modFrequency2, self.modPercentage2]

    def __str__(self):
        return "{0},{1},{2},{3},{4},{5},{6},{7},{8},{9}".format(self.name, self.amount, self.missing, self.cardinality, self.mod, self.modFrequency, self.modPercentage, self.mod2, self.modFrequency2, self.modPercentage2)

def median(datalistTmp, name):
    medianIndex = 0
    check = False

    if len(datalistTmp) % 2 == 0:
        medianIndex = int(len(datalistTmp) / 2)
        check = True
    else:
        medianIndex = round(len(datalistTmp) / 2, 0)

    medianIndex = int(medianIndex)
    if check is True:
        continuousData[name].median += datalistTmp[medianIndex - 1]
        continuousData[name].median += datalistTmp[medianIndex]
        continuousData[name].median = continuousData[name].median / 2
    elif check is False:
        continuousData[name].median += float(datalistTmp[medianIndex - 1])



def quartile1Calculation(datalistTmp, name):
    quartile1 = 0
    medianIndex = 0

    check = False
    if len(datalistTmp) % 2 == 0:
        check = True
        medianIndex = int(len(datalistTmp) / 2)
        quartile1 = medianIndex / 2
    else:
        medianIndex = round(len(datalistTmp) / 2, 0)
        quartile1 = round(medianIndex / 2, 0)

    quartile1 = int(quartile1)
    if check is True:
        continuousData[name].quartile1 += datalistTmp[quartile1]
        continuousData[name].quartile1 += datalistTmp[quartile1-1]
        continuousData[name].quartile1 = continuousData[name].quartile1 / 2
    elif check is False:
        continuousData[name].quartile1 += datalistTmp[quartile1 - 1]


def quartile3Calculation(datalistTmp, name):
    quartile1 = 0
    medianIndex = 0

    check = False
    if len(datalistTmp) % 2 == 0:
        check = True
        medianIndex = int(len(datalistTmp) / 2)
        quartile3 = medianIndex + int(medianIndex / 2)
    else:
        medianIndex = round(len(datalistTmp) / 2, 0)
        quartile3 = medianIndex + int(medianIndex / 2)

    quartile3 = int(quartile3)
    if check is True:
        continuousData[name].quartile3 += datalistTmp[quartile3]
        continuousData[name].quartile3 += datalistTmp[quartile3 - 1]
        continuousData[name].quartile3 = continuousData[name].quartile3 / 2
    elif check is False:
        continuousData[name].quartile3 += datalistTmp[quartile3 - 1]


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
dataWithEmpty = {}
with open('transcoding_mesurment TST.tsv') as tsvfile:
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
                    dataWithEmpty[name].append(None)
                else:
                    dataWithoutEmpty[name].append(float(row[name]))
                    dataWithEmpty[name].append(float(row[name]))

            elif (name == "id" or name == "codec" or name == "o_codec") and row[name] == "" and row[name] == "nan" and row[name] == "NaN" and row[name] == "NAN":
                dataEmpty[name] += 1
                dataWithEmpty[name].append(None)
            elif name == "id" or name == "codec" or name == "o_codec":
                dataWithoutEmpty[name].append(row[name])
                dataWithEmpty[name].append(row[name])


        else:
            dataWithoutEmpty[name].append(int(row[name]))
            dataWithEmpty[name].append(int(row[name]))


continuousData = ContiniuosCalc(nameList, dataWithoutEmpty, continuousData)
categoricalData = CategoricalCalc(nameList, dataWithoutEmpty, categoricalData)


def SimpleConditionalFilter(name):
    nameList = []
    coundList = {}
    filteredList = []
    for a in dataWithEmpty[name]:  # tinka duration, resuliucija, bitrate
        if a not in nameList:
            nameList.append(a)
            coundList[a] = 1
            # secondlist[a] = dataWithoutEmpty['utime'][index]
            # index += 1

            # countCodec[a] += 1
        else:
            coundList[a] += 1
            # secondlist[a] += dataWithoutEmpty['utime'][index]
            # index += 1

    for value in filteredList.values():
        filteredList.append(value)

    return nameList, filteredList


def ByToConditionalFilter(nameFirst, nameSecond, filterBy):
    index = 0
    nameList = []
    coundList = {}
    secondCountList = {}
    filteredList = []
    for a in dataWithEmpty[nameFirst]:  # tinka duration, resuliucija, bitrate

        if a not in nameList and filterBy == dataWithoutEmpty[nameSecond][index]:
            nameList.append(a)
            coundList[a] = 1
            secondCountList[a] = 1
        elif filterBy == dataWithoutEmpty[nameSecond][index]:
            coundList[a] += 1
            secondCountList[a] += 1
        index += 1

    for value in secondCountList.values():
        filteredList.append(value)
    return nameList, filteredList


nameReturned, listas = ByToConditionalFilter('codec', 'o_codec', 'mpeg4')

print(listas)

# Draw.DrowHist(dataWithoutEmpty['codec'])
# Draw.DrowHist(dataWithoutEmpty['o_codec'])
# Draw.DrowHist(dataWithoutEmpty['umem'])
# Draw.DrowHist(dataWithoutEmpty['utime'])
# histogramos done


# Draw.DrowScatter(dataWithEmpty['i'], dataWithEmpty['o_bitrate'])   # padaryti width height kaip kategorini
# Draw.DrawScatterMatrix(dataWithoutEmpty['utime'], dataWithoutEmpty['umem'], dataWithoutEmpty['p_size'], dataWithoutEmpty['i'], dataWithoutEmpty['framerate']) # duration paskutinis buvo bit rategali buti   framerate su p
# Draw.DrowBarPlot(nameCodec, listas)
# Draw.DrowBarPlot(nameCodec, atnrasListas)
# Draw.DrowBarPlot(nameReturned, listas)
Draw.BoxPlot(listas)
# Draw.Heat()
with open("Result.csv", "w+", encoding="utf-8-sig", newline='') as csv_file:
    spamwriter = csv.writer(csv_file, delimiter=',')
    spamwriter.writerow(csv_file)
    spamwriter.writerow(
        ["Atributo pavadiniams", "Kiekis (Eilučių sk.)", "Trūkstamos reikšmės, %", "Kardinalumas", "Minimali reikšmė",
         "Maksimali reikšmė", "1-asis kvartilis", "3-asis kvartilis", "Vidurkis", "Mediana", "Standartinis nuokrypis"])
    for nameTemp in nameList:
        if nameTemp != "id" and nameTemp != "codec" and nameTemp != "o_codec":
            spamwriter.writerow(continuousData[nameTemp].asList())
    csv_file.close()


with open("Result.csv", "a+", encoding="utf-8-sig", newline='') as csv_file:
    spamwriter = csv.writer(csv_file, delimiter=',')
    spamwriter.writerow(csv_file)
    spamwriter.writerow(
        ["Atributo pavadiniams", "Kiekis (Eilučių sk.)", "Trūkstamos reikšmės, %", "Kardinalumas", "Moda",
         "Modos dažnumas", "Moda, %", "2-oji Moda", "2-osios Modos dažnumas", "2-oji Moda, %"])
    for nameTemp in nameList:
        if nameTemp != "id" and (nameTemp == "codec" or nameTemp == "o_codec"):
            spamwriter.writerow(categoricalData[nameTemp].asList())
    csv_file.close()