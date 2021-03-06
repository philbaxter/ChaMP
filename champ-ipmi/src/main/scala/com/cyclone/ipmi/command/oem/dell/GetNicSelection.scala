package com.cyclone.ipmi.command.oem.dell

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * GetNicSelection
  */
object GetNicSelection {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val nicSelection = is.read(1).as[NicSelection]

        CommandResult(nicSelection)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(nicSelection: NicSelection) extends IpmiCommandResult

  case object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {
      def encode(request: Command.type): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.OemFree30hRequest
    val commandCode = CommandCode(0x25)
  }

}
