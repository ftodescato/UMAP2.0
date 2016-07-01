package models

import play.api.libs.json.Json
import play.api.mvc.Request
import play.api.i18n._

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator

import scala.concurrent.Future
//import com.mohiva.play.silhouette.api._
//import play.api.Logger._
//import play.api.mvc.RequestHeader

/**
 * Check for authorization
 * @param roles Array che contiene tutti i roles autorizzati
 * @param mail Boolean abilita il controllo dell'autorizzazione controllando oltre al ruolo il campo di mailConfirmed
 * @return true se autorizzato altrimenti false
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
