package com.megatrex4;

import com.megatrex4.config.RandomBlockPlacementConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GradientSettingsScreen extends Screen {
    private static final int PANEL_MAX_WIDTH = 720;
    private static final int PANEL_MAX_HEIGHT = 520;
    private static final int PANEL_MARGIN = 16;
    private static final int HEADER_HEIGHT = 54;
    private static final int FOOTER_HEIGHT = 50;
    public GradientSettingsScreen() {
        super(Component.translatable("screen.randomblockplacement.gradient.title"));
    }

    @Override
    public void onClose() {
        RandomBlockPlacementConfig.CLIENT.save();
        super.onClose();
    }

    @Override
    protected void init() {
        clearWidgets();
        int panelWidth = Math.min(PANEL_MAX_WIDTH, width - PANEL_MARGIN * 2);
        int panelHeight = Math.min(PANEL_MAX_HEIGHT, height - PANEL_MARGIN * 2);
        int panelLeft = (width - panelWidth) / 2;
        int panelTop = (height - panelHeight) / 2;
        int contentTop = panelTop + HEADER_HEIGHT;
        int contentHeight = panelHeight - HEADER_HEIGHT - FOOTER_HEIGHT;

        addRenderableWidget(new GradientScrollArea(
                panelLeft + 16,
                contentTop,
                panelWidth - 32,
                contentHeight
        ));

        int doneWidth = Math.min(148, panelWidth - 48);
        AbstractWidget doneButton = GradientControlWidget.button(
                panelLeft + panelWidth - doneWidth - 16,
                panelTop + panelHeight - FOOTER_HEIGHT + 10,
                doneWidth,
                30,
                Component.translatable("screen.randomblockplacement.gradient.done"),
                this::onClose
        );
        addRenderableWidget(doneButton);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float deltaTracker) {
        super.extractBackground(graphics, mouseX, mouseY, deltaTracker);
        int panelWidth = Math.min(PANEL_MAX_WIDTH, width - PANEL_MARGIN * 2);
        int panelHeight = Math.min(PANEL_MAX_HEIGHT, height - PANEL_MARGIN * 2);
        int panelLeft = (width - panelWidth) / 2;
        int panelTop = (height - panelHeight) / 2;

        graphics.fill(panelLeft - 2, panelTop - 2, panelLeft + panelWidth + 2, panelTop + panelHeight + 2,
                0xD0000000);
        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, 0xF0222427);
        graphics.fill(panelLeft, panelTop, panelLeft + 4, panelTop + panelHeight, 0xFF438C2C);
        graphics.text(font, title, panelLeft + 18, panelTop + 17, 0xFFF2F2F2);
        graphics.fill(
                panelLeft + 12,
                panelTop + HEADER_HEIGHT - 1,
                panelLeft + panelWidth - 16,
                panelTop + HEADER_HEIGHT,
                0xFF4B4D50
        );
        graphics.fill(
                panelLeft + 16,
                panelTop + panelHeight - FOOTER_HEIGHT,
                panelLeft + panelWidth - 16,
                panelTop + panelHeight - FOOTER_HEIGHT + 1,
                0xFF4B4D50
        );
    }
}
