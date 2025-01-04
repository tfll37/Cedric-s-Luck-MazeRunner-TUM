Here's a formatted version for your `README.md` file:

# FOP Project

## Update 1

## Update 2 (ver 1.1.0)

### Overview
Finally discovered the issue of the improper movement of the player. The player was restricted to the actual size of the map in pixels, and later when "upscaling" the map, the movement boundaries remained the same. Added new files like a `.md` file, configuration class, class that fixes TiledTileIDs, and manages tiles properties. Added a new copyright-free tile set `Dawnlike.tmx` (created by DawnBringer), and some other assets for testing. Made slight adjustments across other classes to support the new features.

### Changes

#### Files

**New:**
- `gameCONFIG.java` - Stores future game settings (like `UNIT_SCALE`, etc.).
- `README.md` - Stores development notes.
- `TilePropMngr.java` - Manages the properties of the Tiled tiles.
- `TMapTileIDAdjuster.java` - During the Tiled Software export process, the IDs of the tiles on the map get incremented by 1, causing bugs. This class fixes this by incrementing back by 1. (As of this moment, it is not being used, but will still push it.)
- `Gamemap.tmx` - New map used for development testing.
- `Dawnlike.tsx` - New copyright-free tile set created by DawnBringer.
- `TilesNew.png`
- `bush.png` - New icon for testing the character movement and mechanics.

**Old:**
- Tuned the following classes to support the new features: `Background.java`, `GameScreen.java`, `Labyrinth.java`, `Player.java`.

**Removed:**
- `FOP_test.tmx`
