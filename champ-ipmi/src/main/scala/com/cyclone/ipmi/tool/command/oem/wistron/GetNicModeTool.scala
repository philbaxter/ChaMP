package com.cyclone.ipmi.tool.command.oem.wistron

import com.cyclone.command.TimeoutContext
import com.cyclone.ipmi.IpmiError
import com.cyclone.ipmi.command.oem.LanSource
import com.cyclone.ipmi.command.oem.wistron.GetLanSource
import com.cyclone.ipmi.tool.command.IpmiCommands.{CommandExecutor, Ctx}
import com.cyclone.ipmi.tool.command.{IpmiCommands, IpmiToolCommand, IpmiToolCommandResult}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.EitherT._
import scalaz.Scalaz._
import scalaz.\/

import scala.concurrent.Future

/**
  * [[IpmiToolCommand]] that wraps the Wistron [[GetLanSource]] low-level command.
  */
object GetNicModeTool {

  object Command extends IpmiToolCommand {
    implicit val executor: CommandExecutor[Command.type, Result] =
      new CommandExecutor[Command.type, Result] {

        def execute(command: Command.type)(implicit ctx: Ctx): Future[IpmiError \/ Result] = {
          implicit val timeoutContext: TimeoutContext = ctx.timeoutContext
          import ctx._

          val result = for {
            cmdResult <- eitherT(connection.executeCommandOrError(GetLanSource.Command()))
          } yield Result(cmdResult.lanSourceSetting)

          result.run
        }
      }

    def description() = "wistron get nic-mode"
  }

  case class Result(lanSourceSetting: LanSource) extends IpmiToolCommandResult

}