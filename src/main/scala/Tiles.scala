import scala.util.chaining.given
import scodec.Codec
import scodec.codecs.*
import scodec.Err

case class Tile(
    tileType: Option[Int],
    frames: Option[Frames],
    color: Option[Int],
    wall: Option[Int],
    wallColor: Option[Int],
    liquid: Option[Int],
    wallExtraByte: Option[Int],
    liquidType: Int,
    tileSlope: Int,
    tileIsHalfBrick: Boolean,
    tileIsWire3: Boolean,
    tileIsWire2: Boolean,
    tileIsWire1: Boolean,
    isWire4: Boolean,
    isInActive: Boolean,
    isActuator: Boolean
)

case class Frames(x: Int, y: Int)

case class Flags(
    flags1: Flags1,
    flags2: Flags2,
    flags3: Flags3
)

case class Flags1(
    sizeOfRepeatTileCount: Int,
    typeHasExtraByte: Boolean,
    liquidType: Int,
    hasWallByte: Boolean,
    isActive: Boolean
)

def anyFlags2Set(flags2: Flags2) =
  flags2.tileSlope != 0 ||
    flags2.tileIsHalfBrick ||
    flags2.tileIsWire1 ||
    flags2.tileIsWire2 ||
    flags2.tileIsWire3

def emptyFlags2 = Flags2(0, false, false, false, false)

case class Flags2(
    tileSlope: Int,
    tileIsHalfBrick: Boolean,
    tileIsWire3: Boolean,
    tileIsWire2: Boolean,
    tileIsWire1: Boolean
)

def emptyFlags3 = Flags3(false, false, false, false, false, false)

def anyFlags3Set(flags3: Flags3) =
  flags3.tileHasExtraWallByte ||
    flags3.tileIsWire4 ||
    flags3.tileHasWallColorByte ||
    flags3.tileHasColorByte ||
    flags3.tileIsInActive ||
    flags3.tileIsActuator

case class Flags3(
    tileHasExtraWallByte: Boolean,
    tileIsWire4: Boolean,
    tileHasWallColorByte: Boolean,
    tileHasColorByte: Boolean,
    tileIsInActive: Boolean,
    tileIsActuator: Boolean
)

object Tiles:
  def tiles(importance: List[Boolean], maxTilesY: Int) = 
    Tiles.tile(importance)
      .pipe(list)
      .pipe(runLineEncoding(maxTilesY))
      .xmap(_.toVector, _.toList)
  
  def runLineEncoding(groupSize: Int)(codec: Codec[List[(Tile, Int)]]) =
    codec.xmap(encoded => Util.runLineDecode(encoded), _.grouped(groupSize).map(Util.runLineEncode).flatten.toList)

  def tile(important: List[Boolean]) = flags.consume(toTile(important))(toFlags)

  def toTile(important: List[Boolean])(flags: Flags) = 
    val tileTypeAndFrames = tileType(flags.flags1.isActive, flags.flags1.typeHasExtraByte).flatZip(frames(important))

    val conditionals =
      conditional(flags.flags3.tileHasColorByte, tileColor) ::
        conditional(flags.flags1.hasWallByte, tileWall) ::
        conditional(flags.flags3.tileHasWallColorByte, wallColor) ::
        conditional(flags.flags1.liquidType != 0, tileLiquid) ::
        conditional(flags.flags3.tileHasExtraWallByte, tileWallExtraByte)
    
    val providedFlags =
      provide(flags.flags1.liquidType) ::
        provide(flags.flags2.tileSlope) ::
        provide(flags.flags2.tileIsHalfBrick) ::
        provide(flags.flags2.tileIsWire1) ::
        provide(flags.flags2.tileIsWire2) ::
        provide(flags.flags2.tileIsWire3) ::
        provide(flags.flags3.tileIsWire4) ::
        provide(flags.flags3.tileIsInActive) ::
        provide(flags.flags3.tileIsActuator)

    (tileTypeAndFrames ++ conditionals ++ providedFlags).as[Tile] ::
      numberOfDuplicateTiles(flags.flags1.sizeOfRepeatTileCount)

  def toFlags(tile: Tile, numberOfDuplicateTiles: Int) =
    Flags(
      Flags1(
        sizeOfDuplicateTiles(numberOfDuplicateTiles),
        tile.tileType.fold(false)(_ > 255),
        tile.liquidType,
        tile.wall.isDefined,
        tile.tileType.isDefined
      ),
      Flags2(
        tile.tileSlope,
        tile.tileIsHalfBrick,
        tile.tileIsWire3,
        tile.tileIsWire2,
        tile.tileIsWire1
      ),
      Flags3(
        tile.wallExtraByte.isDefined,
        tile.isWire4,
        tile.wallColor.isDefined,
        tile.color.isDefined,
        tile.isInActive,
        tile.isActuator
      )
    )

  def flags =
    val flagsTuple = flags1.as[Flags1] :: optional(bool, flags2.as[Flags2] :: optional(bool, flags3.as[Flags3]))

    flagsTuple
      .xmap(
        f => 
          Flags(
            f(0),
            f(1).map(f => f(0)).getOrElse(emptyFlags2),
            f(1).flatMap(f => f(1)).getOrElse(emptyFlags3)
          ),
        f => 
          val f2 = if anyFlags2Set(f.flags2) || anyFlags3Set(f.flags3) then Some(f.flags2) else None
          (
            f.flags1,
            f2.map(f2 => (f2, if anyFlags3Set(f.flags3) then Some(f.flags3) else None))
          )
      )

  def tileType(isActive: Boolean, typeHasExtraByte: Boolean) =
    conditional(isActive, if typeHasExtraByte then uint16L else uint8L)

  def frames(important: List[Boolean])(tileType: Option[Int]) =
    tileType match
      case Some(tileType) =>
        conditional(important(tileType), (uint16L :: uint16L).as[Frames])
      case None =>
        provide(None)

  def tileColor = uint8L

  def tileWall = uint8L

  def wallColor = uint8L

  def tileLiquid = uint8L

  def tileWallExtraByte = uint8L

  def numberOfDuplicateTiles(sizeOfRepeatTileCount: Int) =
    sizeOfRepeatTileCount match {
      case 0 => provide(0)
      case 1 => uint8L
      case 2 => uint16L
      case _ => fail(Err("Invalid size for number of duplicate tiles"))
    }

  def sizeOfDuplicateTiles(numberOfDuplicateTiles: Int) =
    if numberOfDuplicateTiles == 0 then
      0
    else if numberOfDuplicateTiles < 256 then
      1
    else
      2

  def flags1 =
    sizeOfRepeatTileCount ::
      typeHasExtraByte ::
      liquidType ::
      hasWallByte ::
      isActive

  def sizeOfRepeatTileCount = uint(2)
  def typeHasExtraByte = bool
  // TODO: to enum
  def liquidType = uint(2)
  def hasWallByte = bool
  def isActive = bool

  def flags2 =
    ignore(1) ~>
      tileSlope ::
      tileIsHalfBrick ::
      tileIsWire3 ::
      tileIsWire2 ::
      tileIsWire1

  def tileSlope = uint(2)
  def tileIsHalfBrick = bool
  def tileIsWire3 = bool
  def tileIsWire2 = bool
  def tileIsWire1 = bool

  def flags3 =
    ignore(1) ~>
      tileHasExtraWallByte ::
      tileIsWire4 ::
      tileHasWallColorByte ::
      tileHasColorByte ::
      tileIsInActive ::
      tileIsActuator <~
      ignore(1)

  def tileHasExtraWallByte = bool
  def tileIsWire4 = bool
  def tileHasWallColorByte = bool
  def tileHasColorByte = bool
  def tileIsInActive = bool
  def tileIsActuator = bool
