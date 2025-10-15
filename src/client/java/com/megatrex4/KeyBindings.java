package com.megatrex4;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.KeyBinding.Category;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import net.minecraft.util.Identifier;

import static com.megatrex4.RandomBlockPlacement.MOD_ID;

public class KeyBindings {
    public static KeyBinding randomPlaceKey;

    public static void registerKeyBindings() {
        Category category = Category.create(Identifier.of(MOD_ID, "category"));

        randomPlaceKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.randomblockplacement.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (randomPlaceKey.wasPressed()) {
                RandomBlockPlacementClient.onRandomPlaceKeyPressed();
            }
        });
    }
}
