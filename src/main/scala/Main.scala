import java.nio.charset.Charset
import java.nio.file.Files.readAllBytes
import java.nio.file.Paths
import scala.util.chaining.*
import scodec.*
import scodec.bits.*
import scodec.codecs.*
import scodec.Attempt.Successful

@main def hello: Unit =
  val result = "/Users/nick.hallstrom@divvypay.com/Downloads/Nicks_World.wld"
    .pipe(p => Paths.get(p))
    .pipe(readAllBytes)
    .pipe(BitVector.apply)
    .pipe(codec.decode)

  val Successful(DecodeResult(parsed, rest)) = result
  println(parsed._2.length)
  println(parsed._2.reverse.take(10).map(_._2))
  println(parsed._1.positions)

def codec =
  header
    .as[Header]
    .flatZip(header =>
      fixedSizeBytes(
        header.positions(2) - header.positions(1),
        list(WorldTiles.tile(header.importance))
      )
    )

def header =
  version ::
    relogic ~>
    fileType ::
    revision ::
    isFavorite ::
    positions ::
    importance ::
    worldName ::
    seed ::
    worldGenVersion ::
    uuid ::
    worldId ::
    leftWorld ::
    rightWorld ::
    topWorld ::
    bottomWorld ::
    maxTilesY ::
    maxTilesX ::
    gameMode ::
    drunkWorld ::
    goodWorld ::
    tenthAnniversaryWorld ::
    dontStarveWorld ::
    notTheBeesWorld ::
    creationTime ::
    moonType ::
    treeX ::
    treeStyle ::
    caveBackX ::
    caveBackStyle ::
    iceBackStyle ::
    jungleBackStyle ::
    hellBackStyle ::
    spawnTileX ::
    spawnTileY ::
    worldSurface ::
    rockLayer ::
    tempTime ::
    tempDayTime ::
    tempMoonPhase ::
    tempBloodMoon ::
    eclipse ::
    dungeonX ::
    dungeonY ::
    crimson ::
    downedBoss1 ::
    downedBoss2 ::
    downedBoss3 ::
    downedQueenBee ::
    downedMechBoss1 ::
    downedMechBoss2 ::
    downedMechBoss3 ::
    downedMechBossAny ::
    downedPlantBoss ::
    downedGolemBoss ::
    downedSlimeKing ::
    savedGoblin ::
    savedWizard ::
    savedMech ::
    downedGoblins ::
    downedClown ::
    downedFrost ::
    downedPirates ::
    shadowOrbSmashed ::
    spawnMeteor ::
    shadowOrbCount ::
    altarCount ::
    hardMode ::
    invasionDelay ::
    invasionSize ::
    invasionType ::
    invasionX ::
    slimeRainTime ::
    sundialCooldown ::
    tempRaining ::
    tempRainTime ::
    tempMaxRain ::
    savedOreTiersCobalt ::
    savedOreTiersMythril ::
    savedOreTiersAdamantite ::
    backgrounds ::
    cloudBGActive ::
    numClouds ::
    windSpeed ::
    anglerWhoFinishedToday ::
    savedAngler ::
    anglerQuest ::
    savedStylist ::
    savedTaxCollector ::
    savedGolfer ::
    invasionSizeStart ::
    tempCultistDelay ::
    npcKillCount ::
    fastForwardTime ::
    downedFishron ::
    downedMartians ::
    downedAncientCultist ::
    downedMoonlord ::
    downedHalloweenKing ::
    downedHalloweenTree ::
    downedChristmasIceQueen ::
    downedChristmasSantank ::
    downedChristmasTree ::
    downedTowerSolar ::
    downedTowerVortex ::
    downedTowerNebula ::
    downedTowerStardust ::
    towerActiveSolar ::
    towerActiveVortex ::
    towerActiveNebula ::
    towerActiveStardust ::
    lunarApocalypseIsUp ::
    tempPartyManual ::
    tempPartyGenuine ::
    tempPartyCooldown ::
    partyCelebratingNPCs ::
    tempSandstormHappening ::
    tempSandstormTimeLeft ::
    tempSandstormSeverity ::
    tempSandstormIntendedSeverity ::
    savedBartender ::
    downedInvasionT1 ::
    downedInvasionT2 ::
    downedInvasionT3 ::
    backgrounds2 ::
    combatBookWasUsed ::
    tempLanternNightCooldown ::
    tempLanternNightGenuine ::
    tempLanternNightManual ::
    tempLanternNightNextNightIsGenuine ::
    treeTopVariations ::
    forceHalloweenForToday ::
    forceXMasForToday ::
    savedOreTiersCopper ::
    savedOreTiersIron ::
    savedOreTiersSilver ::
    savedOreTiersGold ::
    boughtCat ::
    boughtDog ::
    boughtBunny ::
    downedEmpressOfLight ::
    downedQueenSlime ::
    downedDeerclops

def version = int32L

def relogic =
  val Right(relogic) = ByteVector.encodeAscii("relogic")
  constant(relogic)

// TODO: to enum
def fileType = int8L

def revision = int32L
def isFavorite = bool(64)
def positions = listOfN(int16L, int32L)
// TODO: Read byte at a time and then right to left
def importance = byteAligned(
  listOfN(int16L, bool).xmap(
    _.grouped(8).map(_.reverse).flatten.toList,
    _.grouped(8).map(_.reverse).flatten.toList
  )
)
def worldName = string
def seed = string
def worldGenVersion = int64L
def uniqueId = uuid
def worldId = int32L
def leftWorld = int32L
def rightWorld = int32L
def topWorld = int32L
def bottomWorld = int32L
def maxTilesY = int32L
def maxTilesX = int32L
def gameMode = int32L
def drunkWorld = flag
def goodWorld = flag
def tenthAnniversaryWorld = flag
def dontStarveWorld = flag
def notTheBeesWorld = flag
def creationTime = int64L
def moonType = int8L
def treeX = listOfN(provide(3), int32L)
def treeStyle = listOfN(provide(4), int32L)
def caveBackX = listOfN(provide(3), int32L)
def caveBackStyle = listOfN(provide(4), int32L)
def iceBackStyle = int32L
def jungleBackStyle = int32L
def hellBackStyle = int32L
def spawnTileX = int32L
def spawnTileY = int32L
def worldSurface = doubleL
def rockLayer = doubleL
def tempTime = doubleL
def tempDayTime = flag
def tempMoonPhase = int32L
def tempBloodMoon = flag
def eclipse = flag
def dungeonX = int32L
def dungeonY = int32L
def crimson = flag
def downedBoss1 = flag
def downedBoss2 = flag
def downedBoss3 = flag
def downedQueenBee = flag
def downedMechBoss1 = flag
def downedMechBoss2 = flag
def downedMechBoss3 = flag
def downedMechBossAny = flag
def downedPlantBoss = flag
def downedGolemBoss = flag
def downedSlimeKing = flag
def savedGoblin = flag
def savedWizard = flag
def savedMech = flag
def downedGoblins = flag
def downedClown = flag
def downedFrost = flag
def downedPirates = flag
def shadowOrbSmashed = flag
def spawnMeteor = flag
def shadowOrbCount = uint8L
def altarCount = int32L
def hardMode = flag
def invasionDelay = int32L
def invasionSize = int32L
def invasionType = int32L
def invasionX = doubleL
def slimeRainTime = doubleL
def sundialCooldown = uint8L
def tempRaining = flag
def tempRainTime = int32L
def tempMaxRain = float
def savedOreTiersCobalt = int32L
def savedOreTiersMythril = int32L
def savedOreTiersAdamantite = int32L
def backgrounds = listOfN(provide(8), int8L)
def cloudBGActive = int32L
def numClouds = int16L
def windSpeed = float
def anglerWhoFinishedToday = listOfN(int32L, string)
def savedAngler = flag
def anglerQuest = int32L
def savedStylist = flag
def savedTaxCollector = flag
def savedGolfer = flag
def invasionSizeStart = int32L
def tempCultistDelay = int32L
def npcKillCount = listOfN(int16L, int32L)
def fastForwardTime = flag
def downedFishron = flag
def downedMartians = flag
def downedAncientCultist = flag
def downedMoonlord = flag
def downedHalloweenKing = flag
def downedHalloweenTree = flag
def downedChristmasIceQueen = flag
def downedChristmasSantank = flag
def downedChristmasTree = flag
def downedTowerSolar = flag
def downedTowerVortex = flag
def downedTowerNebula = flag
def downedTowerStardust = flag
def towerActiveSolar = flag
def towerActiveVortex = flag
def towerActiveNebula = flag
def towerActiveStardust = flag
def lunarApocalypseIsUp = flag
def tempPartyManual = flag
def tempPartyGenuine = flag
def tempPartyCooldown = int32L
def partyCelebratingNPCs = listOfN(int32L, int32L)
def tempSandstormHappening = flag
def tempSandstormTimeLeft = int32L
def tempSandstormSeverity = float
def tempSandstormIntendedSeverity = float
def savedBartender = flag
def downedInvasionT1 = flag
def downedInvasionT2 = flag
def downedInvasionT3 = flag
def backgrounds2 = listOfN(provide(5), int8L)
def combatBookWasUsed = flag
def tempLanternNightCooldown = int32L
def tempLanternNightGenuine = flag
def tempLanternNightManual = flag
def tempLanternNightNextNightIsGenuine = flag
def treeTopVariations = listOfN(int32L, int32L)
def forceHalloweenForToday = flag
def forceXMasForToday = flag
def savedOreTiersCopper = int32L
def savedOreTiersIron = int32L
def savedOreTiersSilver = int32L
def savedOreTiersGold = int32L
def boughtCat = flag
def boughtDog = flag
def boughtBunny = flag
def downedEmpressOfLight = flag
def downedQueenSlime = flag
def downedDeerclops = flag

def string = variableSizeBytes(uint8L, ascii)
def flag = bool(8)
