# Random Block Placement Mod

## Overview

The **Random Block Placement Mod** is a Minecraft mod designed to enhance gameplay by introducing a randomized block placement feature. When activated, this mod will randomly select which block to place from your hotbar, adding an element of unpredictability and excitement to block placement.

## Features  
- **Random block selection** from your hotbar when placing blocks.  
- **Toggleable mode** using a customizable keybind (default: `R`).  
- **HUD Indicator** to show when randomization is active.  
- **Supports all block items** from Vanilla and mods.  

## How to Use  
1. Press **`R`** to toggle **Random Block Placement Mode**.  
2. When enabled, every time you place a block, a random block from your hotbar is selected.  
3. A small icon appears on the HUD when the mode is active.  

## Installation

1. **Download**: Get the latest release from the [Releases](https://github.com/yourusername/random-block-placement/releases) page.
2. **Install**:
   - Place the downloaded `.jar` file into your Minecraft `mods` folder.
   - Make sure you have [Fabric](https://fabricmc.net/) and [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) installed.
3. **Launch Minecraft**: Start Minecraft with the Fabric loader to see the mod in action.

## Usage

- **Toggle Random Placement**: Press the designated key (default: `R`) to toggle the random block placement mode.
- **Block Placement**: When the mod is active, blocks will be randomly selected from your hotbar when you place them.

## Gradient Building

Gradient Placement lets you build a structure with blocks changing across space, while still placing blocks wherever you need them. Press `X` to toggle the mode, or press `Shift+X` to open its settings. Both keys can be changed in Minecraft's Controls menu. The gradient icon on the HUD shows when a placement mode is active.

Before building, arrange the block types you want to use in hotbar slots 1–9. The mod scans those slots from left to right and uses every non-empty block item it finds as an ordered color stop; empty slots and non-block items are skipped. You do not need to fill all nine slots. For example, six block types can be spread across a gradient that is nine or eleven blocks long.

The first successful placement after enabling Gradient Placement becomes the anchor and uses the first block in the palette. The configured length is the number of block positions from that anchor through the final color, including both endpoints. For each later placement, the mod measures its position relative to the anchor, maps that progress across the available palette stops, and selects the corresponding hotbar block before placement. This means you can place the rest of the build in any order. Positions beyond the configured length use the last palette block.

The settings screen lets you choose the gradient length, orientation, and shape. Horizontal linear gradients follow the direction you were facing when you placed the anchor; radial and square gradients measure distance outward from it. Vertical orientation maps progress by height. Optional dithering varies which neighboring palette stop is chosen at nearby positions, producing a more blended appearance when viewed from a distance.

Gradient length, orientation, shape, and dithering are saved as client settings. The active mode and anchor are temporary: the anchor resets when you disable Gradient Placement or leave the world. Random Placement (`R`) and Gradient Placement cannot be active at the same time; the mod displays an actionbar message if you try to enable one while the other is active.

## Configuration

- The mod can be configured via keybindings. Use the in-game controls menu to set your preferred key for toggling the random placement mode.

## Contributing

Contributions are welcome! If you have suggestions, bug reports, or want to contribute code, please create an issue or a pull request. Follow these guidelines for contributing:

1. Fork the repository.
2. Create a new branch for your feature or fix.
3. Commit your changes.
4. Push to your forked repository.
5. Open a pull request to the main repository.


---
