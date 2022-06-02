import scodec.codecs.*

case class TownManager(item: Long, x: Long, y: Long)

object TownManager:
  def townManager =
    listOfN(int32L, (uint32L :: uint32L :: uint32L).as[TownManager])