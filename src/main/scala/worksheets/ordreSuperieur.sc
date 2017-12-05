def myComposition(f: Int => Int, x:Int): Int = f(f(x))

def f(x:Int): Int = x+1

val trois: Int = myComposition(f, 1)


def mySuperComposition[T](f: T => T, x:T): T = f(f(x))

def fInt(x:Int) = x+1
def fString(s:String) = s+" + 1 "

val superCompoInt = mySuperComposition(fInt, 1)
val superCompoString = mySuperComposition(fString, "1")

def toCompose(x: Int) = (y: Int) => x+y
val toCompose1 = toCompose(1)
def toSuperCompose(x:Int, op: (Int, Int)=>Int) = (y:Int) => op(x,y)
val superCompose1 = toSuperCompose(1, (x,y)=>x-y)
superCompose1(1)


val myFuckingSuperComposition = mySuperComposition(toCompose1, 1)

/* Curryfication */
def addTwoElements(x:Int)(y:Int) = x+y

def addOne = addTwoElements(1)_
val three = addOne(2)
mySuperComposition(addOne, 1)