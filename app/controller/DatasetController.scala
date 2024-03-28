package controller

import play.api.libs.json._
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import service.DatasetService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class DatasetController @Inject()(val cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  private val datasetService = new DatasetService()

  def createContent: Action[JsValue] = Action(parse.json) { request =>
    val contentJson = request.body
    val requiredFields = Set("content_id", "content_type", "user")
    val data = contentJson.asOpt[Map[String, String]]

    data match {
      case Some(dataMap) if dataMap.keySet == requiredFields =>
        val contentId = dataMap("content_id")
        val contentType = dataMap("content_type")
        val userId = dataMap("user")
        val result = datasetService.createDataset(contentId, contentType, userId)

        if (result) {
          println("Data saved in the database:")
          println(dataMap)
          Ok("data successfully inserted into Cassandra.")
        } else {
          BadRequest("Failed to create content")
        }

      case Some(_) =>
        val errorMsg = "Invalid JSON format. Required fields (content_id, content_type, user) must be present."
        println(s"Error: $errorMsg")
        BadRequest(errorMsg)

      case None =>
        val errorMsg = "Invalid JSON format."
        println(s"Error: $errorMsg")
        BadRequest(errorMsg)
    }
  }

  def updateContent(id: String): Action[JsValue] = Action(parse.json) { request =>
    val contentJson = request.body
    val requiredFields = Set( "percentage")
    val data = contentJson.asOpt[Map[String, JsValue]]

    data match {
      case Some(dataMap) if dataMap.keySet == requiredFields =>
//        val idOpt = dataMap.get("id").flatMap(_.asOpt[String])
        val percentageOpt = dataMap.get("percentage").flatMap(_.asOpt[Double])

        ( percentageOpt) match {
          case ( Some(percentage)) if percentage > 0 && percentage < 100 =>
            try {
              val updateResult = datasetService.updateDataset(id, percentage)
              updateResult match {
                case Some(true) => Ok("Content successfully updated in Cassandra.")
                case Some(false) => BadRequest("User does not exist")
                case None => BadRequest("An error occurred while updating content")
              }
            } catch {
              case _: Exception =>
                BadRequest("Failed to update content")
            }
          case (Some(percentage)) if percentage <= 0 || percentage >= 100 =>
            BadRequest("Percentage value should be greater than 0 and less than 100")
          case _ =>
            BadRequest("Invalid JSON format in request body")
        }
      case None =>
        BadRequest("Missing request body")
      case Some(_) =>
        BadRequest("Invalid JSON format, unexpected fields in request body")
    }
  }
}
