package v1.strategy

/**
  * Created by sturmm on 25.11.16.
  */
case class ElevatorState(level: Int, movingDirection: Direction)

sealed trait Direction
object Stopped extends Direction
object MovingUpward extends Direction
object MovingDownward extends Direction