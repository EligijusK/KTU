from matplotlib import pyplot as plot
import seaborn as sb

def DrowHist(dataList):
    fig, (ax1,ax2) = plot.subplots(nrows = 2, ncols = 1)
    sb.distplot(dataList, kde=True, ax = ax1, bins=200)
    ax1.set_xlabel("utime")
    ax1.set_ylabel("frequency")
    ax1.set_title("kazkas")
    plot.show()


def DrowScatter(dataList, datalist2):
    fig, (ax1,ax2) = plot.subplots(nrows = 2, ncols = 1)
    ax1.scatter(dataList, datalist2)
    ax1.set_xlabel("utime")
    ax1.set_ylabel("frequency")
    ax1.set_title("kazkas")
    plot.show()

def DrowBarPlot(dataList):
    fig, (ax1, ax2) = plot.subplots(nrows=2, ncols=1)
    plot.bar(dataList, 10000)
    ax1.set_xlabel("utime")
    ax1.set_ylabel("frequency")
    ax1.set_title("kazkas")
    plot.show()