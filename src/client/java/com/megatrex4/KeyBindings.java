package com.megatrex4;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.megatrex4.RandomBlockPlacement.MOD_ID;

public class KeyBindings {
    public static KeyMapping randomPlaceKey;

    public static void registerKeyBindings() {
        // Create custom category for this mod
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.withDefaultNamespace(MOD_ID)
        );

        // Constructor auto-registers the keybinding
        randomPlaceKey = new KeyMapping(
                "key.randomblockplacement.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                category
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (randomPlaceKey.consumeClick()) {
                RandomBlockPlacementClient.onRandomPlaceKeyPressed();
            }
        });
    }
}