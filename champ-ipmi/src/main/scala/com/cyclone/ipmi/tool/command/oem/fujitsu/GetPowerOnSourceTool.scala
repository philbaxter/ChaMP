package com.cyclone.ipmi.tool.command.oem.fujitsu

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError
import com.cyclone.ipmi.client.IpmiConnection
import com.cyclone.ipmi.command.oem.fujitsu.common.power.GetPowerOnSource
import com.cyclone.ipmi.command.oem.fujitsu.common.power.GetPowerOnSource.PowerOnSource
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.tool.command.{IpmiCommands, IpmiToolCommand, IpmiToolCommandResult}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the Fujitsu [[GetPowerOnSource]] low-level command.
  */
object GetPowerOnSourceTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] =
      new CommandExecutor[Command.type, Result] {

        def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
          import ctx._

          val result = for {
            cmdResult <- eitherT(connection.executeCommandOrError(GetPowerOnSource.Command))
          } yield Result(cmdResult.powerOnSource)

          result.run
        }
      }

    def description() = "fujitsu get power-on-source"
  }

  case class Result(powerOnSource: PowerOnSource) extends IpmiToolCommandResult

}
