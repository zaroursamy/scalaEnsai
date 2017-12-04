package machinelearning

import model.MDataPred
import org.apache.spark.sql.Dataset

/**
  * Created by zarour on 02/12/2017.
  */



object LinearRegression {

  def linear(dataForPred: Dataset[MDataPred]) = {

    import org.apache.spark.ml.regression.GeneralizedLinearRegression

    val glr = new GeneralizedLinearRegression()
      .setFamily("gaussian")
      .setFitIntercept(true)
      .setLink("identity")
      .setMaxIter(1000)
      .setRegParam(0.3)
      .setLabelCol("salary")


    val model = glr.fit(dataForPred)


    // Print the coefficients and intercept for generalized linear regression model
    println(s"Coefficients: ${model.coefficients}")
    println(s"Intercept: ${model.intercept}")

    // Summarize the model over the training set and print out some metrics
    val summary = model.summary
    println(s"Coefficient Standard Errors: ${summary.coefficientStandardErrors.mkString(",")}")
    println(s"T Values: ${summary.tValues.mkString(",")}")
    println(s"P Values: ${summary.pValues.mkString(",")}")
    println(s"Dispersion: ${summary.dispersion}")
    println(s"Null Deviance: ${summary.nullDeviance}")
    println(s"Residual Degree Of Freedom Null: ${summary.residualDegreeOfFreedomNull}")
    println(s"Deviance: ${summary.deviance}")
    println(s"Residual Degree Of Freedom: ${summary.residualDegreeOfFreedom}")
    println(s"AIC: ${summary.aic}")
    println("Deviance Residuals: ")
    summary.residuals().show()
  }

}
