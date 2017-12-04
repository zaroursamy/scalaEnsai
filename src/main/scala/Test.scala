import java.util.UUID

case class AppelSortant(id: String, nameCommercial: String, debut: Long, numAppele: String, fin: Long) extends Sortant {
  override def startTime: Long = debut

  override def duration: Long = fin - debut

  override def endTime: Long = fin

  override def from: String = nameCommercial

  override def to: String = numAppele

  override def typeAppel: TypeAppel = Sortant
}
object AppelSortant {
  def apply(name: String, debut: Long, num: String, fin: Long): AppelSortant = AppelSortant(
    id = UUID.randomUUID().toString.take(8),
    nameCommercial = name,
    debut = debut,
    numAppele = num,
    fin = fin
  )
}
case class AppelEntrant(id: String, date: Long, numAppelant: String, commercial: String, duree: Long) extends Entrant {
  override def startTime: Long = date

  override def duration: Long = duree

  override def endTime: Long = startTime+duration

  override def from: String = numAppelant

  override def to: String = commercial

  override def typeAppel: TypeAppel = Entrant
}
object AppelEntrant {
  def apply(date: Long, numAppelant: String, commercial: String, duree: Long): AppelEntrant = AppelEntrant(
    id = UUID.randomUUID().toString.take(8),
    date = date,
    numAppelant = numAppelant,
    commercial = commercial,
    duree = duree
  )
}


sealed trait TypeAppel
case object Sortant extends TypeAppel
case object Entrant extends TypeAppel
case object Autre extends TypeAppel

sealed trait Event {
  def startTime: Long
  def duration: Long
  def endTime: Long
}

trait Appel extends Event{
  def from:String
  def to:String
  def typeAppel: TypeAppel
}

trait Entrant extends Appel

trait Sortant extends Appel

object Event {

  case class CallSession(startCall: Appel, calls: Seq[Appel]) {
    def endTime: Long = allCalls.map(_.endTime).max

    def allCalls: Seq[Appel] = startCall +: calls
  }

  case class ProcessResult(sessions: Seq[CallSession], rejects: Seq[Event])

  def process(seq: Seq[Appel]): ProcessResult = {
    val sortedEvent: Seq[Appel] = seq.sortBy(_.startTime)

    case class LocalState(currentCS: Option[CallSession],
                          prevCS: Seq[CallSession],
                          rejects: Seq[Event])

    def zero: LocalState = LocalState(None, Seq.empty, Seq.empty)

    def op(ls: LocalState, evt: Event): LocalState = {
      (evt, ls) match {
        case (e: Appel, LocalState(None, _, _)) => ls.copy(currentCS = Some(CallSession(e, Nil)))
        case (e: Appel, LocalState(Some(cs), _, _)) if e.startTime - cs.endTime <= 2 => ls.copy(currentCS = Some(cs.copy(calls = cs.calls :+ e)))
        case (e: Appel, LocalState(Some(cs), prev, _)) => ls.copy(currentCS = Some(CallSession(e, Nil)), prevCS = prev :+ cs)
        case _ => ls.copy(rejects = ls.rejects :+ evt)

      }
    }

    val ls: LocalState = sortedEvent.foldLeft(zero)(op)

    def finalizer(localState: LocalState): ProcessResult = {
      import localState._
      ProcessResult(currentCS.map(c => prevCS :+ c).getOrElse(prevCS),
        rejects)
    }

    val pr = finalizer(ls)
    pr
  }
}

object Test {

  def main(args: Array[String]): Unit = {

    val sortant1 = AppelSortant("Tata", 0, "06", 5)
    val sortant2 = AppelSortant("Tata", 6, "06", 7)
    val sortant3 = AppelSortant("Tata", 20, "06", 24)
    val sortant4 = AppelSortant("Toto", 90, "07", 5)

    val entrant1 = AppelEntrant(8, "06", "Tata", 5)
    val entrant2 = AppelEntrant(0, "07", "Toto", 1)

    val seq: Seq[Appel] = Seq(sortant1, sortant2, sortant3, sortant4, entrant1, entrant2)

    val res = seq.groupBy{a =>
      a.typeAppel match {
        case Entrant => a.to
        case Sortant => a.from
      }
    }
      .map({case(name, appels) => name -> Event.process(appels)})


    println("sessions tata")
    res.get("Tata").foreach(_.sessions.foreach(println))
    println("rejects tata")
    res.get("Tata").foreach(_.rejects.foreach(println))

    println("****")

    println("sessions toto")
    res.get("Toto").foreach(_.sessions.foreach(println))
    println("rejects toto")
    res.get("Toto").foreach(_.rejects.foreach(println))



  }
}