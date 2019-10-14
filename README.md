> 官方的HBase不支持DataFrame空值的处理，手动修改相关源码处理

修改内容如下：

1 遇到null后，直接跳过addColumn
```scala
val put = timestamp.fold(new Put(rBytes))(new Put(rBytes, _))
  colsIdxedFields.foreach { case (x, y) =>
    // 当value不为null时，才加入到更新Putter里面
    if (row(x) != null){
      put.addColumn(
        coder.toBytes(y.cf),
        coder.toBytes(y.col),
        SHCDataTypeFactory.create(y).toBytes(row(x)))
    }

  }
```

2 遍历时，如果某一行全部为Null，进行filter之后再更新

```scala
rdd.mapPartitions(iter => {
      SHCCredentialsManager.processShcToken(serializedToken)
      iter.map(convertToPut)
    }).filter(t2 => {
      // 当全部值为null时，对应的二元组family个数为0，需要过滤掉，不然会报错。
      t2._2.numFamilies()>0
    }).saveAsNewAPIHadoopDataset(jobConfig)
```

重新编译打包即可。