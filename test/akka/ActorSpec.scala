import akka.{ActorA, ActorB}
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

  "ActorA" should {
    "send messages to ActorB and receive response" in {
      val actorB = TestProbe("actorB")
      val actorA = system.actorOf(Props(new ActorA(actorB.ref)))
      actorA ! "startLoop"
      actorB.expectMsg(10.seconds, "Hi.. this is message from actor A")
      actorB.reply("Sending response from actor B to actor A")
      expectNoMessage(5.seconds)
    }
  }


  "ActorB" should {
    "receive message from ActorA and send response" in {
      val actorB = system.actorOf(Props[ActorB])
      actorB ! "Some message"
      expectMsg(5.seconds, "Sending response from actor B to actor A")
    }
  }

}
