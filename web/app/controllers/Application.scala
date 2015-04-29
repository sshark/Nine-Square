package controllers

import play.api.Logger
import play.api.mvc.{Action, Controller}


object Application extends Controller {

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
