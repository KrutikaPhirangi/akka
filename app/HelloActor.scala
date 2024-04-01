import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.concurrent.duration._

class ActorA(actorB: ActorRef) extends Actor {
  import context.dispatcher
  def receive: Receive = {
    case "startLoop" =>
      println("Starting loop in ActorA")
      context.become(looping)
      context.system.scheduler.scheduleOnce(5.seconds, self, "sendMessage")
  }

  def looping: Receive = {
    case "sendMessage" =>
      println("Sending message to actor B")
      actorB ! "Hi.. this is message from actor A"
      context.system.scheduler.scheduleOnce(5.seconds, self, "sendMessage")
    case "Sending response from actor B to actor A" =>
      println(s"Received response from actor B")
  }
}

class ActorB extends Actor {
  def receive: Receive = {
    case msg: String =>
      println(s"Received message from actorA: $msg")
      Thread.sleep(2000)
      sender() ! "Sending response from actor B to actor A"
  }
}

object Main extends App {
  private val system = ActorSystem("HelloSystem")

  private val actorB = system.actorOf(Props[ActorB], "actorB")
  private val actorA = system.actorOf(Props(new ActorA(actorB)), name = "actorA")
  actorA ! "startLoop"
}
