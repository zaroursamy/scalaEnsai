
def addOnePure(x: Int): Int = x + 1

/* Effet de bord */
def addOneNonPure(x: Int): Int = {
  println("hello world") // On modifie l'extérieur
  x + 1
}

def lancerMissile(): String = {
  println("BOOM")
  /* bla bla bla, modifications de fichiers, ... */
  "BOOM"
}

def addOneNonPure2(x:Int): Int = {
  lancerMissile() // on sait pas ce qu'on fait
  println("missile lancé") // on modifie l'extérieur
  x + 1
}

val x = 4
val y = 3 + 1

val xPure = addOnePure(3) // GOOD
val xImpure = addOneNonPure(3) // BAD
val xImpure2 = addOneNonPure2(3) // BAD TOO


