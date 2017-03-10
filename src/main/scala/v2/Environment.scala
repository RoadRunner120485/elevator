package v2

import akka.actor.{Actor, ActorRef}

/**
  * Created by sturmm on 27.11.16.
  */
class Environment(controller: ActorRef) extends Actor {
  import v2.Protocol._

  val random = new scala.util.Random()
  val floors = context.system.settings.config.getInt("elevator.floors");

  override def preStart(): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._

    context.system.scheduler.schedule(100 milliseconds, 1000 milliseconds, self, Tick)
  }

  override def receive: Receive = {
    case Tick => {
      if (random.nextInt(11) == 10) {
        val sourceFloor: Int = random.nextInt(floors + 1)
        val direction: Direction = if (random.nextBoolean() && sourceFloor < floors) Up else Down
        controller ! ServiceRequest(sourceFloor, direction)
      }
    }
  }
}
