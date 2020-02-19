from matplotlib import pyplot as plot
import seaborn as sb

def Drow(dataList):
    fig, (ax1,ax2) = plot.subplots(nrows = 2, ncols = 1)
    sb.distplot(dataList, kde=True, ax = ax1, bins=200)
    ax1.set_xlabel("utime")
    ax1.set_ylabel("frequency")
    ax1.set_title("kazkas")
    plot.show()