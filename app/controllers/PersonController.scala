package controllers

import javax.inject._

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class PersonController @Inject()(personService: PersonRepository,
                                  cc: MessagesControllerComponents
                                )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  case class CreatePersonForm(name: String, phoneNumber: String)

  val personForm: Form[CreatePersonForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "phoneNumber" -> nonEmptyText
    )(CreatePersonForm.apply)(CreatePersonForm.unapply)
  }

  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.first())
  }

  def addPerson(): Action[AnyContent] = Action.async { implicit request =>
    personForm.bindFromRequest.fold(
      errorForm => Future.successful(Ok("Wrong person data")),
      person =>
        personService.add(person.name, person.phoneNumber).map { _ =>
          Ok("Succsessfully added")
        }
    )
  }

  def updatePerson(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    personForm.bindFromRequest.fold(
      hasError => Future(BadRequest("Wrong person data")),
      personData => personService.update(Person(id, personData.name, personData.phoneNumber))
        .map(result => Ok(result))
    )
  }

  def deletePerson(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    personService.delete(id).map(result => {
      println(result)
      Ok(result)
    })
  }

  def getPerson(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    personService.get(id).map({
      case None => NotFound("Id not found")
      case Some(person) => Ok(Json.toJson(person))
    })
  }

  def getAllPersons: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    personService.getAll.map(result =>  Ok(Json.toJson(result)))
  }

  def getFilteredByName(substring: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    personService.getFilteredByName(substring).map(result =>
      Ok(Json.toJson(result)))
  }

  def getFilteredByMobile(substring: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    personService.getFilteredByMobile(substring).map(result =>
      Ok(Json.toJson(result)))
  }
}
