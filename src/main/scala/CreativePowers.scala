import scodec.codecs.*

object CreativePowers:
  // Don't care about this for now, plus it's a really weird one to decode/encode
  def creativePowers = fixedSizeBytes(31, bytes)