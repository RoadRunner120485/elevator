package v1

import akka.actor.{ActorSystem, Props}
import v1.actors.ElevatorActor
import v1.messages.{ServiceRequest, Tick}
import v1.strategy.SimpleElevatorStrategy

/**
* Created by sturmm on 25.11.16.
*/
object Simulation extends App {
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  println("Starting application")

  def elevatorSystem = ActorSystem("elevatorSystem")
  val elevator = elevatorSystem.actorOf(Props(new ElevatorActor with SimpleElevatorStrategy), name = "elevator")

  elevatorSystem.scheduler.schedule(0 milliseconds, 100 milliseconds, elevator, Tick)

  elevator ! ServiceRequest(5)
}
