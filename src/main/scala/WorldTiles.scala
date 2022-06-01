import scodec.codecs.*
import scodec.Err

object WorldTiles:
  def tile(important: List[Boolean]) = flags.flatZip { (flags, rest) =>
    val tileTypeAndFrames = tileType(flags.isActive, flags.typeHasExtraByte).flatZip(frames(important))

    val flags3 = rest.flatMap(_._2)

    val conditionals =
      conditional(flags3.fold(false)(_.tileHasColorByte), tileColor) ::
      conditional(flags.hasWallByte, tileWall) ::
      conditional(flags3.fold(false)(_.tileHasWallColorByte), wallColor) ::
      conditional(flags.liquidType != 0, tileLiquid) ::
      conditional(flags3.fold(false)(_.tileHasExtraWallByte), tileWallExtraByte)

    tileTypeAndFrames ++ conditionals :+ numberOfDuplicateTiles(flags.sizeOfRepeatTileCount)
  }

  def flags =
    flags1.as[Flags1] :: optional(bool, flags2.as[Flags2] :: optional(bool, flags3.as[Flags3]))

  def tileType(isActive: Boolean, typeHasExtraByte: Boolean) =
    conditional(isActive, if typeHasExtraByte then uint16L else uint8L)

  case class Frames(x: Int, y: Int)

  def frames(important: List[Boolean])(tileType: Option[Int]) =
    tileType match {
      case Some(tileType) => conditional(important(tileType), (uint16L :: uint16L).as[Frames])
      case None => provide(None)
    }

  def tileColor = uint8L

  def tileWall = uint8L

  def wallColor = uint8L

  def tileLiquid = uint8L

  def tileWallExtraByte = uint8L

  def numberOfDuplicateTiles(sizeOfRepeatTileCount: Int) = sizeOfRepeatTileCount match {
    case 0 => provide(0)
    case 1 => uint8L
    case 2 => uint16L
    case _ => fail(Err("Invalid size for number of duplicate tiles"))
  }

  case class Flags1(
      sizeOfRepeatTileCount: Int,
      typeHasExtraByte: Boolean,
      liquidType: Int,
      hasWallByte: Boolean,
      isActive: Boolean
  )

  case class Flags2(
      tileSlope: Int,
      tileIsHalfBrick: Boolean,
      tileIsWire3: Boolean,
      tileIsWire2: Boolean,
      tileIsWire1: Boolean
  )

  case class Flags3(
      tileHasExtraWallByte: Boolean,
      tileIsWire4: Boolean,
      tileHasWallColorByte: Boolean,
      tileHasColorByte: Boolean,
      tileIsInActive: Boolean,
      tileIsActuator: Boolean
  )

  case class Tile(
    liquidType: Int,
    isActive: Boolean,
    slope: Int,
    isHalfBrick: Boolean,
    isWire3: Boolean,
    isWire2: Boolean,
    isWire1: Boolean,
    isWire4: Boolean,
    isInActive: Boolean,
    isActuator: Boolean,
    tileType: Int,
    frameX: Int,
    frameY: Int,
    color: Int,
    wall: Int,
    liquidNum: Int
  )

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

  // Size of repeat tile count in bytes
  // 1100 0000
  // Importance index is two bytes instead of 1:
  // 0010 0000
  // 1 = liquid byte, 2 = lava byte, 3 = honey byte
  // 0001 1000
  // Has wall byte:
  // 0000 0100
  // Is active:
  // 0000 0010
  // Has second byte:
  // 0000 0001

  // Tile slope:
  // 0110 0000
  // Tile is half brick:
  // 0001 0000
  // Tile is wire3:
  // 0000 1000
  // Tile is wire2:
  // 0000 0100
  // Tile is wire1:
  // 0000 0010
  // Has third byte:
  // 0000 0001

  // Tile wall has extra byte:
  // 0100 0000
  // Tile is wire4:
  // 0010 0000
  // Tile has wall color byte:
  // 0001 0000
  // Tile has color byte:
  // 0000 1000
  // Tile is inActive:
  // 0000 0100
  // Tile is actuator:
  // 0000 0010