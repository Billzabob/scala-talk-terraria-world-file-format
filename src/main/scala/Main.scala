import java.nio.file.Files.readAllBytes
import java.nio.file.Paths
import scala.util.chaining.given
import scodec.Attempt.Successful
import scodec.bits.BitVector

@main def hello: Unit =
  val bits = File.read
  val Successful(map) = TerrariaMap.codec.decodeValue(bits)
  val Successful(newBits) = TerrariaMap.codec.encode(map)
  val same = bits.take(newBits.length) == newBits
  File.write(newBits)
  println(same)
