import pandas as pd
from pandas.plotting import scatter_matrix
from pandas import DataFrame
import numpy as np
import matplotlib.pyplot as plot
import seaborn as sb


def DrowHist(dataList):
    ax1 = plot.subplots(nrows = 1, ncols = 1)
    # sb.distplot(dataList, kde=True, ax = ax1, bins=20)
    plot.hist(dataList)
    # plot.set_xlabel("utime")
    # plot.set_ylabel("frequency")
    # plot.set_title("kazkas")
    plot.show()


def DrowScatter(dataList, datalist2):
    ax1 = plot.subplots(nrows = 1, ncols = 1)
    plot.scatter(dataList, datalist2)
    # plot.set_xlabel("utime")
    # plot.set_ylabel("frequency")
    # plot.set_title("kazkas")
    plot.show()


def DrowBarPlot(dataName, dataList):

    # y_pos = np.arange(len(dataName))
    # print(dataName)
    # print(dataList)
    # fig, (ax1, ax2) = plot.subplots(nrows=2, ncols=1)
    plot.bar(dataName, dataList)
    # plot.xticks(y_pos, dataName)
    # plot.set_xlabel("utime")
    # plot.set_ylabel("frequency")
    # plot.set_title("kazkas")
    plot.show()

def DrawScatterMatrix(list1, list2, list3, list4, list5):

    d = {'list1':list1, 'list2':list2, 'list3':list3, 'list4':list4, 'list5':list5}
    df = pd.DataFrame(data=d)
    # print(np.random.rand(50, 4))
    # df.plot.scatter(x='a', y='b')
    # ax = df.plot.scatter(x='a', y='b', color='Black', label='Group 1')
    # df.plot.scatter(x='c', y='d', color='DarkGreen', label='Group 2', ax=ax)
    scatter_matrix(df, alpha=0.4, figsize=(6, 6), diagonal='kde')
    plot.show()


def BoxPlot(data):
    fig, (ax1, ax2) = plot.subplots(nrows=1, ncols=2)
    # da = np.random.rand(10, 1)
    # data={"pav":da}
    ax1.boxplot(data)
    # df = pd.DataFrame(np.random.rand(10, 1))
    # df['X'] = pd.Series(['A', 'A', 'A', 'A', 'A', 'B', 'B', 'B', 'B', 'B'])
    # df['X'] = pd.Series(['A', 'A', 'A', 'A', 'A', 'B', 'B', 'B', 'B', 'B'])
    # df['Y'] = pd.Series(['A', 'B', 'B', 'A', 'B', 'B', 'B', 'A', 'A', 'B'])
    plot.figure()
    # bp = df.boxplot(column=['Col1', 'Col2'], by=['X', 'Y'])
    plot.show()


def Heat(nameList, Data):

    # Data = {'A': [45, 37, 42, 35, 39],
    #         'B': [38, 31, 26, 28, 33],
    #         'C': [10, 15, 17, 21, 12]
    #         }

    # print(Data)
    df = DataFrame(Data, columns=nameList)
    corrMatrix = df.corr()
    sb.heatmap(corrMatrix, annot=True)
    plot.show()