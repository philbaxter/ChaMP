package com.cyclone.command

import com.cyclone.command.OperationDeadline.{NoDeadline, Overdue, TimeRemains}
import org.scalatest.{FunSuite, Matchers}

import scala.concurrent.duration._

class OperationDeadlineTest extends FunSuite with Matchers {

  test("deadline result overdue when overdue") {
    val dl = ResettingDeadline(0.seconds)

    dl.currentState(MILLISECONDS) shouldBe Overdue
  }

  test("no deadline for optional deadline") {
    OperationDeadline.currentState(None, MILLISECONDS) shouldBe NoDeadline
  }

  test("deadline overdue when less than one timeunit remaining") {
    val dl = ResettingDeadline(100.nanoseconds)

    dl.currentState(MILLISECONDS) shouldBe Overdue
  }

  test("deadline not overdue when equal to one timeunit remaining") {
    val dl = ResettingDeadline(1.millisecond)

    dl.currentState(MILLISECONDS) shouldBe TimeRemains(1.millisecond)
  }
}
