package org.apache.spark.sql

import java.time.LocalDate

import org.apache.spark.sql.execution.datasources.hbase.HBaseTableCatalog

object DataFrameNullTypeTest {
  def main(args: Array[String]) {

    System.setProperty("hive.metastore.uris", "thrift://hnode1:9083")

    val spark = SparkSession.builder()
      .master("local")
      .appName("tttt")
      .getOrCreate()

    val df1 = spark.createDataFrame(Seq(
      Bean("1", "1zhangsan", null),
      Bean("2", "1lisi12321321321", null),
      Bean("4", null, "name22222"),
      Bean("6", null, null)
    ))

    df1.show()

    df1.write
      .mode(SaveMode.Overwrite)
      .options(Map(HBaseTableCatalog.tableCatalog ->
        s"""
           |{
           |  "table":{
           |    "namespace":"test",
           |    "name":"xhl_test"
           |  },
           |  "rowkey":"id",
           |  "columns":{
           |    "id":{
           |      "cf":"rowkey",
           |      "col":"id",
           |      "type":"string"
           |    },
           |    "name":{
           |      "cf":"t",
           |      "col":"name",
           |      "type":"string"
           |    },
           |    "name2":{
           |      "cf":"t",
           |      "col":"name2",
           |      "type":"string"
           |    }
           |  }
           |}
           """.stripMargin))
      .format("org.apache.spark.sql.execution.datasources.hbase")
      .save()



  }

  case class Bean(id: String, name: String, name2: String)
}
