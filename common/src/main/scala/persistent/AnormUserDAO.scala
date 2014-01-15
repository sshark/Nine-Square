package org.teckhooi.ninesquare.persistent.impl

import org.teckhooi.ninesquare.persistent.UserDAO
import play.api.db.DB
import anorm._
import org.teckhooi.ninesquare.persistent.User
import java.util.Date
import play.api.Play.current

/**
 *
 * @author Lim, Teck Hooi
 *
 *
 */

object AnormUserDAO extends UserDAO{
  @Override
  def insert(user: User) = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """insert into user
            (email, password, name, date_created, active) values
            ({email}, {password}, {name}, {dateCreated},{active})""")
          .on('email -> user.email, 'password -> user.password, 'name -> user.name, 'dateCreated -> user.dateCreated, 'active -> user.active)
          .executeInsert()
    }
  }

  @Override
  def clear = DB.withConnection {implicit connection =>
    SQL("delete from user").executeUpdate()}


  @Override
  def delete(oid : Long) = {
    DB.withConnection {
      implicit connection =>
        SQL("delete from achievement where user_oid = {user_oid}").on('user_oid -> oid).executeUpdate()
        SQL("delete from user where oid = {oid}").on('oid -> oid).executeUpdate()
    }
  }

  @Override
  def update(user: User) = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """update user
            set email = {email},
            password = {password},
            name = {name},
            date_created = {dateCreated},
            active = {active}
            where user_oid = {user_oid}
          """).on('user_id -> user.oid, 'email -> user.email, 'password -> user.password, 'name -> user.name,
            'dateCreated -> user.dateCreated, 'active -> user.active).executeUpdate()
    }
  }

  @Override
  def list : List[User] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from user")().map(row =>
          User(row[String]("email"), row[String]("password"), row[String]("name"), row[Boolean]("active"),
            row[Date]("date_created"), row[Long]("oid"))).toList
    }
  }
}
