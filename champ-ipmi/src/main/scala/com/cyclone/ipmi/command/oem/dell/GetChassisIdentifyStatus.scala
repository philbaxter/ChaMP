package com.cyclone.ipmi.command.oem.dell

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, NetworkFunction, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand}

/**
  * GetChassisIdentifyStatus
  */
object GetChassisIdentifyStatus {

  sealed trait IdentifyStatus

  object IdentifyStatus {

    implicit val decoder: Decoder[IdentifyStatus] = new Decoder[IdentifyStatus] {

      def decode(data: ByteString): IdentifyStatus = data(0).toUnsignedInt match {
        case 0x00 => Off
        case 0x01 => On
      }
    }

    implicit val encoder: Coder[IdentifyStatus] = new Coder[IdentifyStatus] {

      def encode(a: IdentifyStatus): ByteString = {
        a match {
          case Off => ByteString(0x00)
          case On  => ByteString(0x01)
        }
      }
    }

    case object Off extends IdentifyStatus

    case object On extends IdentifyStatus

  }

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val identifyStatus = is.read(1).as[IdentifyStatus]

        CommandResult(identifyStatus)
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(identifyStatus: IdentifyStatus) extends IpmiCommandResult

  case object Command extends IpmiStandardCommand {
    implicit val coder: Coder[Command.type] = new Coder[Command.type] {
      def encode(request: Command.type): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command.type, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command.type, CommandResult]

    val networkFunction: NetworkFunction = NetworkFunction.OemFree30hRequest
    val commandCode = CommandCode(0x32)
  }

}
