package v1.actors

import akka.actor.Actor
import akka.event.Logging
import v1.messages._
import v1.strategy.{ElevatorState, ElevatorStrategy, Stopped}

import scala.collection.immutable.Queue

/**
  * Created by sturmm on 25.11.16.
  */
class ElevatorActor extends Actor {
  this: ElevatorStrategy =>

  val log = Logging(context.system, this)

  var requests: Queue[ServiceRequest] = Queue.empty

  override def receive = stopped(0)

  def stopped(currentLevel: Int): Receive = handleInstruction(currentLevel) orElse enqueueServiceRequest orElse handleTick(currentLevel)

  def handleTick(currentLevel: Int): Receive = {
    case Tick if requests.nonEmpty => {
      val (req, tail) = requests.dequeue
      requests = tail
      self ! handleServiceRequest(ElevatorState(currentLevel, Stopped), req)
    }
  }

  def enqueueServiceRequest: Receive = {
    case req: ServiceRequest => requests = requests :+ req
  }

  def handleInstruction(currentLevel: Int): Receive = {
    case OpenDoors(_) => context become doorsOpen(currentLevel)
    case MoveUp(x) => context become movingUp(currentLevel, x)
    case MoveDown(x) => context become movingDown(currentLevel, x)
  }

  def movingUp(currentLevel: Int, targetLevel: Int, tick: Int = 0): Receive = moving(1)(currentLevel, targetLevel, tick)
  def movingDown(currentLevel: Int, targetLevel: Int, tick: Int = 0): Receive = moving(-1)(currentLevel, targetLevel, tick)


  def moving(step: Int)(currentLevel: Int, targetLevel: Int, tick: Int = 0): Receive = {
    case Tick => {
      val currentTick = tick + 1
      val newLevel = if (currentTick % 4 == 0) currentLevel + step else currentLevel

      println(s"Moving ${if(step > 0) "up" else "down"}: $newLevel, $targetLevel, $tick")

      if(newLevel == targetLevel) {
        context become stopped(newLevel)
        self ! OpenDoors()
      } else {
        context become movingUp(newLevel, targetLevel, if(currentTick >= 4) 0 else currentTick)
      }
    }
  }


  def doorsOpen(currentLevel: Int, tick: Int = 0): Receive = {
    case Tick => {
      val currentTick = tick + 1

      if(currentTick % 2 == 0) {
        println(s"Doors closed: $currentLevel, $tick")
        context become stopped(currentLevel)
      } else {
        println(s"Doors open: $currentLevel, $tick")
        context become doorsOpen(currentLevel, tick + 1)
      }
    }
  }

}
