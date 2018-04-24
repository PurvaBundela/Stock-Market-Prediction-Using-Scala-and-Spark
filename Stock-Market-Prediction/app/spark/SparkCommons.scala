package spark

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object SparkCommons {

  lazy val conf = {
    new SparkConf(false)
        .setMaster("local[*]")
    .setAppName("Stock-market")
    .set("spark.logconf","true")
  }


//  val sc = SparkContext.getOrCreate(conf)
//  val streamingContext = new StreamingContext(sc,Seconds(4))

}
