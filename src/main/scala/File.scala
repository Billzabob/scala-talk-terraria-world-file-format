import java.nio.file.{Paths, Files}
import scala.util.chaining.given
import scodec.Attempt.Successful
import scodec.bits.BitVector
import java.io.FileOutputStream

object File:
  def read = 
    "/Users/nick.hallstrom@divvypay.com/Library/Application Support/Terraria/Worlds/Scala_World.wld"
      .pipe(p => Paths.get(p))
      .pipe(Files.readAllBytes)
      .pipe(BitVector.apply)
  
  def write(bits: BitVector) =
    "/Users/nick.hallstrom@divvypay.com/Library/Application Support/Terraria/Worlds/Scala_World2.wld"
      .pipe(p => Paths.get(p))
      .pipe(p => Files.write(p, bits.toByteArray))