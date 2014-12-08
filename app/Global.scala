package global

import utils.Response

import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import java.io.File
import scala.concurrent._

object Global extends play.api.GlobalSettings with Response {

  /** do start up set up here, current implementation makes some start up checks */
  override def onStart(app: play.api.Application) {
    ensureCfgExists("app.api.root")
    ensureCfgExists("app.datastore.root")
    ensureCfgExists("booth.auth.secret")
    ensureCfgExists("booth.auth.expiry")
    ensureCfgExists("app.datastore.public")
    ensureCfgExists("app.datastore.private")

    val publicDs = Play.current.configuration.getString("app.datastore.public").get
    val privateDs = Play.current.configuration.getString("app.datastore.private").get

    val publicDsF = new File(publicDs)
    val privateDsF = new File(privateDs)

    if( (!publicDsF.exists) || (!publicDsF.isDirectory) || (!publicDsF.canWrite)) {
      Logger.error(s"$publicDs not directory or is not writable")
      System.exit(1)
    }

    if( (!privateDsF.exists) || (!privateDsF.isDirectory) || (!privateDsF.canWrite)) {
      Logger.error(s"$privateDs not directory or is not writable")
      System.exit(1)
    }
  }

  /** global error handler */
  override def onError(request: RequestHeader, throwable: Throwable) = {
    Future { InternalServerError(Json.toJson(Error(s"Internal error while processing request $request", ErrorCodes.GENERAL_ERROR))) }
  }

  /** ensures the configuration property is set */
  private def ensureCfgExists(cfg: String) = {
    Play.current.configuration.getString(cfg) match {
      case None => {
        Logger.error("configuration $cfg not set")
        System.exit(1)
      }
      case Some(value) => value
    }
  }
}