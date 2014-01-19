package org.teckhooi.ninesquare.persistent

import java.util.Date

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

case class User(email : String, password : String, verifyPassword : String, name : String,
                active : Option[Boolean], dateCreated : Option[Date], oid: Option[Long] = Some(0))

case class Login(email : String, password : String)

trait UserDAO {
  def insert(user : User)
  def delete(oid : Long)
  def update(user : User)
  def list : List[User]
  def exist(user : String) : Boolean
  def login(user : String, password : String) : Boolean
  def clear
}
