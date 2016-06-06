// package controllers
//
// import java.io.File
// import javax.inject.Inject
//
// import org.apache.commons.mail.EmailAttachment
// import play.api.Environment
// import play.api.libs.mailer._
// import play.api.mvc.{Action, Controller}
//
//  class ApplicationScala @Inject()(mailer: MailerClient, environment: Environment) extends Controller {
//
//   def sender = Action {
//     //val cid = "465"
//     val email = Email(
//       "Simple email",
//       "Mister FROM <latexebiscotti@gmail.com>",
//       Seq("Miss TO <tode.92@hotmail.it>"),
//       bodyText = Some("L'ho fatto andare. Non so bene cosa sbagliassi ma tant'è che sta mail te la manda play, buon lavoro.")
//     )
//     val id = mailer.send(email)
//     Ok(s"Email $id sent!")
//   }
//
//   // def sendWithCustomMailer = Action {
//   //   val mailer = new SMTPMailer(SMTPConfiguration("typesafe.org", 1234))
//   //   val id = mailer.send(Email("Simple email", "Mister FROM <filippo.todescato@gmail.com>"))
//   //   Ok(s"Email $id sent!")
//   // }
// }
