#encoding=utf-8
import pylab as pl
def myPlot(x,y):
    pl.plot(x, y,'o')
    pl.title("你好！");
    pl.show()

# 1+2
a=[1,2,3,4,5]
b=[2,4,6,8,10]
myPlot(a,b)