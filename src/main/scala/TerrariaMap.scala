import scodec.bits.ByteVector

case class TerrariaMap(
  header: Header,
  tiles: Vector[Vector[Tile]],
  chests: List[Chest],
  signs: List[Sign],
  npcs: List[NPC],
  activeNPCs: List[ActiveNPC],
  tileEntities: List[TileEntity],
  pressurePlates: List[PressurePlate],
  townManager: List[TownManager],
  bestiary: Bestiary,
  creativePowers: ByteVector,
  footer: Footer
)

object TerrariaMap:
  val codec = codecTuple.as[TerrariaMap]

  def codecTuple =
    Header.header
      .flatZip(header => Tiles.tiles(header))
      :+ Chests.chests
      :+ Signs.signs
      :+ NPCs.npcs
      :+ NPCs.activeNPCs
      :+ TileEntities.tileEntities
      :+ PressurePlates.pressurePlates
      :+ TownManager.townManager
      :+ Bestiary.bestiary
      :+ CreativePowers.creativePowers
      :+ Footer.footer