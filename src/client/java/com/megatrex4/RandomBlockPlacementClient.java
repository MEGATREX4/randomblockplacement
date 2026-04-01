package com.megatrex4;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;

import java.util.Random;

import static com.megatrex4.RandomBlockPlacement.MOD_ID;

public class RandomBlockPlacementClient implements ClientModInitializer {
	private static boolean randomPlacementMode = false;
	private static final Random random = new Random();
	private boolean wasRightClicking = false;
	private static final RandomBlockPlacementClient INSTANCE = new RandomBlockPlacementClient();
	private static final Identifier ICON_TEXTURE = Identifier.parse(MOD_ID + ":textures/gui/rblock.png");
	private static final Identifier ICON_LAYER_ID = Identifier.parse(MOD_ID + ":random_icon");

	@Override
	public void onInitializeClient() {
		KeyBindings.registerKeyBindings();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null && client.screen == null) {
				boolean isRightClicking = client.options.keyUse.isDown();
				boolean isPlacingBlock = isRightClicking && !wasRightClicking;
				if (randomPlacementMode && isPlacingBlock) {
					handleBlockPlacement(client.player);
					wasRightClicking = true;
				} else if (!isRightClicking) {
					wasRightClicking = false;
				}
			}
		});

		// Register HUD element after crosshair
		HudElementRegistry.attachElementAfter(
				VanillaHudElements.CROSSHAIR,
				ICON_LAYER_ID,
				(graphics, deltaTracker) -> {
					if (randomPlacementMode) {
						renderIcon(graphics);
					}
				}
		);
	}

	public static void onRandomPlaceKeyPressed() {
		randomPlacementMode = !randomPlacementMode;
	}

	public void handleBlockPlacement(LocalPlayer player) {
		if (randomPlacementMode && player.getMainHandItem().getItem() instanceof BlockItem) {
			randomizeHotbarSlot(player);
		}
	}

	private static void randomizeHotbarSlot(LocalPlayer player) {
		// Use vanilla's built-in getSelectedSlot()
		int currentSlot = player.getInventory().getSelectedSlot();
		int blockCount = 0;
		int[] blockSlots = new int[9];
		for (int i = 0; i < 9; i++) {
			if (player.getInventory().getItem(i).getItem() instanceof BlockItem) {
				blockSlots[blockCount++] = i;
			}
		}
		if (blockCount <= 1) {
			return;
		}
		int probability = 100 / blockCount;
		int randomValue = random.nextInt(100);
		int accumulatedProbability = 0;
		for (int i = 0; i < blockCount; i++) {
			accumulatedProbability += probability;
			if (randomValue < accumulatedProbability) {
				// Use vanilla's built-in setSelectedSlot()
				player.getInventory().setSelectedSlot(blockSlots[i]);
				break;
			}
		}
	}

	public static RandomBlockPlacementClient getInstance() {
		return INSTANCE;
	}

	private void renderIcon(GuiGraphicsExtractor graphics) {
		Minecraft client = Minecraft.getInstance();
		int screenWidth = client.getWindow().getGuiScaledWidth();
		int screenHeight = client.getWindow().getGuiScaledHeight();
		int iconSize = 16;
		int x = (screenWidth - iconSize) / 2;
		int y = (screenHeight - iconSize) / 2 - 13;
		graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				ICON_TEXTURE,
				x, y,
				0.0f, 0.0f,
				iconSize, iconSize,
				iconSize, iconSize
		);
	}
}