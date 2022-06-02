import java.nio.file.Files.readAllBytes
import java.nio.file.Paths
import scala.util.chaining.given
import scodec.Attempt.Successful
import scodec.DecodeResult
import scodec.bits.BitVector

@main def hello: Unit =
  val result = "/Users/nick.hallstrom@divvypay.com/Downloads/Nicks_World.wld"
    .pipe(p => Paths.get(p))
    .pipe(readAllBytes)
    .pipe(BitVector.apply)
    .pipe(TerrariaMap.codec.decode)

  val Successful(DecodeResult(parsed, rest)) = result
  println(parsed.signs)
