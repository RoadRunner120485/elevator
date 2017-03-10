package v2

import akka.actor.ActorRef

/**
  * Created by sturmm on 26.11.16.
  */
object Protocol {

  case object Tick

  sealed trait Direction
  object Up extends Direction
  object Down extends Direction

  trait Instruction
  case class ServiceRequest(sourceFloor: Int, direction: Direction) extends Instruction
  case class ServiceInstruction(targetFloor: Int) extends Instruction

  case class AnyServiceRequest(currentFloor: Int)
  case class ServiceRequestOnFloor(sourceFloor: Int)

  case class ElevatorCreated(elevator: ActorRef)

}
