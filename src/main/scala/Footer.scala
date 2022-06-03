import scodec.codecs.*

case class Footer(
  worldName: String,
  worldId: Long
)

object Footer:
  def footer = (ignore(7) ~> bool.unit(true) :: worldName :: worldId).dropUnits.as[Footer]

  def worldName = variableSizeBytes(uint8L, ascii)
  def worldId = uint32L