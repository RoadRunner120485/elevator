package v1.strategy
import v1.messages._

/**
  * Created by sturmm on 26.11.16.
  */
trait SimpleElevatorStrategy extends ElevatorStrategy {

  override def handleServiceRequest(state: ElevatorState, request: ServiceRequest): Instruction = {
    state match {
      case ElevatorState(currentLvl, Stopped) =>
        if (currentLvl > request.level) MoveDown(request.level)
        else if(currentLvl < request.level) MoveUp(request.level)
        else OpenDoors(currentLvl)
    }
  }

}
