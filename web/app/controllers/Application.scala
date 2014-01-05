package controllers


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready 123."))
//    Redirect(routes.Products.list())
  }

}