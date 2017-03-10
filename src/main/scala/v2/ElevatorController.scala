package v2

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.collection.immutable.Queue

/**
  * Created by sturmm on 26.11.16.
  */
class ElevatorController(noOfElevators: Int = 1) extends Actor with ActorLogging {
  import v2.Protocol._

  var elevators: List[ActorRef] = Nil
  var requests: Queue[ServiceRequest] = Queue()

  override def preStart(): Unit = {
    import scala.concurrent.duration._
    import scala.concurrent.ExecutionContext.Implicits.global

    (1 to noOfElevators) foreach { i =>
      context.actorOf(Props[Elevator], s"Elevator-No-$i")
    }
    context.system.scheduler.schedule(0 milliseconds, 250 milliseconds, self, Tick)
  }

  override def receive: Receive = {
    case Tick => {
      log.debug("Tick")
      elevators foreach(_ ! Tick)
    }

    case ElevatorCreated(e) => {
      log.debug(s"Created ${e.path}")
      elevators = e :: elevators
    }

    case AnyServiceRequest(currentFloor) if requests.nonEmpty => {

      val (req, queue) = requests.dequeue
      requests = queue

      sender ! req
    }

    case req: ServiceRequest => {
      log.debug(s"Incomming ServiceRequest: $req")

      requests = requests :+ req
    }
  }


}
