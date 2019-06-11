# MyLocation

该 app 用于定位信息采集，需要存储权限和定位权限。

数据文件存储位置位于外部存储的`locations/${date}.data.csv`。

csv 的头部信息为

```csv
time,longitude,latitude,altitude,bearing,speed,accuracy,provider
```

