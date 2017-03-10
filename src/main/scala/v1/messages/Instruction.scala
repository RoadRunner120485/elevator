package v1.messages

/**
  * Created by sturmm on 25.11.16.
  */
trait Instruction {
  def level: Int
}

case class MoveUp(level: Int) extends Instruction
case class MoveDown(level: Int) extends Instruction
case class OpenDoors(level: Int = 0) extends Instruction
