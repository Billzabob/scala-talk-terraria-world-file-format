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
  // val num = 542_155
  val Successful(newBits) = TerrariaMap.codec.encode(map)
  val same = newBits == bits
  File.write(newBits)
  println(same)
  println(bits.length)
  println(newBits.length)
