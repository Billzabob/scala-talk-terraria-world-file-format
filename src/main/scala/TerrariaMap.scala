import scala.deriving.Mirror
import scodec.{Attempt, Codec, DecodeResult, SizeBound}
import scodec.bits.{BitVector, ByteVector}
import scodec.codecs.*

case class TerrariaMap(
    header: Header,
    tiles: Vector[Tile],
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
  val codec = new Codec[TerrariaMap] {
    def decode(bits: BitVector): Attempt[DecodeResult[TerrariaMap]] =
      val codec =
        Header.header.flatMap(headerTuple =>
          val positions = headerTuple(4)
          val header = summon[Mirror.Of[Header]].fromProduct(headerTuple.take(4) ++ headerTuple.drop(5))
          provide(header)
            :: fixedSizeBytes(positions(2) - positions(1), Tiles.tiles(header.importance, header.maxTilesY))
            :: Chests.chests
            :: Signs.signs
            :: NPCs.npcs
            :: NPCs.activeNPCs
            :: TileEntities.tileEntities
            :: PressurePlates.pressurePlates
            :: TownManager.townManager
            :: Bestiary.bestiary
            :: CreativePowers.creativePowers
            :: Footer.footer
        )

      codec.as[TerrariaMap].decode(bits)

    def encode(map: TerrariaMap): Attempt[BitVector] =
      for
        tiles <- Tiles.tiles(map.header.importance, map.header.maxTilesY).encode(map.tiles)
        chests <- Chests.chests.encode(map.chests)
        signs <- Signs.signs.encode(map.signs)
        npcs <- NPCs.npcs.encode(map.npcs)
        activeNPCs <- NPCs.activeNPCs.encode(map.activeNPCs)
        tileEntities <- TileEntities.tileEntities.encode(map.tileEntities)
        pressurePlates <- PressurePlates.pressurePlates.encode(map.pressurePlates)
        townManager <- TownManager.townManager.encode(map.townManager)
        bestiary <- Bestiary.bestiary.encode(map.bestiary)
        creativePowers <- CreativePowers.creativePowers.encode(map.creativePowers)
        footer <- Footer.footer.encode(map.footer)
        positions = 151 :: List(tiles.intSize.get, chests.intSize.get, signs.intSize.get, npcs.intSize.get + activeNPCs.intSize.get, tileEntities.intSize.get, pressurePlates.intSize.get, townManager.intSize.get, bestiary.intSize.get, creativePowers.intSize.get).map(_ / 8).scan(3295)(_ + _)
        header = Tuple.fromProductTyped(map.header)
        header2 = header.take(4) ++ positions *: header.drop(4)
        headerBits <- Header.header.encode(header2)
      yield 
        headerBits ++
          tiles ++
          chests ++
          signs ++
          npcs ++
          activeNPCs ++
          tileEntities ++
          pressurePlates ++
          townManager ++
          bestiary ++
          creativePowers ++
          footer

    def sizeBound: SizeBound = SizeBound.unknown
  }
