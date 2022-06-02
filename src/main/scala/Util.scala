import scodec.Codec
import scodec.codecs.*

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