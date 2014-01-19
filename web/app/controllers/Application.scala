package controllers

import play.api.mvc.{Action, Controller}
import play.api.Logger


object Application extends Controller {

  def index = Action {implicit  request =>
    val form = if (flash.get("error").isDefined) {
      Logger.debug("Login error")
      Users.loginForm.bind(flash.data).withGlobalError(flash.get("error").get)
    } else {
      Users.loginForm
    }
    Ok(views.html.index(form))
  }
}
