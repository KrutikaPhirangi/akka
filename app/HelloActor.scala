import akka.actor.{Actor, ActorRef, ActorSystem, Props}

class ActorA(actorB: ActorRef) extends Actor {
  def receive: Receive = {
    case "startLoop" =>
      println("Starting loop in ActorA")
      context.become(looping)
      self ! "sendMessage"
  }

  def looping: Receive = {
    case "sendMessage" =>
      println("Sending message to actor B")
      actorB ! "Hi.. this is message from actor A"
    case "Sending response from actor B to actor A" =>
      println(s"Received response from actor B")
      self ! "sendMessage"
    case "stopLoop" =>
      println("Stopping loop in ActorA")
      context.unbecome()
  }
}

class ActorB extends Actor {
  def receive: Receive = {
    case msg: String =>
      println(s"Received message from actorA: $msg")
      sender() ! "Sending response from actor B to actor A"
  }
}
object ActorB {
  def props: Props = Props[ActorB]
}
object Main extends App {
  private val system = ActorSystem("HelloSystem")
  private val actorB = system.actorOf(ActorB.props, "actorB")
  private val actorA = system.actorOf(Props(new ActorA(actorB)), name = "actorA")

  actorA ! "startLoop"
}
