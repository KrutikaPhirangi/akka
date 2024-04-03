package akka

import akka.actor.{Actor, ActorSystem, Props}


 class Receiver extends Actor {
  def receive: Receive = {
    case msg: String =>
      println(s"Received message from sender: $msg")
      Thread.sleep(2000)
      sender() ! "Sending response from receiver to sender"
  }
}

 object Main extends App {
  private val system = ActorSystem("SenderReceiver")
  private val receiver = system.actorOf(Props[Receiver], "receiver")
  private val sender = system.actorOf(Props(new Sender(receiver)), name = "sender")
  sender ! "startLoop"
}

