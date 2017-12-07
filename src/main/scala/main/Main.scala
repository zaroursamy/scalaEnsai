package main

import model.{MData, MDataPred, SData}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import scala.reflect.ClassTag

object Main {

  val sparkSession = SparkSession
    .builder()
    .appName("profesors")
    .master("local[*]")
    .getOrCreate()

  def main(args: Array[String]): Unit = {

    /* read brut data */
    val newNames = Seq("id", "rank", "discipline", "yearsPhd", "experience", "sex", "salary")
    val df: DataFrame = sparkSession
      .read
      .options(Map("header" -> "true", "delimiter" -> ","))
      .csv("src/main/resources/data.csv")
      .toDF(newNames: _*)

    /* read referential */
    val disciplineRdd = sparkSession
      .read
      .options(Map("header" -> "true", "delimiter" -> ";"))
      .csv("src/main/resources/disciplineLabel.csv")

    val refDiscipline: Broadcast[Map[String, String]] = sparkSession.sparkContext.broadcast(
      disciplineRdd
        .collect()
        .map(r => r.getAs[String]("id") -> r.getAs[String]("label"))
        .toMap
    )

    import sparkSession.implicits._
    val sData: Dataset[SData] = df.as[SData]

    val mData: RDD[MData] = sData.map(MData.fromSource).rdd

    val rdd: RDD[MData] = mData.map { data =>
      data.copy(discipline = refDiscipline.value.getOrElse(data.discipline, "Autre"))
    }

    val rddCogroup: RDD[MData] = mData
      .keyBy(_.discipline)
      .cogroup(disciplineRdd.rdd.keyBy(_.getAs[String]("id")))
      .flatMap{c =>
        c._2._1.flatMap{it =>
          c._2._2.headOption match {
            case Some(r) => Some(it.copy(discipline = r.getAs[String]("label")))
            case _ => None
          }
        }
      }

    val rddLeftOuter: RDD[MData] = mData.keyBy(_.discipline)
      .leftOuterJoin(disciplineRdd.rdd.keyBy(_.getAs[String]("id")))
      .map(t => t._2._1.copy(discipline = t._2._2.map(_.getAs[String]("label")).getOrElse("")))

    rdd.toDS.show()

    /* salaire moyen */
    println("salaire moyen: " + rdd.map(_.salary).mean())
    println("****")


    import utils.Utils.StatRDD

    println("salaire moyen par sex")
    rdd.meanByKey[String]((m: MData) => m.sex, (m: MData) => m.salary).foreach(println)
    println("****")

    println("experience moyenne par sex")
    rdd.meanByKey[String]((m: MData) => m.sex, (m: MData) => m.experience).foreach(println)
    println("****")

    println("salaire moyen par discipline")
    rdd.meanByKey[String]((m: MData) => m.discipline, (m: MData) => m.salary).foreach(println)
    println("****")

    println("salaire moyen par type de prof")
    rdd.meanByKey[String]((m: MData) => m.rank, (m: MData) => m.salary).foreach(println)
    println("****")


    def sizeByKey[T, K: ClassTag](rdd: RDD[T], f: T => K) = rdd
      .groupBy(f)
      .map({ case (k, v) => k -> v.size })

    println("Nombre de professeurs par discipline")
    sizeByKey(rdd, (m: MData) => m.discipline).foreach(println)
    println("****")

    println("Nombre de professeurs par discipline et par rank")
    sizeByKey(rdd, (m: MData) => (m.discipline, m.rank)).foreach(println)
    println("****")

    println("Salaire par discipline et rank")
    rdd.meanByKey((m: MData) => (m.discipline, m.rank), (m: MData) => m.salary).foreach(println)
    println("****")

    println("Salaire moyen par sex et discipline")
    rdd.meanByKey((m: MData) => (m.discipline, m.rank), (m: MData) => m.salary).foreach(println)


    /*
        println("regression linéaire")
        val dataPred = rdd.map(MDataPred.fromMData).toDS
        import machinelearning.LinearRegression
        LinearRegression.linear(dataPred)*/

  }
}
