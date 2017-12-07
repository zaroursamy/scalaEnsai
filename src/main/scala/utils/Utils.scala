package utils

import model.MData
import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

object Utils {

  implicit class StatSeq(it:Seq[Double]){
    def density(factor:Double => String): Map[String, Int] = it
      .groupBy(factor)
      .map({case(k, iterable) => k->iterable.size})
    def mean:Double = it.sum / it.size
  }

  implicit class StatRDD(rdd:RDD[MData]){
    def meanByKey[K : ClassTag](f:MData => K, g:MData => Double): RDD[(K, Double)] = rdd
      .groupBy(f)
      .map{t =>
        t._1 -> t._2.toSeq.map(g).mean}
  }

}
