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

def median(datalistTmp, name):
    medianIndex = 0
    check = False

    if len(datalistTmp) % 2 == 0:
        medianIndex = int(len(datalistTmp) / 2)
        check = True
    else:
        medianIndex = round(len(datalistTmp) / 2, 0)

    medianIndex = int(medianIndex)
    # print(medianIndex)
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

def average(list):
    avg = 0
    index = 0
    for data in list:
        avg += data
        index += 1
    avg = avg / index
    return avg

def normalizationFunction(list):
    dictionary = {}
    for name in list.keys():
        if name != "id" and name != "codec" and name != "o_codec":
            minValue = min(list[name])
            maxValue = max(list[name])
            dictionary[name] = []
            for a in list[name]:
                # dictionary[name].append(((float(a) - avg) / devi))
                dictionary[name].append(((float(a) - minValue) / (maxValue - minValue))*(1-(-2)+(-2)))
        else:
            dictionary[name] = list[name]
    return  dictionary

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


# initialization
for row in dataList:
    for names in row:
        if names != "codec" and names != "o_codec" and names != "id":
            continuousData[names] = Continuous(names, len(dataList), 0, 0, 0, 0, 0, 0, 0, 0, 0)
        elif names == "codec" or names == "o_codec":
            categoricalData[names] = Categorical(names, len(dataList), 0, 0, 0, 0, 0, 0, 0, 0)
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

    for value in coundList.values():
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

def ConditionalAndContiniousBox(nameFirst, nameSecond):
    index = 0
    nameList = []
    coundList = {}
    secondCountList = {}
    filteredList = []
    print(dataWithEmpty[nameFirst])
    for a in dataWithEmpty[nameFirst]:  # tinka duration, resuliucija, bitrate
        if a not in nameList:
            nameList.append(a)
            coundList[a] = 1
            secondCountList[a] = []
            if dataWithEmpty[nameSecond][index] != None:
                secondCountList[a].append(dataWithEmpty[nameSecond][index])
            else:
                secondCountList[a].append(0)
        else:
            coundList[a] += 1
            if dataWithEmpty[nameSecond][index] != None:
                secondCountList[a].append(dataWithEmpty[nameSecond][index])
            else:
                secondCountList[a].append(0)
        index += 1

    for value in secondCountList.values():
        filteredList.append(value)
    return filteredList

def ConditionalAndContiniousHist(nameFirst, nameSecond, filterBy):
    index = 0
    nameList = []
    coundList = {}
    secondCountList = {}
    filteredList = []
    for a in dataWithEmpty[nameFirst]:  # tinka duration, resuliucija, bitrate
        if a == filterBy:
            if a not in nameList:
                nameList.append(a)
                coundList[a] = 1
                secondCountList[a] = []
                secondCountList[a].append(dataWithoutEmpty[nameSecond][index])
            else:
                coundList[a] += 1
                secondCountList[a].append(dataWithoutEmpty[nameSecond][index])
            index += 1

    for value in secondCountList.values():
        filteredList.append(value)
    return nameList, filteredList

def ByToConditionalFilterForHeat(nameFirst, nameSecond):
    index = 0
    nameList = []
    coundList = {}
    secondCountList = {}
    filteredList = []
    for a in dataWithEmpty[nameFirst]:  # tinka duration, resuliucija, bitrate

        if a not in nameList:
            nameList.append(a)
            coundList[a] = 1
            secondCountList[a] = []
            secondCountList[a].append(dataWithEmpty[nameSecond][index])
        else:
            coundList[a] += 1
            secondCountList[a].append(dataWithEmpty[nameSecond][index])
        index += 1
    maxVal = 0
    for tempName in secondCountList:
        if len(secondCountList[tempName]) > maxVal:
            maxVal = len(secondCountList[tempName])

    for temp in secondCountList:
        count = maxVal - len(secondCountList[temp])
        for i in range(count):
            secondCountList[temp].append(None)
        # print(maxVal - len(secondCountList[temp]))


    # for value in secondCountList.values():
    #     filteredList.append(value)
    return nameList, secondCountList


normalizedData = normalizationFunction(dataWithoutEmpty)

# nameReturned, listasMpeg = ByToConditionalFilter('codec', 'o_codec', 'mpeg4')
#
# nameReturned, listasVp8 = ByToConditionalFilter('codec', 'o_codec', 'vp8')
#
# nameReturned, listasFlv = ByToConditionalFilter('codec', 'o_codec', 'flv')
#
# nameReturned, listasH264 = ByToConditionalFilter('codec', 'o_codec', 'h264')

nameHeat, dictionaryHeat = ByToConditionalFilterForHeat('codec', 'utime')

# listRet = ConditionalAndContiniousBox("codec", "bitrate")
# listRet2 = ConditionalAndContiniousBox("o_codec", "utime")
# listRet3 = ConditionalAndContiniousBox("codec", "umem")


# Draw.DrowHist(dataWithoutEmpty['bitrate'], "Bitrate", "Video Bitrate Histogram")
# Draw.DrowHist(dataWithoutEmpty['framerate'], "Framerate", "Video Framerate Histogram")
# Draw.DrowHist(dataWithoutEmpty['codec'], "Codec", "Codec Histogram")
# Draw.DrowHist(dataWithoutEmpty['size'], "Size", "Video Size Histogram")
# Draw.DrowHist(dataWithoutEmpty['o_codec'], "O_Codec", "Output Codec Histogram")
# Draw.DrowHist(dataWithoutEmpty['o_bitrate'], "O_Bitrate", "Video Output Bitrate Histogram")
# Draw.DrowHist(dataWithoutEmpty['o_framerate'], "O_Framerate", "Video Output Framerate Histogram")
# Draw.DrowHist(dataWithoutEmpty['umem'], "Umem", "Used Memory Histogram")
# Draw.DrowHist(dataWithoutEmpty['utime'], "Utime", "Used Time Histogram")

# histogramos done


# Draw.DrowScatter(dataWithEmpty['i'], dataWithEmpty['utime'], "Video I Frames", "Utime", "I Video Frames And Used Time")   # padaryti width height kaip kategorini
# Draw.DrowScatter(dataWithEmpty['p_size'], dataWithEmpty['bitrate'], "P_Size", "Bitrate", "P_Size and Bitrate")   # padaryti width height kaip kategorini
# Draw.DrowScatter(dataWithEmpty['i_size'], dataWithEmpty['bitrate'], "I_Size", "Bitrate", "I_Size and Bitrate")   # padaryti width height kaip kategorini
# Draw.DrowScatter(dataWithEmpty['duration'], dataWithEmpty['utime'], "Duration", "Utime", "Video Duration And Used Time")   # padaryti width height kaip kategorini
# koreliuoja
#
# Draw.DrowScatter(dataWithEmpty['width'], dataWithEmpty['framerate'], "Width", "Framerate", "Width And Framerate")   # padaryti width height kaip kategorini
# Draw.DrowScatter(dataWithEmpty['width'], dataWithEmpty['i'], "Width", "I", "Width And I")   # padaryti width height kaip kategorini
# Nekoreliuoja
# Scatter diagramos baigtos

# Draw.DrawScatterMatrix(dataWithEmpty['width'], dataWithEmpty['umem'], dataWithEmpty['p_size'], dataWithEmpty['framerate'], dataWithEmpty['i'], "Width", "Umem", "P_Size", "Framerate", "Video I Frames") # duration paskutinis buvo bitrate gali buti   framerate su p

# Draw.DrowBarPlot(nameReturned, listasMpeg, "Codec", "Filtered Output Codec just with MPEG4")
# Draw.DrowBarPlot(nameReturned, listasVp8, "Codec", "Filtered Output Codec just with VP8")
# Draw.DrowBarPlot(nameReturned, listasFlv, "Codec", "Filtered Output Codec just with FLV")
# Draw.DrowBarPlot(nameReturned, listasH264, "Codec", "Filtered Output Codec just with H264")

# Draw.DrowBarPlot(dataWithEmpty['codec'], dataWithEmpty['bitrate'], "Codec", "Codec and Bitrate")
# Draw.DrowBarPlot(dataWithEmpty['o_codec'], dataWithEmpty['utime'], "O_Codec", "Output Codec and Utime")
# Draw.DrowBarPlot(dataWithEmpty['codec'], dataWithEmpty['umem'], "Codec", "Codec and Umem")

# nameReturn, listFilter = SimpleConditionalFilter("codec")
# Draw.DrowHist(listFilter, "Codec", "Codec")
# for name in nameReturn:
#     nameReturned, histo = ConditionalAndContiniousHist("codec", "umem", name)
#     Draw.DrowHist(histo, "Codec", "{0} And Umem Histogram".format(name.capitalize()))

# Draw.BoxPlot(dataWithEmpty['bitrate'], listRet)
# Draw.BoxPlot(dataWithEmpty['utime'], listRet2)
# Draw.BoxPlot(dataWithEmpty['umem'], listRet3)

heat = {}
for name in nameList[:14]: #14
    if name != "id" and name != "codec" and name != "o_codec":
        heat[name] = dataWithEmpty[name]
Draw.Heat(nameList, heat)

with open("Result.csv", "w+", encoding="utf-8-sig", newline='') as csv_file:
    spamwriter = csv.writer(csv_file, delimiter=',')
    spamwriter.writerow(csv_file)
    spamwriter.writerow(
        ["Atributo pavadinimas", "Kiekis (Eilučių sk.)", "Trūkstamos reikšmės, %", "Kardinalumas", "Minimali reikšmė",
         "Maksimali reikšmė", "1-asis kvartilis", "3-asis kvartilis", "Vidurkis", "Mediana", "Standartinis nuokrypis"])
    for nameTemp in nameList:
        if nameTemp != "id" and nameTemp != "codec" and nameTemp != "o_codec":
            spamwriter.writerow(continuousData[nameTemp].asList())
    csv_file.close()


with open("Result.csv", "a+", encoding="utf-8-sig", newline='') as csv_file:
    spamwriter = csv.writer(csv_file, delimiter=',')
    spamwriter.writerow(csv_file)
    spamwriter.writerow(
        ["Atributo pavadinimas", "Kiekis (Eilučių sk.)", "Trūkstamos reikšmės, %", "Kardinalumas", "Moda",
         "Modos dažnumas", "Moda, %", "2-oji Moda", "2-osios Modos dažnumas", "2-oji Moda, %"])
    for nameTemp in nameList:
        if nameTemp != "id" and (nameTemp == "codec" or nameTemp == "o_codec"):
            spamwriter.writerow(categoricalData[nameTemp].asList())
    csv_file.close()