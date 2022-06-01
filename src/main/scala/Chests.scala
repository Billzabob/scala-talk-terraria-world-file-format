import scodec.Codec
import scodec.codecs.*
import scodec.Err

case class Item(stackSize: Int, netDefaults: Int, prefix: Int)

case class Chest(x: Long, y: Long, name: String, slots: List[Option[Item]])

object Chests:
  val slotCount = 40

  def chests = listOfN(chestCount <~ chestSize.unit(slotCount), chest.as[Chest])

  def chest = chestX :: chestY :: chestName :: listOfN(provide(slotCount), slot)

  def slot = stackSize.consume(stackSize =>
    conditional(stackSize != 0, item(stackSize).as[Item])
  )(_.fold(0)(_.stackSize))

  def item(stackSize: Int) = (provide(stackSize) :: netDefaults :: prefix)

  def stackSize = int16L
  def netDefaults = int32L
  def prefix = uint8L

  def chestCount = uint16L
  def chestSize = uint16L

  def chestX = uint32L
  def chestY = uint32L
  def chestName = variableSizeBytes(uint8L, ascii)