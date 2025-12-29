package wallapp.process.bridge

import wallapp.process.Process
import wallapp.process.ProcessMock
import wallapp.process.bus.ProcessBus
import wallapp.process.bus.ProcessBusMock
import wallapp.process.receiver.ProcessReceiverMock

class ProcessBridgeMock(override val process: Process = ProcessMock(),
                        override val processBus: ProcessBus = ProcessBusMock(ProcessReceiverMock()))
    : ProcessBridge()