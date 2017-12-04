def myComposition(f: Int => Int, x:Int) = f(f(x))

def f(x:Int) = x+1

val trois = myComposition(f, 1)


def mySuperComposition[T](f: T => T, x:T) = f(f(x))

def fInt(x:Int) = x+1
def fString(s:String) = s+" + 1 "

val superCompoInt = mySuperComposition(fInt, 1)
val superCompoString = mySuperComposition(fString, "1")

def toCompose(x: Int) = (y: Int) => x+y
val toCompose1 = toCompose(1)

val myFuckingSuperComposition = mySuperComposition(toCompose1, 1)

/* Curryfication */
def addTwoElements(x:Int)(y:Int) = x+y

def addOne = addTwoElements(1)_
val three = addOne(2)
mySuperComposition(addOne, 1)