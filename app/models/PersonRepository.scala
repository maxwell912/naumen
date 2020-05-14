package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ Future, ExecutionContext }


@Singleton
class PersonRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class PersonTable(tag: Tag) extends Table[Person](tag, "persons") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def phoneNumber = column[String]("phoneNumber")

    def * = (id, name, phoneNumber) <> ((Person.apply _).tupled, Person.unapply)
  }

  private val persons = TableQuery[PersonTable]

  def add(name: String, phoneNumber: String): Future[Person] = db.run {
    (persons.map(p => (p.name, p.phoneNumber))
      returning persons.map(_.id)
      into ((nameAge, id) => Person(id, nameAge._1, nameAge._2))
    ) += (name, phoneNumber)
  }

  def update(person: Person): Future[String] = db.run {
    persons.filter(_.id === person.id).update(person)
      .map(_ => "Person successfully updated")
  }

  def delete(id: Long): Future[String] = db.run {
    persons.filter(_.id === id).delete
      .map(_ => "Person successfully deleted")
  }

  def get(id: Long): Future[Option[Person]] = db.run {
    persons.filter(_.id === id).result.headOption
  }

  def getAll: Future[Seq[Person]] = db.run {
    persons.result
  }

  def getFilteredByName(substring: String): Future[Seq[Person]] = db.run {
      (for {
        person <- persons if person.name like s"%${substring}%"
      } yield person).result
  }

  def getFilteredByMobile(substring: String): Future[Seq[Person]] = db.run {
    {
      for {
        person <- persons if person.phoneNumber like s"%${substring}%"
      } yield person
    }.result
  }
}
