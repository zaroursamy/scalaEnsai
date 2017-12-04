import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

/* Case class */
case class AppelSortant(id: String, nameCommercial: String, debut: Long, numAppele: String, fin: Long)

object AppelSortant {
  def apply(name: String, debut: Long, num: String, fin: Long): AppelSortant = AppelSortant(
    id = UUID.randomUUID().toString,
    nameCommercial = name,
    debut = debut,
    numAppele = num,
    fin = fin
  )

  def empty = AppelSortant("","",0,"",0)
}

case class AppelEntrant(id: String, date: Long, numAppelant: String, commercial: String, duree: Long)

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


val sortant1 = AppelSortant("Tata",0,"06",5)
val sortant2 = AppelSortant("Tata",6,"06",7)
val sortant3 = AppelSortant("Tata",20,"06",24)
val sortant4 = AppelSortant("Toto",2,"07",5)

val entrant1 = AppelEntrant(8, "06","Tata",5)
val entrant2 = AppelEntrant(0, "07","Toto",1)

object Implicits{
  implicit class Sortant(sortant: AppelSortant){
    def isLong = sortant.fin - sortant.debut >= 2
  }
}
