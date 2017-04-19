package com.truelaurel.codingame.metaheuristic.evolutionstrategy

import com.truelaurel.codingame.metaheuristic.model.{Problem, Solution}
import com.truelaurel.codingame.time.Chronometer

import scala.concurrent.duration.Duration

/**
  *
  * @param lambda number of children generated by the parents
  * @param mu     number of parents selected
  * @param duration
  */
class MuPlusLambda(mu: Int, lambda: Int, duration: Duration) {
  require(lambda > 0)
  require(mu > 0 && mu <= lambda)

  private val chrono = new Chronometer(duration)
  private val parentsRange = 0 until lambda
  private val tweakedRange = 0 until lambda / mu

  def search[S <: Solution](problem: Problem[S]): S = {
    chrono.start()
    var parents = parentsRange
      .map(_ => problem.randomSolution())
      .map(s => (s, s.quality()))
      .sortBy(_._2)
      .map(_._1)
    var bestSolution = parents.last
    while (!chrono.willOutOfTime) {
      //truncation selection
      val greatest = parents
        .map(s => (s, s.quality()))
        .sortBy(_._2)
        .map(_._1)
        .takeRight(mu)
      bestSolution = greatest.last
      parents = greatest ++ greatest.flatMap(s => tweakedRange.map(_ => problem.tweakSolution(s)))
    }
    bestSolution
  }

}
