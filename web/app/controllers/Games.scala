package controllers

import play.api.mvc.{Action, Controller}

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

object Games extends Controller {
  def index = Action {implicit request =>
    Ok(views.html.game())
  }
}
