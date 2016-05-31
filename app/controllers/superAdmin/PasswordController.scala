package controllers.superAdmin

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import forms.company._
import models.PersistentPasswordInfo
import models.User
import models.services._
import models.daos.user.UserDAO
import models.daos.password.PasswordInfoDAO
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action

//import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
//import com.mohiva.play.silhouette.api.services.AvatarService
//import com.mohiva.play.silhouette.api.util.PasswordHasher
//import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import scala.concurrent.Future


class PasswordController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  userService: UserService,
  userDao: UserDAO,
  passwordInfoDao: PasswordInfoDAO,
  authInfoRepository: AuthInfoRepository,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher)
  extends Silhouette[User, JWTAuthenticator] {


//     def updateUser(userID: UUID) = Action.async(parse.json) { implicit request =>
//       request.body.validate[EditUser.Data].map { data =>
//         userDao.findByID(userID).flatMap {
//           case None => Future.successful(BadRequest(Json.obj("message" -> Messages("user.notComplete"))))
//           case Some(user) =>
//           val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
//           val companyInfo = data.company
//           companyDao.findByID(companyInfo).flatMap{
//           case Some(companyToAssign) =>
//             //val authInfo = passwordHasher.hash(data.password)
//             val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
//             val user2 = User(
//               userID = user.userID,
//               name = data.name,
//               surname = data.surname,
//               loginInfo = loginInfo,
//               email = data.email,
//               company = data.company,
//               role = data.role
//             )
//             for {
//               //user <- userService.save(user.copy(avatarURL = avatar))
//               user <- userDao.update(userID,user2)
//               //authInfo <- passwordInfoDao.update(loginInfo,authInfo)
//               authenticator <- env.authenticatorService.create(loginInfo)
//               token <- env.authenticatorService.init(authenticator)
//             } yield {
//             //  env.eventBus.publish(SignUpEvent(user, request, request2Messages))
//             //  env.eventBus.publish(LoginEvent(user, request, request2Messages))
//               Ok(Json.obj("token" -> "ok"))
//             }
//             case None =>
//               Future.successful(BadRequest(Json.obj("message" -> Messages("company.notExists"))))
//           }
//         }
//       }.recoverTotal {
//         case error =>
//           Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
//     }
// }

}
