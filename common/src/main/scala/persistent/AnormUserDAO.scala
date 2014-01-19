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

  override def insert(user: User) = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """insert into user
            (email, password, name, date_created, active) values
            ({email}, {password}, {name}, {dateCreated},{active})""")
          .on('email -> user.email, 'password -> user.password, 'name -> user.name, 'dateCreated -> user.dateCreated, 'active -> true)
          .executeInsert()
    }
  }

  override def clear = DB.withConnection {implicit connection =>
    SQL("delete from user").executeUpdate()}


  override def delete(oid : Long) = {
    DB.withConnection {
      implicit connection =>
        SQL("delete from achievement where user_oid = {user_oid}").on('user_oid -> oid).executeUpdate()
        SQL("delete from user where oid = {oid}").on('oid -> oid).executeUpdate()
    }
  }

  override def update(user: User) = {
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

  override def list : List[User] = DB.withConnection {
      implicit connection =>
        SQL("select * from user")().map(row =>
          User(row[String]("email"), row[String]("password"), "", row[String]("name"), row[Option[Boolean]]("active"),
            row[Option[Date]]("date_created"), row[Option[Long]]("oid"))).toList
  }

  override def exist(email : String) = DB.withConnection {
      implicit connection =>
        SQL("select count(*) as c from user where email = {email}").on('email -> email)().head[Long]("c")
  }  == 1

  override def login(email : String, password : String)= DB.withConnection {
      implicit connection =>
        SQL("select count(*) as c from user where email = {email} and password = {password}")
          .on('email -> email, 'password -> password)().head[Long]("c")
  } == 1
}
