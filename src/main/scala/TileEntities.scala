import scodec.codecs.*

case class TileEntity(id: Int)

object TileEntities:
  def tileEntities = listOfN(int32L, uint8L.as[TileEntity])