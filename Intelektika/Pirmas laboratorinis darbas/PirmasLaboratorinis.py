import re
import csv


class Data:
    def __init__(self, ids, duration, codec, bitrate, framerate, i, p, b, frames, i_size, p_size, size, o_codec, o_framerate, umem, utime):
        ids = ids
        duration = duration
        codec = codec
        bitrate = bitrate
        framerate = framerate
        i = i
        p = p
        frames = frames
        i_size = i_size
        p_size = p_size
        size = size
        o_codec = o_codec
        o_framerate = o_framerate
        umem = umem
        utime = utime


index = 0
dataList = []

with open('transcoding_mesurment.tsv') as tsvfile:
  reader = csv.DictReader(tsvfile, dialect='excel-tab')
  for row in reader:
    print(row)
    tempData = Data(row['id'], row['duration'], row['codec'], row['bitrate'], row['framerate'], row['i'], row['p'], row['frames'], row['i_size'], row['p_size'], row['size'], row['o_codec'], row['o_bitrate'], row['o_framerate'], row['umem'], row['utime'])
    dataList.append(tempData)



