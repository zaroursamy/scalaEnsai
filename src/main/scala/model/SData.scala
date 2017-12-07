package model

import org.apache.spark.ml.linalg.Vectors

/**
  * Created by zarour on 02/12/2017.
  */
case class SData(id: String,
                 rank: String,
                 discipline: String,
                 yearsPhd: String,
                 experience: String,
                 sex: String,
                 salary: String)

case class MData(id: String,
                 rank: String,
                 discipline: String,
                 yearsPhd: Double,
                 experience: Double,
                 sex: String,
                 salary: Double
                ) {
  def rankDouble = discipline match {
    case "Prof" => 2
    case "AssocProf" => 1
    case _ => 0
  }

  def sexDouble = sex match {
    case "Male" => 1
    case _ => 0
  }

  def disciplineDouble = discipline match {
    case "applied" => 1
    case _ => 0
  }
}

object MData {

  def fromSource(sData: SData) = {

    import sData._
    MData(
      id,
      rank,
      discipline,
      yearsPhd.toDouble,
      experience.toDouble,
      sex,
      salary.toDouble
    )
  }
}


case class MDataPred(salary: Double, features: org.apache.spark.ml.linalg.Vector)

object MDataPred {
  def fromMData(mData: MData) = MDataPred(
    salary = mData.salary,
    features = Vectors.dense(Array(mData.experience, mData.disciplineDouble, mData.sexDouble))
  )
}