import scodec.codecs.*

case class Item(stackSize: Int, netDefaults: Int, prefix: Int)

case class Chest(x: Long, y: Long, name: String, slots: List[Item])

object Chests:
  val slotCount = 40

  def chests = listOfN(chestCount <~ chestSize.unit(slotCount), chest.as[Chest])

  def chest = chestX :: chestY :: chestName :: slots

  def slots = listOfN(provide(slotCount), slot).xmap(
    slots => slots.collect { case Some(slot) => slot },
    items => items.map(Some(_)) ++ List.fill(slotCount - items.length)(None)
  )

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