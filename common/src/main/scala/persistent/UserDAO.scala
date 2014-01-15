package org.teckhooi.ninesquare.persistent

import java.util.Date

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

case class User(email : String, password : String, name : String, active : Boolean, dateCreated : Date, oid: Long = 0)

trait UserDAO {
  def insert(user : User)
  def delete(oid : Long)
  def update(user : User)
  def list : List[User]
  def clear
}
