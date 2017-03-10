package v1.strategy

import v1.messages.{Instruction, ServiceRequest}

/**
  * Created by sturmm on 25.11.16.
  */
trait ElevatorStrategy {
  def handleServiceRequest(state: ElevatorState, request: ServiceRequest): Instruction
}
