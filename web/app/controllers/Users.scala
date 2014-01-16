package controllers

import play.api.mvc.{Flash, Action, Controller}
import play.api.libs.json.{JsArray, Json}
import org.teckhooi.ninesquare.persistent.User
import org.teckhooi.ninesquare.persistent.impl.AnormUserDAO
import play.Logger
import play.api.data.Form
import play.api.data.Forms._

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

object Users extends Controller {

  private val userForm: Form[User] = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText,
      "verifyPassword" -> nonEmptyText,
      "name" -> nonEmptyText,
      "active" -> optional(boolean),
      "dateCreated" -> optional(date),
      "oid" -> optional(longNumber)
    )(User.apply)(User.unapply)
  )

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

/*
  def newUser = Action {
    AnormUserDAO.insert(User("usr1@nine-square.com", "p@ss", "p@ss", "usr1", true, new Date))
    AnormUserDAO.insert(User("usr2@nine-square.com", "p@ss", "p@ss", "usr1", true, new Date))
    AnormUserDAO.insert(User("usr3@nine-square.com", "p@ss", "p@ss", "usr1", true, new Date))
    Ok(Json.toJson(Map("newUsers" -> "3 users created")))
  }
*/

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
    Ok(Json.toJson(Map("deleted" -> "{oid : -1 }")))
  }

  def newUser = Action { implicit request =>
    Logger.debug("Logging on to new user")
    val form = if (flash.get("error").isDefined) {
      Logger.debug("Mew user form error")
      userForm.bind(flash.data)
    }

    else {
      Logger.debug("New user form")
      userForm
    }
    Ok(views.html.editUser(form))
  }

  def saveUser = Action { implicit request =>
    val newUserForm = userForm.bindFromRequest()

    newUserForm.fold(
      hasErrors = { form =>
        Redirect(routes.Users.newUser()).
          flashing(Flash(form.data) +
            ("error" -> "Validation error"))
      },
      success = { newUser =>
        AnormUserDAO.insert(newUser)
        Redirect(routes.Application.index)
      }
    )
  }
}
