import java.util.UUID

sealed trait TypeAppel
case object Sortant extends TypeAppel
case object Entrant extends TypeAppel
case object Autre extends TypeAppel



case class Session(start:Long, end:Long, appels:Seq[Appel])

object Session{
  def empty = Session(0,0,Nil)
}

trait Appel extends Session{

  self =>
  def start: Long
  def end: Long
  def from: String
  def to: String
  def typeAppel: TypeAppel

}


/* Case class */
case class AppelSortant(id: String, nameCommercial: String, debut: Long, numAppele: String, fin: Long) extends Appel {
  override def start: Long = debut

  override def end: Long = fin

  override def from: String = nameCommercial

  override def to: String = numAppele

  override def typeAppel: TypeAppel = Sortant
}

object AppelSortant {
  def apply(name: String, debut: Long, num: String, fin: Long): AppelSortant = AppelSortant(
    id = UUID.randomUUID().toString,
    nameCommercial = name,
    debut = debut,
    numAppele = num,
    fin = fin
  )

  def empty = AppelSortant("", "", 0, "", 0)
}

case class AppelEntrant(id: String, date: Long, numAppelant: String, commercial: String, duree: Long) extends Appel {
  override def start: Long = date

  override def end: Long = date + duree

  override def from: String = numAppelant

  override def to: String = commercial

  override def typeAppel: TypeAppel = Entrant
}

object AppelEntrant {
  def apply(date: Long, numAppelant: String, commercial: String, duree: Long): AppelEntrant = AppelEntrant(
    id = UUID.randomUUID().toString,
    date = date,
    numAppelant = numAppelant,
    commercial = commercial,
    duree = duree
  )

  def empty = AppelEntrant("", 0L, "", "", 0L)
}


val sortant1 = AppelSortant("Tata", 0, "06", 5)
val sortant2 = AppelSortant("Tata", 6, "06", 7)
val sortant3 = AppelSortant("Tata", 20, "06", 24)
val sortant4 = AppelSortant("Toto", 2, "07", 5)
val entrant1 = AppelEntrant(8, "06", "Tata", 5)
val entrant2 = AppelEntrant(0, "07", "Toto", 1)

case class State(sessions:Seq[Session], rejects: Seq[Appel])

val data: Seq[Appel] = Seq(sortant1, sortant2, sortant3, sortant4, entrant1, entrant2)

def compose(appel:Appel, state:State): State = {

  state.sessions.foldLeft(state){(st, session) =>
    val (min , max) = if(session.debSession < appel.start) (session, appel) else (appel, session)
    if(min.debSession - max.debSession <= 2) state.copy(sessions =state.sessions :+  Session(min.debSession, max.finSession, session.appels :+ appel))
    else state.copy(rejects = state.rejects :+ appel)
  }
}

val res: Map[String, State] = data.groupBy{ appel =>

  appel.typeAppel match {
    case Entrant => appel.to
    case Sortant => appel.from
  }
}
.map({ case (name, appels) =>

  name -> appels.foldLeft(State(Nil, Nil))({ case (s, a) => compose(a, s) })
})


/*
object Implicits {

  implicit class Sortant(sortant: AppelSortant) {
    def isLong = sortant.fin - sortant.debut >= 2
  }
}

case class LocalState(currentSession: Option[Session],
                      rejects:Seq[Appel])

def composeAppel(a:Appel, b:Session): LocalState*/