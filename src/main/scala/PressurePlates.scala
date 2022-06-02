import scodec.codecs.*

case class PressurePlate(x: Long, y: Long)

object PressurePlates:
  def pressurePlates = listOfN(int32L, (uint32L :: uint32L).as[PressurePlate])