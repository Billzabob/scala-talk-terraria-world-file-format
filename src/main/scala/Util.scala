import scodec.Codec
import scodec.codecs.*
import scala.annotation.tailrec

object Util:
  def takeWhile[A](bool: Codec[Boolean], codec: Codec[A]): Codec[List[A]] =
    bool.consume(more =>
      if more then
        cons(codec, takeWhile(bool, codec))
      else
        empty[A]
    ) (_.nonEmpty)

  def cons[A](a: Codec[A], b: Codec[List[A]]): Codec[List[A]] =
    (a :: b).xmap((a, b) => a :: b, list => (list.head, list.tail))

  def empty[A]: Codec[List[A]] = provide(Nil)

  def runLineEncode[A](list: List[A]): List[(A, Int)] =
    @tailrec
    def loop(rest: List[A], acc: List[List[A]]): List[List[A]] =
      rest match
        case Nil =>
          acc
        case list =>
          val (head, tail) = list.span(_ == list.head)
          loop(tail, head :: acc)

    loop(list, Nil).map(a => (a.head, a.length - 1)).reverse

  def runLineDecode[A](encoded: List[(A, Int)]) =
    encoded.flatMap { case (a, count) =>
      List.fill(count + 1)(a)
    }

  def tileIndexToCoords(index: Int, header: Header) =
      (index / header.maxTilesY, index % header.maxTilesY)

  def tileCoordsToIndex(coords: (Int, Int), header: Header) =
      coords._1 * header.maxTilesY + coords._2
