package controllers

import play.api.mvc.{Action, Controller}


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready 123."))
//    Redirect(routes.Products.list())
  }

}