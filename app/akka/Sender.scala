package akka

import akka.actor.{Actor, ActorRef}

import scala.concurrent.duration.DurationInt

class Sender(receiver: ActorRef) extends Actor {

  import context.dispatcher

  def receive: Receive = {
    case "startLoop" =>
      println("Starting loop in sender")
      context.become(looping)
      context.system.scheduler.scheduleOnce(5.seconds, self, "sendMessage")
  }

  private def looping: Receive = {
    case "sendMessage" =>
      println("Sending message to receiver")
      receiver ! "Hi.. this is message from sender"
      context.system.scheduler.scheduleOnce(5.seconds, self, "sendMessage")
    case "Sending response from receiver to sender" =>
      println(s"Received response from receiver")
  }
}
