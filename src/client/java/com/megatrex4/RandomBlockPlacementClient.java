package com.megatrex4;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.text.Text;
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
				renderIcon(drawContext);
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
		int blockCount = 0;
		int[] blockSlots = new int[9];

		// Collect block slots
		for (int i = 0; i < 9; i++) {
			if (player.getInventory().getStack(i).getItem() instanceof BlockItem) {
				blockSlots[blockCount++] = i;
			}
		}

		if (blockCount <= 1) {
			return;
		}

		// Pick a different block slot
		int selectedIndex;
		do {
			selectedIndex = random.nextInt(blockCount);
		} while (blockSlots[selectedIndex] == currentSlot && blockCount > 1);

		player.getInventory().selectedSlot = blockSlots[selectedIndex];
	}

	public static RandomBlockPlacementClient getInstance() {
		return INSTANCE;
	}

	private void renderIcon(MatrixStack matrixStack) {
		MinecraftClient client = MinecraftClient.getInstance();
		int screenWidth = client.getWindow().getScaledWidth();
		int screenHeight = client.getWindow().getScaledHeight();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		matrixStack.push();

		int iconSize = 16;
		int x = (screenWidth - iconSize) / 2; // Center horizontally
		int y = (screenHeight - iconSize) / 2 - 13; // Slightly above the crosshair

		// Directly use the Identifier for the texture
		RenderSystem.setShaderTexture(0, ICON_TEXTURE);
		DrawableHelper.drawTexture(matrixStack, x, y, 0, 0, iconSize, iconSize, iconSize, iconSize);

		matrixStack.pop();

		RenderSystem.disableBlend();
	}




}
