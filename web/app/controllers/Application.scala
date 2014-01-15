package org.teckhooi.ninesquare.controllers

import play.api.mvc.{Action, Controller}


object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }
}