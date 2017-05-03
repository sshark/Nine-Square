package controllers

import org.teckhooi.ninesquare.persistent.{Login, User}
import play.Logger
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, Flash}

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

class Users extends Controller {

  private val userForm : Form[User] = Form(
    mapping(
      "email" -> email.verifying(Messages("error.email.exists"), email => true),  // TODO check if email exist with database
      "password" -> nonEmptyText,
      "verifyPassword" -> nonEmptyText,
      "name" -> nonEmptyText,
      "active" -> optional(boolean),
      "dateCreated" -> optional(date),
      "oid" -> optional(longNumber)
    )(User.apply)(User.unapply)
      .verifying(Messages("error.passwords.not.equal"), user => user.password == user.verifyPassword))

  def list = Action {
/*  TODO retrieve users from database
    val users = AnormUserDAO.list.foldLeft(JsArray())((users, user: User) => users.append(Json.obj(
      "email" -> user.email,
      "password" -> user.password,
      "name" -> user.name,
      "dateCreated" -> user.dateCreated,
      "active" -> user.active,
      "oid" -> user.oid
    )))
*/
    val users = List[String]()  // TODO temporarily
    Ok(Json.toJson(users))
  }

  def removeUser(oid: Long) = Action {
//    AnormUserDAO.delete(oid)  // TODO remove user from database
    Logger.debug("User with oid " + oid + " removed.")
    Ok(Json.toJson(Map("deleted" -> ("{oid : " + oid + "}"))))
  }

  /*
    def updateUser = Action(parse.json) {request => {
      Logger.debug("{received : " + request.body.as)
      }
    }
  */

  def clearUsers = Action {
//    AnormUserDAO.clear  // TODO remove all users
    Ok(Json.toJson(Map("clearUsers" -> "{deleted : Ok }")))
  }

  def signIn = Action {implicit request =>
    Users.loginForm.bindFromRequest().fold(
      hasErrors = { form =>
        Redirect(routes.Application.index)
          .flashing(Flash(form.data) +
            ("error" -> Messages("error.validation")))
      },
      success = {
        newLogin => Ok(views.html.game())
      }
    )
  }

  def newUser = Action { implicit request =>
    val form = if (request.flash.get("error").isDefined) {
      userForm.bind(request.flash.data).withGlobalError(request.flash.get("error").get)
    } else {
      userForm
    }
    Ok(views.html.editUser(form))
  }

  // this method can be used for update and create new user
  def updateUser = Action { implicit request =>
    val newUserForm = userForm.bindFromRequest()

    newUserForm.fold(
      hasErrors = { form =>
        Redirect(routes.Users.newUser)
          .flashing(Flash(form.data) +
            ("error" -> Messages("error.validation")))
      },
      success = { newUser => {
          Logger.debug("New user is inserted, " + newUser.email)
//          AnormUserDAO.insert(newUser)  // TODO add new user
          Redirect(routes.Application.index)
        }
      }
    )
  }
}

object Users {
  val loginForm : Form[Login] = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(Login.apply)(Login.unapply)
      .verifying(Messages("error.login"), login => true)) // TODO check login using database i.e. AnormUserDAO.login(login.email, login.password)))
}
