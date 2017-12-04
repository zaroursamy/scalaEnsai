def pair(): String = "c'est pair"
def impair(): String = "c'est impair"

def printIsPair(x:Int, fPair:()=>String, fImp:()=>String) = {
  if(x % 2 == 0) fPair() else fImp()
}

val pair2 = printIsPair(2, pair, impair) // impair n'est pas evalué
val pair3 = printIsPair(3, pair, impair) // pair n'est pas évalué


lazy val lazyX ={
  println("hey, it's X")
  2
}

val noLazyX = 2

