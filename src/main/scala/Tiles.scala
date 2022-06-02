import scodec.codecs.*
import scodec.Err
import scodec.Codec

case class Tile(
    flags: Flags,
    tileType: Option[Int],
    frames: Option[Frames],
    color: Option[Int],
    wall: Option[Int],
    wallColor: Option[Int],
    liquid: Option[Int],
    wallExtraByte: Option[Int],
    duplicateTiles: Int
)

case class Frames(x: Int, y: Int)

case class Flags(
    flags1: Flags1,
    flags2: Option[Flags2],
    flags3: Option[Flags3]
)

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

object Tiles:
  def tiles(header: Header) = 
    fixedSizeBytes(
      header.positions(2) - header.positions(1),
      list(Tiles.tile(header.importance))
    )

  def tile(important: List[Boolean]) = flags.flatPrepend(flags =>
    val tileTypeAndFrames = tileType(flags.flags1.isActive, flags.flags1.typeHasExtraByte).flatZip(frames(important))

    val conditionals =
      conditionalOpt(flags.flags3, _.tileHasColorByte, tileColor) ::
        conditional (flags.flags1.hasWallByte, tileWall) ::
        conditionalOpt(flags.flags3, _.tileHasWallColorByte, wallColor) ::
        conditional(flags.flags1.liquidType != 0, tileLiquid) ::
        conditionalOpt(flags.flags3, _.tileHasExtraWallByte, tileWallExtraByte)

    tileTypeAndFrames ++ conditionals :+ numberOfDuplicateTiles(flags.flags1.sizeOfRepeatTileCount)
  )
  .as[Tile]

  def conditionalOpt[A, C](option: Option[A], f: A => Boolean, codec: Codec[C]) =
    conditional(option.fold(false)(f), codec)

  def flags =
    val flagsTuple = flags1.as[Flags1] :: optional(bool, flags2.as[Flags2] :: optional(bool, flags3.as[Flags3]))
    flagsTuple
      .xmap(
        f => Flags(f(0), f(1).map(f => f(0)), f(1).flatMap(f => f(1))),
        f => (f.flags1, f.flags2.map(f2 => (f2, f.flags3)))
      )

  def tileType(isActive: Boolean, typeHasExtraByte: Boolean) =
    conditional(isActive, if typeHasExtraByte then uint16L else uint8L)

  def frames(important: List[Boolean])(tileType: Option[Int]) =
    tileType match {
      case Some(tileType) =>
        conditional(important(tileType), (uint16L :: uint16L).as[Frames])
      case None =>
        provide(None)
    }

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
