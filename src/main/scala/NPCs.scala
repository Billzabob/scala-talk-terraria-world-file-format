import scodec.codecs.*
import Util.takeWhile

case class Position(x: Float, y: Float)
case class HomePosition(x: Long, y: Long)

case class NPC(
    defaults: Long,
    givenName: String,
    position: Position,
    homeless: Boolean,
    homePosition: HomePosition,
    townNPC: Long
)

case class ActiveNPC(
    defaults: Long,
    position: Position
)

object NPCs:
  def npcs = takeWhile(boolean, npc.as[NPC])
  def activeNPCs = takeWhile(boolean, npc2.as[ActiveNPC])

  def npc =
    defaults ::
      givenName ::
      position ::
      homeless ::
      homePosition ::
      townNPC

  def npc2 =
    defaults ::
      position

  def defaults = uint32L
  def givenName = variableSizeBytes(uint8L, ascii)
  def position = (floatL :: floatL).as[Position]
  def homeless = boolean
  def homePosition = (uint32L :: uint32L).as[HomePosition]
  def townNPC = uint32L

  def boolean = ignore(7) ~> bool
