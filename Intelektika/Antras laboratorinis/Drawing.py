import pandas as pd
from mpl_toolkits import mplot3d
from pandas.plotting import scatter_matrix
from pandas import DataFrame
import numpy as np
import matplotlib.pyplot as plot
import seaborn as sb


def DrowPlot(x, y):
    fig, ax = plot.subplots()
    ax.plot(x, y)
    ax.grid()
    plot.show()

def DrowPlot3D(x, y, z):
    ax = plot.axes(projection='3d')
    # ax.plot3D(x, y, z)
    zline = np.array(x)
    xline = np.array(y)
    yline = np.array(z)
    ax.plot3D(xline, yline, zline)
    plot.show()

def DrowHist(dataList, X_name, name):
    ax1 = plot.subplots(nrows = 1, ncols = 1)
    # sb.distplot(dataList, kde=True, ax = ax1, bins=20)
    # testData = [40, 10, 11, 2]
    plot.hist(dataList)
    plot.xlabel(X_name)
    plot.ylabel("frequency")
    plot.title(name)
    plot.show()


def DrowScatter(dataList, datalist2, x, y, name):
    ax1 = plot.subplots(nrows = 1, ncols = 1)
    plot.scatter(dataList, datalist2)
    plot.xlabel(x)
    plot.ylabel(y)
    plot.title(name)
    plot.show()


def DrowBarPlot(dataName, dataList, xName, name):

    # y_pos = np.arange(len(dataName))
    # print(dataName)
    # print(dataList)
    # fig, (ax1, ax2) = plot.subplots(nrows=2, ncols=1)
    plot.bar(dataName, dataList)
    plot.xlabel(xName)
    plot.ylabel("Frequency")
    plot.title(name)
    plot.show()

def DrawScatterMatrix(list1, list2, list3, list4, list5, name1, name2, name3, name4, name5):

    d = {name1:list1, name2:list2, name3:list3, name4:list4, name5:list5}
    df = pd.DataFrame(data=d)
    # print(np.random.rand(50, 4))
    # df.plot.scatter(x='a', y='b')
    # ax = df.plot.scatter(x='a', y='b', color='Black', label='Group 1')
    # df.plot.scatter(x='c', y='d', color='DarkGreen', label='Group 2', ax=ax)
    scatter_matrix(df, alpha=0.4, figsize=(6, 6), diagonal='kde')
    plot.show()


def BoxPlot(data1, data2):
    fig, (ax1, ax2) = plot.subplots(nrows=1, ncols=2)
    # da = np.random.rand(10, 1)
    # data={"pav":da}
    ax1.boxplot(data1)
    ax2.boxplot(data2)
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