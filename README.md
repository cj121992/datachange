# 数据变更流中间件,参考databus，以avro schema文件作为传输标准，将源数据(主要为数据库变更)为二进制文件存储到中继器(mq或其他实现)，同步到其他同构或者异构数据存储介质中(db,es,或大数据分析用)

@(示例笔记本)[java|es|mq]

## 架构图
![](https://raw.githubusercontent.com/cj121992/datachange/master/resource/%E6%95%B0%E6%8D%AE%E5%8F%98%E6%9B%B4%E6%B5%81%E6%9E%B6%E6%9E%84.png)

## 应用图
![](https://raw.githubusercontent.com/cj121992/datachange/master/resource/clipboard.png)

## 流程图
![](https://raw.githubusercontent.com/cj121992/datachange/master/resource/ES%E6%A8%A1%E5%9E%8B%E5%8F%98%E6%9B%B4%E6%B5%81%E7%A8%8B%E5%9B%BE.png)


## 转化标准
```
JAVA         ->  AVRO

基础类型
int, Integer ->  int
String       ->  string
long, Long   ->  long
double, Double -> double
float,Float        ->  float
boolean,Boolean    ->  boolean
byte,Byte          ->  byte


时间类型
timestamp,Date    ->  13位时间戳long(区别开long,在meta中添加dbType的说明)

数据库dml操作类型
drud                  ->  string(值为insert,update,delete)
```
&ensp;&ensp;对于数据库变更的schema,需要默认有op_type这个filed标识操作类型,type为```string```,值对应```"insert"```,```"update"```,```"delete"```

&ensp;&ensp;示例：
```
{
  "name" : "DemoInfo_V1",
  "doc" : "Auto-generated Avro schema for DemoInfo. Generated at Nov 15, 2017 09:07:05 PM PST",
  "type" : "record",
  "meta" : "dbFieldName=demo_info;pk=demo_id;",
  "namespace" : "com.melot.data.example",
  "fields" : [ {
    "name" : "demo_id",
    "type" : [ "int", "null" ],
    "meta" : "dbFieldName=demo_id;dbFieldPosition=0;"
  }, {
    "name" : "nickname",
    "type" : [ "string", "null" ],
    "meta" : "dbFieldName=nickname;dbFieldPosition=1;"
  }, {
    "name" : "join_time",
    "type" : [ "long", "null" ],
    "meta" : "dbFieldName=join_time;dbFieldPosition=2;"
  }, 
  { "name" : "create_time",
    "type" : [ "long", "null" ],
    "meta" : "dbFieldName=create_time;dbFieldPosition=3;dbType=timestamp;"}
  {
    "name" : "op_type",
    "type" : [ "string", "null" ],
    "meta" : "dbFieldName=op_type;dbFieldPosition=4;"
  }
  ]
}
```

tips：
1.在meta指定数据库的主键.pk=demo_id
