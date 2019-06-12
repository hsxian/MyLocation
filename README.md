# MyLocation

该 app 用于定位信息采集，需要存储权限和定位权限。

数据文件存储位置位于外部存储的`locations/${date}.data.csv`。

csv 的头部信息为

```csv
time,longitude,latitude,altitude,bearing,speed,accuracy,provider
```

## 关于GPS后台定位

经过实际使用，发现退出App或者手机息屏后GPS将中断连接，不在获取定位信息。在网上找了很多方法，多是关于GPS该不该息屏后使用（费电）的讨论。目前GPS本身没有这样的功能定位，其他人的方法通过系统任务调度的方式实现。几度撸码与测试后发现多是过时的实现方式（andriod版本已经有了巨大的变更）。最方便简洁的方式是在手机上安装`GPS Keeper Lite`，这是一个及其有用的App。