package com.megatrex4;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;

public class RandomBlockPlacementRenderer {
    private static final Identifier TEXTURE = Identifier.of("randomblockplacement", "textures/gui/rblock.png");

    public static void renderTexture(DrawContext drawContext, int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.options.hudHidden) {
            return;
        }

        client.getTextureManager().bindTexture(TEXTURE);

        int textureWidth = 16;
        int textureHeight = 16;
        int x = (screenWidth / 2) - (textureWidth / 2);
        int y = (screenHeight / 2) - (textureHeight / 2);

        MatrixStack matrixStack = drawContext.getMatrices();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrixStack.push();
        matrixStack.translate(0, -15, 0);

        drawContext.drawTexture(TEXTURE, x, y, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);

        matrixStack.pop();

        RenderSystem.disableBlend();
    }
}
