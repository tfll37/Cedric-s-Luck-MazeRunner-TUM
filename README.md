Here's a formatted version for your `README.md` file:

# FOP Project

## Update 1
Construction of the game scaffold.

## Update 2 (ver 0.1.0)

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

## Update 3 (ver 0.2.0)

### Overview
Major update of player movement and combat systems. Implemented enemy pathfinding, player combat mechanics, animation systems, and level management. Added multiple map sizes with proper property files. Introduced damage system, hit particles, and basic UI elements. New features include dash mechanics, health tracking, and development improvements across multiple systems.

### Changes

#### Files

**New:**
- `AnimationMNGR.java` - Handles all game animations including character movement and combat
- `CameraMNGR.java` - Camera control system with zoom and follow mechanics
- `Enemy.java` - Enemy AI with pathfinding and combat behaviors
- `GameUI.java` - Health bars and game interface elements
- `HitParticle.java` - Combat effect visualization system
- `LevelMNGR.java` - Level loading and management system
- `MovementREQ.java` - Movement request handling system
- `MovementSYS.java` - Core movement processing system
- `Pathfinding.java` - A* pathfinding implementation for enemy AI
- Level property files (level-1.properties through level-5.properties) - Map configurations
- `SoundMNGR.java` - Audio management system (preliminary)

**Modified:**
- `Player.java` - Added combat mechanics, health system, and animation states
- `GameScreen.java` - Updated to support new combat and movement systems
- `Background.java` - Enhanced to support multiple map sizes
- `MazeRunnerGame.java` - Added support for level management and UI systems
- `MenuScreen.java` - Updated to support level selection
- Other supporting classes adjusted for new systems

**Misc:**
- Due to major code conflicts with video animation loading at MenuScreen initialization, this push won't feature Djordje's update on this aspect of the game. This push is to summarize the progress of the team and provide helpful insight behind the development process of Team `Byteme`

# Update 4 (ver 0.3.0)

## Overview
Major update implementing collectible system and minigame mechanics. Added new interactive elements including dice-based minigames, enhanced particle effects, and projectile combat system. Expanded the game's combat mechanics with fireball abilities and improved visual feedback systems. Implemented Player Spawn locations and Player Exit locations. Added support for Trap game mechanics. Support for a second tile layer is added(crucial for the Trap System). Reworks of other classes to support this update.

## Changes

### Files

**New:**
- `Collectable.java` - Base abstract class for all collectible items in the game. Implements core collection mechanics and bounds checking. Provides foundation for future collectible types

- `Dice.java` - Polished the design of the dice rolling animation. Small reworks.

- `DiceMinigame.java` - Complete dice rolling minigame system. Handles dice animation states and result generation. Manages game duration and result display. Features customizable difficulty through roll duration

- `FireBall.java` - Projectile combat system. Implements directional projectile movement. Handles collision detection with enemies and walls. Features animated projectile effects. Includes damage system integration

- `TrapMNGR.java` - A manager class that handles trap objects - parsing of .properties files for trap location in the maze, manages the trap types, spawning and mechanics associated with them.

- `SpecialAreaHNDLR.java` - A handler class that is responsible for the Player Spawn Area, Level Exit Area, the design of these areas.


**Modified:**
- `Player.java`
    - Added fireball shooting mechanics
    - Implemented cooldown system for abilities
    - Enhanced combat interaction with new projectiles
    - Integrated collectible detection system

- `Enemy.java`
    - Updated to handle projectile damage
    - Enhanced hit detection system
    - Improved death state handling

- `AnimationMNGR.java`
    - Added new animations for dice and fireballs
    - Implemented directional projectile animations
    - Enhanced hit particle effects

- `MazeLoader.java`
  - Reworks for the support of the new Trap System and for the Special Areas.

- `Background.java`
    - Reworks for the support of the new Trap System and for the Special Areas.
- Other supporting classes adjusted for the new systems.

**Misc:**
- The classes are separated into new packages(contextually named) to improve development process.
- Wall width of 64x64 maps is doubled to 2 tiles wide.
- Corridors` walking area inside the maze is doubled to 2 tiles wide.
- Reversion of the updates number

