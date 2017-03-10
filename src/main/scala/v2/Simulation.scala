package v2

import akka.actor.{ActorRef, ActorSystem, Props}


/**
  * Created by sturmm on 25.11.16.
  */
object Simulation extends App {

  println("Starting application")

  def elevatorSystem = ActorSystem("simulation")
  val ctrl = elevatorSystem.actorOf(Props(new ElevatorController(elevatorSystem.settings.config.getInt("elevator.count"))), "elevators")
  val environment = elevatorSystem.actorOf(Props(new Environment(ctrl)), "elevators")


}
