/* Set */
val uniqueCol = Set(9, 1, 2, 1, 2, 3, 2, 5)

/* Seq */
val seq = Seq(1, 2, 3)


/* Array */
val arr = Array(1, 2, 3)
arr(0)


/* List */
val list = List("a", "b", "c","d","e")
val list2 = "a" :: "b" :: "c" :: Nil
list.head
list.tail
//Nil.head


/* Exo: reverse une liste */
def reverse[T](list:List[T]): List[T] = list match {
  case Nil => Nil
  case x::Nil => List(x)
  case x::xs => reverse(xs) ++ List(x)
}

val reversedString = reverse(list)
val reversedNil = reverse(Nil)
val reversedInt = reverse(seq.toList)

// stream
val stream = Stream(1,2,3)
val streamOneToHundred: Seq[Int] = Stream.fill(10)(scala.util.Random.nextInt())

// tuples
val unTuple = ("a", 1, List(1, 2))
unTuple._3

/* Map */
val myMap = Map(1 -> "one", 2 -> "two", 3 -> "three")
myMap.get(1)
myMap.get(9)
val myNewMap = myMap + (4 -> "four")
myNewMap
val oneToHundred: Seq[Int] = 1 to 100


// map
val seqPlusOne = seq.map(_+1)
val seqToString = seq.map(s => "numero "+s.toString)

import java.util.UUID

import scala.util.Random
case class Person(name:String, age:Int, uuid:String = UUID.randomUUID().toString)
def generateAge(): Int = (Random.nextGaussian()+23).toInt
def choose[T](xs:Seq[T]): T = xs.apply(Random.nextInt(xs.size))
def generateName() = choose(Seq("toto","tata","titi"))
val person:Seq[Person] = oneToHundred.map(_=>Person(generateName(), generateAge()))

/* choisir les personnes qui ont 23 ans et qui ont un prénom masculin*/
def isMasculin(str:String) = str == "toto"
val filterdPersonn = person.filter({case Person("toto", 23, _)=> true case _ => false})


// filter
val seqFiltered = seq.filter(_%2==0)

// flatten
val flattenList = List(List(1,2), List(3)).flatten

/* fizzbuzz */
/*
Si l'entier est multiple de 3: afficher fizz
Si l'entier est multiple de 2: afficher buzz
Si l'entier est multiple de 2 et 3: afficher fizzBuzz
Sinon renvoyer l'entier en question
 */
def fizzBuzz(i: Int) = (i % 2 == 0, i % 3 == 0) match {
  case (true, false) => "fizz"
  case (false, true) => "buzz"
  case (true, true) => "fizzBuzz"
  case _ => i.toString

}

oneToHundred.map(i => i->fizzBuzz(i))