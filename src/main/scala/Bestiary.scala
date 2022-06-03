import scodec.codecs.*

case class Bestiary(
  kills: List[Kills],
  sights: List[Sights],
  chats: List[Chat]
)

case class Kills(name: String, value: Int)
case class Sights(name: String)
case class Chat(name: String)

object Bestiary:
  def bestiary = (kills :: sights :: chats).as[Bestiary]

  def kills = listOfN(int32L, (name :: value).as[Kills])
  def sights = listOfN(int32L, name.as[Sights])
  def chats = listOfN(int32L, name.as[Chat])

  def name = variableSizeBytes(uint8L, ascii)
  def value = int32L