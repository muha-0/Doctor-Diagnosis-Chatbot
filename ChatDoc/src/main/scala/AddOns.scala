import slick.jdbc.PostgresProfile.api.*

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.Random
import scala.io.StdIn
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.math.min

def corrector(Original: String): List[String] = {

  val query = db
  val result = query.run(wordsTable.result)

  def editDistance(original: String, ms: String): Int = {
    def dpSet(a: String, b: String): Array[Array[Int]] = {
      val list = List.tabulate(b.length + 1)(_ => -1)
      val r = Array.ofDim[Array[Int]](a.length + 1)
      val v = r.map(_ => list.toArray)
      v
    }

    var dp: Array[Array[Int]] = dpSet(Original, ms)


    def lev(i: Int, j: Int): Int = {
      if (i == 0) return j
      if (j == 0) return i

      dp match
        case answer if (answer(i)(j) != -1) => answer(i)(j)
        case answer if (Original(i - 1) == ms(j - 1)) => {
          dp(i)(j) = lev(i - 1, j - 1)
          dp(i)(j)
        }
        case answer => {
          dp(i)(j) = 1 + min(min(lev(i - 1, j), lev(i, j - 1)), lev(i - 1, j - 1))
          dp(i)(j)
        }
    }

    lev(original.length, ms.length)
  }





  val wordsList: Seq[Words] = Await.result(result, Duration.Inf)

  val candidates = wordsList.foldLeft(Seq[(String, Int)]())((l: Seq[(String, Int)], s: Words) => if(editDistance(Original, s.word) < 2) (s.word, editDistance(Original, s.word)) +: l else l)

  candidates.contains((Original, 0)) match
    case false => candidates.map(_._1).toList
    case true => List()
}

