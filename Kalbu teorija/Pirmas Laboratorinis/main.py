

class TelephoneInfo:
    def __init__(self, code, name, price):
        self.code = code
        self.name = name
        self.price = price
        # apsirasyti equals
        # apsirasyti str

    def priceSeconds(self):
        return round(self.price * 0.1, 2)

class TelephoneCalls:
    def __init__(self, number, time):
        self.number = number
        self.time = time
        # apsirasyti equals
        # apsirasyti str


class Data (TelephoneInfo, TelephoneCalls):
    def __init__(self, number, code, name, price, time):
        self.number = number
        self.code = code
        self.name = name
        self.price = price
        self.time = time
        # apsirasyti str pagal code kai == -1 arba nelygu

    def calcuPrice(self):
        return float(self.price) * 0.1 * float(self.time)

    def __str__(self):
        if self.code != -1:
            return "{0:15} {1:16} {2:8} {3:6} {4:6} {5:.2f}\n".format(self.number, self.name, self.code, self.time, str(self.priceSeconds()), self.calcuPrice())
        else:
            return "{0:15} {1:16} {2:8} {3:6} {4:6} {5:.2f}\n".format(self.number, self.name, "", self.time, "", float(self.price))





def dataRead():
    data = []
    file = open("test.txt", "r")
    for dataFromFile in file:
        data.append(dataFromFile)
    file.close()
    return data


def splitLine(dataFromFile):
    split = []
    listForInfo = []
    listForCalls = []
    info = False
    for i in dataFromFile:

        index = 0
        if len(split) == 2 and info:
            split = []
        elif not info:
            split = []

        for string in i:
            if string == " ":
                split.append(i[0:index])
                split.append(i[index + 1: len(i)])
                break
            index += 1
        if index == len(i):
            split.append(i.strip())

        # print("{0} {1}, {2}".format(split[0].strip(), len(i), index))
        # split = i.split(" ")
        split[0] = split[0].strip()
        if len(split) > 1 and not bool(info):
            # print(split)
            split[1] = split[1].strip()
            data = split[1].split('$')
            telephoneInfo = TelephoneInfo(split[0], data[0], float(data[1]) * 0.1)
            listForInfo.append(telephoneInfo)
        elif len(split) == 1 and split[0] == "000000" and not bool(info):
            # print(split)
            split = []
            info = True
        elif len(split) > 1 and bool(info):
            split[1] = split[1].strip()
            calls = TelephoneCalls(split[0], split[1])
            listForCalls.append(calls)
    return listForInfo, listForCalls


def calculatePrice(listOfCall, listOfInfo):
    calculatedData = []
    for calls in listOfCall:
        state = False
        for info in listOfInfo:
            if calls.number[0:len(info.code)] == info.code:
                data = Data(calls.number, calls.number[len(info.code):len(calls.number)], info.name, info.price, calls.time)
                calculatedData.append(data)
                state = True
                break
            elif calls.number[0] != "0":
                tempInfo = TelephoneInfo(calls.number, "Local", 0)
                data = Data(calls.number, tempInfo.code, tempInfo.name, tempInfo.price, calls.time)
                calculatedData.append(data)
                state = True
                break
        if not bool(state):
            data = Data(calls.number, -1, "Unknown", -1, calls.time)
            calculatedData.append(data)
    return calculatedData


def saveData(calculatedData):
    fSave = open("data.txt", "w+")
    # fSave.write("{0:14} {1:16} {2:8} {3:6} {4:6} {5:6}\n".format("1", "17", "51", "56", "62", "69"))
    for data in calculatedData:
        fSave.write(str(data))
    fSave.close()


fileData = dataRead()
infoList, callsList = splitLine(fileData)
dataList = calculatePrice(callsList, infoList)
saveData(dataList)

