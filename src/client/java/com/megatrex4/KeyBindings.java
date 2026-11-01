package com.megatrex4;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import static com.megatrex4.RandomBlockPlacement.MOD_ID;

public class KeyBindings {
    public static KeyMapping randomPlaceKey;
    public static KeyMapping gradientSettingsKey;

    public static void registerKeyBindings() {
        // Create custom category for this mod
        KeyMapping.Category CATEGORY = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "keybindings")
        );

        // Use KeyMappingHelper to register the keybinding
        randomPlaceKey = net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.randomblockplacement.toggle",
                        InputConstants.Type.KEYBOARD,
                        InputConstants.KEY_R,
                        CATEGORY
                )
        );
        gradientSettingsKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.randomblockplacement.gradient",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_X,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (randomPlaceKey.consumeClick()) {
                RandomBlockPlacementClient.onRandomPlaceKeyPressed(client.player);
            }
            while (gradientSettingsKey.consumeClick()) {
                if (InputConstants.isKeyDown(InputConstants.KEY_LSHIFT)
                        || InputConstants.isKeyDown(InputConstants.KEY_RSHIFT)) {
                    client.gui.setScreen(new GradientSettingsScreen());
                } else {
                    RandomBlockPlacementClient.toggleGradientPlacementMode(client.player);
                }
            }
        });
    }
}