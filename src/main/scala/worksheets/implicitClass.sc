object Implicit{

  implicit class RichString(str:String){
    def pluriel = if(str.endsWith("s")) str else str+"s"
  }

  implicit class RichInt(i:Int){
    def addOne = i+1
  }
}

import Implicit._
"ensaien".pluriel
10.addOne

