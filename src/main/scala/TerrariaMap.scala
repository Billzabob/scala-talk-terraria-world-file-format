case class TerrariaMap(
  header: Header,
  tiles: List[Tile],
  chests: List[Chest],
  signs: List[Sign]
)

object TerrariaMap:
  def codec = codecTuple.as[TerrariaMap]

  def codecTuple =
    Header.header
      .flatZip(header => Tiles.tiles(header))
      :+ Chests.chests
      :+ Signs.signs