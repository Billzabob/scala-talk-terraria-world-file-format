case class TerrariaMap(
  header: Header,
  tiles: List[Tile],
  chests: List[Chest],
  signs: List[Sign],
  npcs: List[NPC],
  activeNPCs: List[ActiveNPC],
  tileEntities: List[TileEntity],
  pressurePlates: List[PressurePlate],
  townManager: List[TownManager]
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