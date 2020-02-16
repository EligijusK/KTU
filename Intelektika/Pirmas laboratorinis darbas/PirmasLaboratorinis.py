import re
import csv


class Data:
    def __init__(self, ids, duration, codec, bitrate, framerate, i, p, b, frames, i_size, p_size, size, o_codec, o_framerate, umem, utime):
        self.ids = ids
        self.duration = duration
        self.codec = codec
        self.bitrate = bitrate
        self.framerate = framerate
        self.i = i
        self.p = p
        self.frames = frames
        self.i_size = i_size
        self.p_size = p_size
        self.size = size
        self.o_codec = o_codec
        self.o_framerate = o_framerate
        self.umem = umem
        self.utime = utime


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
continuousData = []
with open('transcoding_mesurment.tsv') as tsvfile:
  reader = csv.DictReader(tsvfile, dialect='excel-tab')
  for row in reader:
    # print(row)
    for rowData in row:
        print(rowData)

    tempData = Data(row['id'], row['duration'], row['codec'], row['bitrate'], row['framerate'], row['i'], row['p'], row['frames'], row['i_size'], row['p_size'], row['size'], row['o_codec'], row['o_bitrate'], row['o_framerate'], row['umem'], row['utime'])
    dataList.append(tempData)

