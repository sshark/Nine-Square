package org.teckhooi.ninesquare.controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.{JsArray, Json}
import java.util.Date
import org.teckhooi.ninesquare.persistent.User
import org.teckhooi.ninesquare.persistent.impl.AnormUserDAO
import play.Logger

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

object UserController extends Controller {
  def list = Action {
    val users = AnormUserDAO.list.foldLeft(JsArray())((users, user: User) => users.append(Json.obj(
      "email" -> user.email,
      "passowrd" -> user.password,
      "name" -> user.name,
      "dateCreated" -> user.dateCreated,
      "active" -> user.active,
      "oid" -> user.oid
    )))

    Ok(Json.stringify(users))
  }

  def newUser = Action {
    AnormUserDAO.insert(User("usr1@nine-square.com", "p@ss", "usr1", true, new Date))
    AnormUserDAO.insert(User("usr2@nine-square.com", "p@ss", "usr1", true, new Date))
    AnormUserDAO.insert(User("usr3@nine-square.com", "p@ss", "usr1", true, new Date))
    Ok(Json.toJson(Map("newUsers" -> "3 users created")))
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
    Ok(Json.toJson(Map("deleted" -> "{oid : -1 }")))
  }

  def registerNew = Action {
    Ok(views.html.registerNew())
  }
}
