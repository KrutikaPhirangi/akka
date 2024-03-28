package service

import com.datastax.driver.core.{Cluster, PreparedStatement, Session}

class DatasetService {
  private val cluster: Cluster = Cluster.builder()
    .addContactPoint("localhost")
    .withPort(9042)
    .build()

  private val session: Session = cluster.connect("dataset")

  private val insertStatement: PreparedStatement = session.prepare(
    "INSERT INTO dataset.data (id, content_id, content_type, percentage, user) VALUES (?, ?, ?, ?, ?)"
  )

  def createDataset(contentId: String, contentType: String, user: String): Boolean = {
    val id: String = s"$contentId-$user"
    val percentage: Double = 0
    try {
      session.execute(insertStatement.bind(id, contentId, contentType, percentage.asInstanceOf[java.lang.Double], user))
      true
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        false
    }
  }

  private val selectStatement: PreparedStatement = session.prepare(
    "SELECT * FROM dataset.data WHERE id = ?"
  )

  private val updateStatement: PreparedStatement = session.prepare(
    "UPDATE dataset.data SET percentage = ? WHERE id = ?"
  )

  private def userExists(id: String): Boolean = {
    val resultSet = session.execute(selectStatement.bind(id))
    val exists = Option(resultSet.one()).isDefined
    println(s"User with ID $id exists: $exists")
    exists
  }

  def updateDataset(id: String, percentage: Double): Option[Boolean]  = {
    try {
      if (userExists(id)) {
        session.execute(updateStatement.bind(percentage.asInstanceOf[java.lang.Double], id))
        Some(true)
      } else {
        println("User does not exist.")
        Some(false)
      }
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        Some(false)
    }
  }

  def closeSession(): Unit = {
    session.close()
    cluster.close()
  }
}
