package controllers

import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

class Games extends Controller {
  def index = Action {implicit request =>
    Ok(views.html.game())
  }
}
