package com.megatrex4;

import com.megatrex4.mixin.client.PlayerInventoryAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.Random;

public class RandomBlockPlacementClient implements ClientModInitializer {
	private static boolean randomPlacementMode = false;
	private static final Random random = new Random();
	private boolean wasRightClicking = false;
	private static final RandomBlockPlacementClient INSTANCE = new RandomBlockPlacementClient();
	private static final Identifier ICON_TEXTURE = Identifier.of("randomblockplacement", "textures/gui/rblock.png");

	/**
	 * Initializes the client mod, registers key bindings and event listeners.
	 */
	@Override
	public void onInitializeClient() {
		KeyBindings.registerKeyBindings();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null && client.currentScreen == null) {
				boolean isRightClicking = client.options.useKey.isPressed();
				boolean isPlacingBlock = isRightClicking && !wasRightClicking;
				if (randomPlacementMode && isPlacingBlock) {
					handleBlockPlacement(client.player);
					wasRightClicking = true;
				} else if (!isRightClicking) {
					wasRightClicking = false;
				}
			}
		});
		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			if (randomPlacementMode) {
				renderIcon(drawContext);
			}
		});
	}

	/**
	 * Toggles the random placement mode when the key is pressed.
	 */
	public static void onRandomPlaceKeyPressed() {
		randomPlacementMode = !randomPlacementMode;
	}

	/**
	 * Handles block placement logic for the player.
	 * @param player The client player entity
	 */
	public void handleBlockPlacement(ClientPlayerEntity player) {
		if (randomPlacementMode && player.getMainHandStack().getItem() instanceof BlockItem) {
			randomizeHotbarSlot(player);
		}
	}

	/**
	 * Randomizes the player's hotbar slot to another block slot.
	 * @param player The client player entity
	 */
	private static void randomizeHotbarSlot(ClientPlayerEntity player) {
		int currentSlot = ((PlayerInventoryAccessor) player.getInventory()).getSelectedSlot();
		int blockCount = 0;
		int[] blockSlots = new int[9];
		for (int i = 0; i < 9; i++) {
			if (player.getInventory().getStack(i).getItem() instanceof BlockItem) {
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
				((PlayerInventoryAccessor) player.getInventory()).setSelectedSlot(blockSlots[i]);
				break;
			}
		}
	}

	/**
	 * Gets the singleton instance of the client mod.
	 * @return The RandomBlockPlacementClient instance
	 */
	public static RandomBlockPlacementClient getInstance() {
		return INSTANCE;
	}

	/**
	 * Renders the random placement mode icon on the HUD.
	 * @param drawContext The draw context
	 */
	private void renderIcon(DrawContext drawContext) {
		MinecraftClient client = MinecraftClient.getInstance();
		int screenWidth = client.getWindow().getScaledWidth();
		int screenHeight = client.getWindow().getScaledHeight();
		int iconSize = 16;
		int x = (screenWidth - iconSize) / 2;
		int y = (screenHeight - iconSize) / 2 - 13;
		drawContext.drawTexture(
			RenderPipelines.GUI_TEXTURED,
			ICON_TEXTURE,
			x, y,
			0.0f, 0.0f,
			iconSize, iconSize,
			iconSize, iconSize
		);
	}
}
