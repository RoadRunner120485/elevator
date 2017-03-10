package v2

import akka.actor.{Actor, ActorLogging}

/**
  * Created by sturmm on 26.11.16.
  */
class Elevator extends Actor with ActorLogging {

  import v2.Protocol._

  val random = new scala.util.Random()

  var floor: Int = 0
  var instructions: List[ServiceInstruction] = Nil

  override def preStart(): Unit = {
    context become idle
    context.parent ! ElevatorCreated(context.self)
  }

  override def receive: Receive = {
    case _ => // do nothing
  }

  def idle: Receive = {
    case Tick => {
      instructions match {
        case Nil => context.parent ! AnyServiceRequest(floor)
        case head::tail => {
          instructions = tail
          context become move(head.targetFloor, head)
        }
      }
    }

    case req@ServiceRequest(sourceFloor, direction) => {
      log.info(s"Incoming ServiceRequest: ($sourceFloor, $direction)")

      context become move(sourceFloor, req)
    }

    case inst@ServiceInstruction(targetFloor) => {
      log.info(s"Incoming ServiceInstruction: ($targetFloor)")
      instructions = inst :: instructions
    }
  }

  def move(targetFloor: Int, instruction: Instruction): Receive = {
    case Tick if targetFloor == floor => {

      if (instruction.isInstanceOf[ServiceInstruction]) {
        log.info("ServiceInstruction handled...")
      } else {
        self ! ServiceInstruction(random.nextInt(10))
        log.info("ServiceRequest handled...")
      }

      log.info(s"...Finally reached  Floor #$targetFloor")

      context become idle
    }

    case Tick => context become floorChangingTimeout(1, if(floor < targetFloor) floor + 1 else floor - 1, move(targetFloor, instruction))
  }

  def floorChangingTimeout(tickCounter: Int, nextFloor: Int, returnState: => Receive): Receive = {
    case Tick if tickCounter % 4 == 0 => {
      log.info(s"Reached Floor #$nextFloor")
      floor = nextFloor
      context become returnState
    }
    case Tick => context become floorChangingTimeout(tickCounter + 1, nextFloor, returnState)
  }

}
