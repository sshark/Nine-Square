package controllers

import play.api.Logger
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

/**
  *
  * @author Lim, Teck Hooi
  *
  *
  */

class Application extends Controller {
  def index = Action {implicit request =>
    val form = if (request.flash.get("error").isDefined) {
      Logger.debug("Login error")
      Users.loginForm.bind(request.flash.data).withGlobalError(request.flash.get("error").get)
    } else {
      Users.loginForm
    }
    Ok(views.html.index(form))
  }
}
