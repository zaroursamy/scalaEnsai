
/* Exo: reverse une liste */
def reverse[T](list: List[T]): List[T] = list match {
  case Nil => Nil
  case x :: Nil => List(x)
  case x :: xs => reverse(xs) ++ List(x)
}

val list = ('a' to 'h').map(_.toString).toList
val seq = 1 to 10
val reversedString = reverse(list)
val reversedNil = reverse(Nil)
val reversedInt = reverse(seq.toList)

import scala.util.Random

case class Person(name: String, age: Int)

object Person {

  def generateAge(): Int = (Random.nextGaussian() + 23).toInt

  def choose[T](xs: Seq[T]): T = xs.apply(Random.nextInt(xs.size))

  def generateName() = choose[String](Seq("toto", "tata", "titi", "tutu"))

  def apply: Person = Person(
    generateName(),
    generateAge()
  )

  def generate(n: Int): Seq[Person] = Stream.fill(n)(apply)
}

val person: Seq[Person] = Person.generate(100).toList

person.map(_.age).reduce((x, y) => math.min(x, y))

person.reduce((p1, p2) => if (p1.age < p2.age) p1 else p2)

person.map(p => List(p, p.copy(age = p.age / 2))).flatten
person.flatMap(p => p match {
  case Person("toto", a) if a > 23 => Some("grand toto")
  case Person("titi", a) if a < 23 => Some("petit titi")
  case _ => None
}
)

person.groupBy(_.age).map(p => p._1 -> p._2.size)

(1 to 10).foldLeft("0+")((s, i) => s + i.toString+ "+").dropRight(1)
person.foldLeft(Set.empty[Int])((set, p) => set++Set(p.age))