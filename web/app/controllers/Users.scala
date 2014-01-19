package controllers

import play.api.mvc.{Flash, Action, Controller}
import play.api.libs.json.{JsArray, Json}
import org.teckhooi.ninesquare.persistent.{Login, User}
import org.teckhooi.ninesquare.persistent.impl.AnormUserDAO
import play.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

object Users extends Controller {

  private val userForm : Form[User] = Form(
    mapping(
      "email" -> email.verifying(Messages("error.email.exists"), email => !AnormUserDAO.exist(email)),
      "password" -> nonEmptyText,
      "verifyPassword" -> nonEmptyText,
      "name" -> nonEmptyText,
      "active" -> optional(boolean),
      "dateCreated" -> optional(date),
      "oid" -> optional(longNumber)
    )(User.apply)(User.unapply)
      .verifying(Messages("error.passwords.not.equal"), user => user.password == user.verifyPassword))

  val loginForm : Form[Login] = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(Login.apply)(Login.unapply)
      .verifying(Messages("error.login"), login => AnormUserDAO.login(login.email, login.password)))
  
  def list = Action {
    val users = AnormUserDAO.list.foldLeft(JsArray())((users, user: User) => users.append(Json.obj(
      "email" -> user.email,
      "password" -> user.password,
      "name" -> user.name,
      "dateCreated" -> user.dateCreated,
      "active" -> user.active,
      "oid" -> user.oid
    )))
    Ok(Json.stringify(users))
  }

  def removeUser(oid: Long) = Action {
    AnormUserDAO.delete(oid)
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
    AnormUserDAO.clear
    Ok(Json.toJson(Map("clearUsers" -> "{deleted : Ok }")))
  }

  def signIn = Action {implicit request =>
    loginForm.bindFromRequest().fold(
      hasErrors = { form =>
        Redirect(routes.Application.index)
          .flashing(Flash(form.data) +
            ("error" -> Messages("error.validation")))
      },
      success = {newLogin => {
        Redirect(routes.Application.index)
           .flashing(Flash(loginForm.data) +
          ("status" -> "Login successful"))
        }
      }
    )
  }

  def newUser = Action { implicit request =>
    val form = if (flash.get("error").isDefined) {
      userForm.bind(flash.data).withGlobalError(flash.get("error").get)
    } else {
      userForm
    }
    Ok(views.html.editUser(form))
  }

  def saveUser = Action { implicit request =>
    val newUserForm = userForm.bindFromRequest()

    newUserForm.fold(
      hasErrors = { form =>
        Redirect(routes.Users.newUser)
          .flashing(Flash(form.data) +
            ("error" -> Messages("error.validation")))
      },
      success = { newUser => {
          Logger.debug("New user is inserted, " + newUser.email)
          AnormUserDAO.insert(newUser)
          Redirect(routes.Application.index)
        }
      }
    )
  }
}
