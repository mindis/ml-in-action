package com.purogurammingu.svm

import scala.annotation.tailrec
import scala.io.Source
import scala.util.Random

/**
  * Created by mateusz on 3/4/16.
  */
object Dataset {

  def load(file: String) = {
    Source.fromFile(getClass.getResource(file).getPath).getLines().map { line =>
      line.stripLineEnd.split("\t").map { data =>
        LabeledPoint(Point(data(0), data(1)), data(2))
      }
    }
  }

  @tailrec
  def selectJrand(i: Int, m: Int): Int = {
    Random.nextInt(m) match {
      case x if x == i => selectJrand(i, m)
      case x => x
    }
  }

  def clipAlpha(aj: Int, H: Int, L: Int): Int = Math.max(Math.min(aj, H), L)

}

case class Point(val x: Int, val y: Int)

case class LabeledPoint(val pts: Point, val label: Int)