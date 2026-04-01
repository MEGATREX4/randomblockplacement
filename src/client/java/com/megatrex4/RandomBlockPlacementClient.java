package com.megatrex4;

import com.megatrex4.config.RandomBlockPlacementConfig;
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
		if (randomPlacementMode) {
			if (!(player.getMainHandItem().getItem() instanceof BlockItem)) {
				switchToAnyBlockSlot(player);
				return;
			}
			randomizeHotbarSlot(player);
		}
	}

	private static void randomizeHotbarSlot(LocalPlayer player) {
		int blockCount = 0;
		int[] blockSlots = new int[9];
		for (int i = 0; i < 9; i++) {
			if (player.getInventory().getItem(i).getItem() instanceof BlockItem
					&& player.getInventory().getItem(i).getCount() > 0) {
				blockSlots[blockCount++] = i;
			}
		}

		if (blockCount == 0) {
			return;
		}

		if (blockCount == 1) {
			player.getInventory().setSelectedSlot(blockSlots[0]);
			return;
		}

		// Pick a random block slot
		int probability = 100 / blockCount;
		int randomValue = random.nextInt(100);
		int accumulatedProbability = 0;
		for (int i = 0; i < blockCount; i++) {
			accumulatedProbability += probability;
			if (randomValue < accumulatedProbability) {
				player.getInventory().setSelectedSlot(blockSlots[i]);
				break;
			}
		}
	}

	private static void switchToAnyBlockSlot(LocalPlayer player) {
		for (int i = 0; i < 9; i++) {
			if (player.getInventory().getItem(i).getItem() instanceof BlockItem
					&& player.getInventory().getItem(i).getCount() > 0) {
				player.getInventory().setSelectedSlot(i);
				return;
			}
		}
	}

	public static RandomBlockPlacementClient getInstance() {
		return INSTANCE;
	}

	private void renderIcon(GuiGraphicsExtractor graphics) {
		Minecraft client = Minecraft.getInstance();
		RandomBlockPlacementConfig.ClientConfig config = RandomBlockPlacementConfig.CLIENT;

		int screenWidth = client.getWindow().getGuiScaledWidth();
		int screenHeight = client.getWindow().getGuiScaledHeight();
		int iconSize = config.iconSize.get();

		int[] pos = config.getIconPosition(screenWidth, screenHeight);

		graphics.blit(
				RenderPipelines.GUI_TEXTURED,
				ICON_TEXTURE,
				pos[0], pos[1],
				0.0f, 0.0f,
				iconSize, iconSize,
				iconSize, iconSize
		);
	}
}