package com.megatrex4;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Identifier;

import java.util.Random;

public class RandomBlockPlacementClient implements ClientModInitializer {
	private static boolean randomPlacementMode = false;
	private static final Random random = new Random();
	private boolean wasRightClicking = false;
	private static final RandomBlockPlacementClient INSTANCE = new RandomBlockPlacementClient();
	private static final Identifier ICON_TEXTURE = Identifier.of("randomblockplacement", "textures/gui/rblock.png");

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
				int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
				int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
				renderTexture(drawContext, screenWidth, screenHeight);
			}
		});
	}

	public static void onRandomPlaceKeyPressed() {
		randomPlacementMode = !randomPlacementMode;
	}

	public void handleBlockPlacement(ClientPlayerEntity player) {
		if (randomPlacementMode) {
			randomizeHotbarSlot(player);
		}
	}

	private static void randomizeHotbarSlot(ClientPlayerEntity player) {
		int currentSlot = player.getInventory().selectedSlot;
		int[] blockSlots = new int[9];
		int blockCount = 0;

		// Collect all hotbar slots with BlockItems
		for (int i = 0; i < 9; i++) {
			if (player.getInventory().getStack(i).getItem() instanceof BlockItem) {
				blockSlots[blockCount++] = i;
			}
		}

		if (blockCount <= 1) {
			return;
		}

		int chosenSlot;
		do {
			chosenSlot = blockSlots[random.nextInt(blockCount)];
		} while (blockCount > 1 && chosenSlot == currentSlot);

		player.getInventory().selectedSlot = chosenSlot;
	}

	private static void renderTexture(DrawContext drawContext, int screenWidth, int screenHeight) {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.options.hudHidden) {
			return;
		}

		client.getTextureManager().bindTexture(ICON_TEXTURE);

		int textureWidth = 16;
		int textureHeight = 16;
		int x = (screenWidth / 2) - (textureWidth / 2);
		int y = (screenHeight / 2) - (textureHeight / 2);

		MatrixStack matrixStack = drawContext.getMatrices();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		matrixStack.push();
		matrixStack.translate(0, -15, 0);

		drawContext.drawTexture(ICON_TEXTURE, x, y, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

		matrixStack.pop();

		RenderSystem.disableBlend();
	}

	public static RandomBlockPlacementClient getInstance() {
		return INSTANCE;
	}
}
