import scodec.Attempt.Successful
import monocle.syntax.all.*

// object Fun:
//   def findCoordinates(id: Int, map: TerrariaMap) =
//     map.tiles
//       .zipWithIndex
//       .filter((tile, _location) => tile.tileType.contains(id))
//       .map((_tile, location) => (location / map.header.maxTilesY, location % map.header.maxTilesY))
