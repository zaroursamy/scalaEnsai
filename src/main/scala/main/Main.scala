package main

import model.{MData, MDataPred, SData}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

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
    val refDiscipline: Broadcast[Map[String, String]] = sparkSession.sparkContext.broadcast(
      sparkSession
        .read
        .options(Map("header" -> "true", "delimiter" -> ";"))
        .csv("src/main/resources/disciplineLabel.csv")
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

    rdd.toDS.show()

    /* salaire moyen */
    println("salaire moyen: " + rdd.map(_.salary).mean())
    println("****")
    /* salaire moyen par sex */
    import utils.Utils.StatRDD
    rdd.meanSalary[String]((m: MData) => m.sex, (m: MData) => m.salary).foreach(println)
    println("****")

    /* experience moyenne par sex */
    rdd.meanSalary[String]((m: MData) => m.sex, (m: MData) => m.experience).foreach(println)
    println("****")

    /* salaire moyen par discipline */
    rdd.meanSalary[String]((m: MData) => m.discipline, (m: MData) => m.salary).foreach(println)
    println("****")

    /* salaire moyen par type de prof */
    rdd.meanSalary[String]((m: MData) => m.rank, (m: MData) => m.salary).foreach(println)
    println("****")


    val dataPred = rdd.map(MDataPred.fromMData).toDS
    import machinelearning.LinearRegression
    LinearRegression.linear(dataPred)

  }
}
