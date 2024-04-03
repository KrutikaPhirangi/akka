import akka.{ Receiver, Sender}
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class ActorSpec extends TestKit(ActorSystem("ActorSpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Sender" should {
    "send messages to receiver and receive response" in {
      val receiver = TestProbe("receiver")
      val sender = system.actorOf(Props(new Sender(receiver.ref)))
      sender ! "startLoop"
      receiver.expectMsg(10.seconds, "Hi.. this is message from sender")
      receiver.reply("Sending response from receiver to sender")
      expectNoMessage(5.seconds)
    }
  }


  "Receiver" should {
    "receive message from sender and send response" in {
      val receiver = system.actorOf(Props[Receiver])
      receiver ! "Some message"
      expectMsg(5.seconds, "Sending response from receiver to sender")
    }
  }

}
