import scodec.codecs.*

case class Sign(text: String, x: Long, y: Long)

object Signs:
  def signs = listOfN(uint16L, sign.as[Sign])

  def sign = signText :: x :: y

  def signText = variableSizeBytes(uint8L, ascii)
  def x = uint32L
  def y = uint32L