import math


class TelephoneInfo:
    def __init__(self, code, name, price):
        self.code = code
        self.name = name
        self.price = price


class TelephoneCalls:
    def __init__(self, number, time):
        self.number = number
        self.time = time


class Data (TelephoneInfo, TelephoneCalls):
    def __init__(self, number, code, name, price, time):
        self.number = number
        self.code = code
        self.name = name
        self.price = price
        self.time = time
        # super().number = number
        # super().code = code
        # super().price = price
        # super().name = name
        # super().time = time



info = False
infoList = []
callsList = []
dataList = []
f = open("test.txt", "r")
split = []
for i in f.readlines():
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
        infoList.append(telephoneInfo)
    elif len(split) == 1 and split[0] == "000000" and not bool(info):
        # print(split)
        split = []
        info = True
    elif len(split) > 1 and bool(info):
        split[1] = split[1].strip()
        calls = TelephoneCalls(split[0], split[1])
        callsList.append(calls)


for calls in callsList:
    state = False
    for info in infoList:
        if calls.number[0:len(info.code)] == info.code:
            data = Data(calls.number, calls.number[len(info.code):len(calls.number)], info.name, info.price, calls.time)
            dataList.append(data)
            state = True
            break
        elif calls.number[0] != "0":
            tempInfo = TelephoneInfo(calls.number, "Local", 0)
            data = Data(calls.number, tempInfo.code, tempInfo.name, tempInfo.price, calls.time)
            dataList.append(data)
            state = True
            break
    if not bool(state):
        data = Data(calls.number, -1, "Unknown", -1, calls.time)
        dataList.append(data)


fSave = open("data.txt", "w+")
# fSave.write("{0:14} {1:16} {2:8} {3:6} {4:6} {5:6}\n".format("1", "17", "51", "56", "62", "69"))
for data in dataList:
    if data.code != -1:
        fSave.write("{0:15} {1:16} {2:8} {3:6} {4:6} {5:.2f}\n".format(data.number, data.name, data.code, data.time, str(round(float(data.price) * 0.1, 2)), float(data.price) * 0.1 * float(data.time)))
    else:
        fSave.write("{0:15} {1:16} {2:8} {3:6} {4:6} {5:.2f}\n".format(data.number, data.name, "", data.time, "", float(data.price)))
f.close()




