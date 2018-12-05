package com.cyclone.ipmi.command.oem.supermicro

import akka.util.ByteString
import com.cyclone.ipmi.codec._
import com.cyclone.ipmi.command.{CommandCode, StatusCodeTranslator}
import com.cyclone.ipmi.protocol.packet.{CommandResultCodec, IpmiCommandResult, IpmiStandardCommand, NetworkFunction}
import com.google.common.base.Charsets

/**
  * GetExtraFirmwareInfo
  */
object GetExtraFirmwareInfo {

  object CommandResult {
    implicit val decoder: Decoder[CommandResult] = new Decoder[CommandResult] {

      def decode(data: ByteString): CommandResult = {
        val iterator = data.iterator
        val is = iterator.asInputStream

        val firmwareMajorVersion = is.read(4).as[Int]
        val firmwareMinorVersion = is.read(4).as[Int]
        val firmwareSubVersion = is.read(4).as[Int]
        val firmwareBuildNumber = is.read(4).as[Int]
        val hardwareId = is.readByte
        val firmwareTag = iterator.toByteString.decodeString(Charsets.US_ASCII.name())

        CommandResult(
          firmwareMajorVersion,
          firmwareMinorVersion,
          firmwareSubVersion,
          firmwareBuildNumber,
          hardwareId,
          firmwareTag
        )
      }
    }

    implicit val statusCodeTranslator: StatusCodeTranslator[CommandResult] =
      StatusCodeTranslator[CommandResult]()
  }

  case class CommandResult(
    firmwareMajorVersion: Int,
    firmwareMinorVersion: Int,
    firmwareSubVersion: Int,
    firmwareBuildNumber: Int,
    hardwareId: Byte,
    firmwareTag: String
  ) extends IpmiCommandResult

  object Command {
    implicit val coder: Coder[Command] = new Coder[Command] {
      def encode(request: Command): ByteString = ByteString.empty
    }

    implicit val codec: CommandResultCodec[Command, CommandResult] =
      CommandResultCodec.commandResultCodecFor[Command, CommandResult]
  }

  case class Command() extends IpmiStandardCommand {

    val networkFunction: NetworkFunction = NetworkFunction.OemFree3chRequest
    val commandCode = CommandCode(0x20.toByte)
  }

}