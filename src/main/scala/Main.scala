import java.nio.file.Files.readAllBytes
import java.nio.file.Paths
import scala.util.chaining.given
import scodec.Attempt.Successful
import scodec.bits.BitVector
import scodec.Attempt.Failure

@main def hello: Unit =
  val bits = File.read
  val Successful(map) = TerrariaMap.codec.decodeValue(bits)
  println("Encoding")
  val Successful(newBits) = TerrariaMap.codec.encode(map)
  File.write(newBits)
