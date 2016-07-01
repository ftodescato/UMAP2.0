package models

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import play.api.libs.json.Json
import scala.concurrent.Future
import play.api.mvc.Request
import play.api.Logger._

import play.api.i18n._
import play.api.mvc.RequestHeader

/**
 * Check for authorization
 *
 */
 case class WithServices(roles: Array[String], mail: Boolean) extends Authorization[User, JWTAuthenticator] {
   def isAuthorized[B](user: User, authenticator: JWTAuthenticator)(implicit r: Request[B], m: Messages)  = {
    var roleOK = false
    if(mail)
    {
      for(userRole <- roles if roleOK == false) {
        if (user.role == userRole)
          roleOK = true
      }
      Future.successful(roleOK && user.mailConfirmed == true)
    }
    else
      Future.successful(roleOK)
    }
 }
/*
 case class WithServicesMultiple(role: String, role2: String, mail: Boolean) extends Authorization[User, JWTAuthenticator] {
   def isAuthorized[B](user: User, authenticator: JWTAuthenticator)(implicit r: Request[B], m: Messages)  = {
    if(mail)
     Future.successful((user.role == role || user.role == role2) && user.mailConfirmed == true)
    else
     Future.successful(user.role == role || user.role == role2)
  }
}
 case class WithServicesMultipleAll(role: String, role2: String, role3: String, mail: Boolean) extends Authorization[User, JWTAuthenticator] {
   def isAuthorized[B](user: User, authenticator: JWTAuthenticator)(implicit r: Request[B], m: Messages)  = {
    if(mail)
     Future.successful((user.role == role || user.role == role2 || user.role == role3) && user.mailConfirmed == true)
    else
     Future.successful(user.role == role || user.role == role2 || user.role == role3)
  }
 }


 object WithServices {
   def isAuthorized(user: User, role: String): Boolean =
     role == user.role
 }*/
