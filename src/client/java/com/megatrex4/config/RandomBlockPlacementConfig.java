package com.megatrex4.config;

import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.Version;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedEnum;
import net.minecraft.resources.Identifier;

import static com.megatrex4.RandomBlockPlacement.MOD_ID;

public class RandomBlockPlacementConfig {

    public static ClientConfig CLIENT = ConfigApiJava.registerAndLoadConfig(
            ClientConfig::new, RegisterType.CLIENT
    );

    @Version(version = 1)
    public static class ClientConfig extends Config {

        public ClientConfig() {
            super(Identifier.parse(MOD_ID + ":client-config"));
        }

        @Comment("Position of the random placement indicator icon")
        public ValidatedEnum<IconPosition> iconPosition =
                new ValidatedEnum<>(IconPosition.ABOVE_CROSSHAIR);

        @Comment("Size of the indicator icon in pixels")
        public ValidatedInt iconSize = new ValidatedInt(16, 128, 4);

        @Comment("Padding from screen edges or crosshair (in pixels)")
        public ValidatedInt padding = new ValidatedInt(5, 100, 0);

        @Comment("Custom X position (-1.0 is Left, 0.0 is Center, 1.0 is Right)")
        public ValidatedFloat customX = new ValidatedFloat(0.0f, 1.0f, -1.0f);

        @Comment("Custom Y position (-1.0 is Top, 0.0 is Center, 1.0 is Bottom)")
        public ValidatedFloat customY = new ValidatedFloat(0.0f, 1.0f, -1.0f);

        public int[] getIconPosition(int screenWidth, int screenHeight) {
            int size = iconSize.get();
            int pad = padding.get();
            int halfW = screenWidth / 2;
            int halfH = screenHeight / 2;
            int half = size / 2;

            return switch (iconPosition.get()) {
                case ABOVE_CROSSHAIR -> new int[]{
                        halfW - half,
                        halfH - half - size - pad
                };
                case BELOW_CROSSHAIR -> new int[]{
                        halfW - half,
                        halfH + half + pad
                };
                case LEFT_OF_CROSSHAIR -> new int[]{
                        halfW - half - size - pad,
                        halfH - half
                };
                case RIGHT_OF_CROSSHAIR -> new int[]{
                        halfW + half + pad,
                        halfH - half
                };
                case TOP_LEFT -> new int[]{
                        pad,
                        pad
                };
                case TOP_RIGHT -> new int[]{
                        screenWidth - size - pad,
                        pad
                };
                case BOTTOM_LEFT -> new int[]{
                        pad,
                        screenHeight - size - pad
                };
                case BOTTOM_RIGHT -> new int[]{
                        screenWidth - size - pad,
                        screenHeight - size - pad
                };
                case CUSTOM -> new int[]{
                        (int) (((customX.get() + 1.0f) / 2.0f) * (screenWidth - size)),
                        (int) (((customY.get() + 1.0f) / 2.0f) * (screenHeight - size))
                };
            };
        }
    }
}