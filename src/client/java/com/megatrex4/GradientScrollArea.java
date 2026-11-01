package com.megatrex4;

import com.megatrex4.config.GradientAxis;
import com.megatrex4.config.GradientType;
import com.megatrex4.config.RandomBlockPlacementConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

final class GradientScrollArea extends AbstractScrollArea {
    private static final int CONTENT_HEIGHT = 540;
    private static final int SCROLLBAR_WIDTH = 9;
    private static final int TEXT = 0xFFE2E2E2;
    private static final int SUBTLE_TEXT = 0xFFB7B7B7;
    private static final int TRACK = 0xFF242629;
    private static final int THUMB = 0xFFB8B8B8;
    private static final int GREEN = 0xFF438C2C;

    private final List<GradientControlWidget> controls = new ArrayList<>();
    private GradientControlWidget activeControl;
    private boolean draggingScrollbar;

    GradientScrollArea(int x, int y, int width, int height) {
        super(
                x,
                y,
                width,
                height,
                Component.translatable("screen.randomblockplacement.gradient.title"),
                AbstractScrollArea.defaultSettings(18)
        );
        createControls();
        positionControls();
    }

    @Override
    protected int contentHeight() {
        return CONTENT_HEIGHT;
    }

    @Override
    protected void extractWidgetRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float deltaTracker
    ) {
        positionControls();
        graphics.fill(getX(), getY(), getRight(), getBottom(), TRACK);
        graphics.enableScissor(getX(), getY(), getRight() - SCROLLBAR_WIDTH, getBottom());
        drawContentLabels(graphics);
        drawPalette(graphics);
        for (GradientControlWidget control : controls) {
            control.extractRenderState(graphics, mouseX, mouseY, deltaTracker);
        }
        graphics.disableScissor();
        drawScrollbar(graphics);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (isOverScrollbar(event.x(), event.y()) && updateScrolling(event)) {
            draggingScrollbar = true;
            return;
        }
        if (!isInsideContent(event.x(), event.y())) {
            activeControl = null;
            return;
        }

        positionControls();
        for (int i = controls.size() - 1; i >= 0; i--) {
            GradientControlWidget control = controls.get(i);
            if (control.mouseClicked(event, doubleClick)) {
                activeControl = control;
                return;
            }
        }
        activeControl = null;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        positionControls();
        for (GradientControlWidget control : controls) {
            control.mouseMoved(isInsideContent(mouseX, mouseY) ? mouseX : Double.NaN,
                    isInsideContent(mouseX, mouseY) ? mouseY : Double.NaN);
        }
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (draggingScrollbar) {
            return super.mouseDragged(event, deltaX, deltaY);
        }
        if (activeControl != null) {
            return activeControl.mouseDragged(event, deltaX, deltaY);
        }
        return false;
    }

    @Override
    public void onRelease(MouseButtonEvent event) {
        if (activeControl != null) {
            activeControl.mouseReleased(event);
            activeControl = null;
        }
        if (draggingScrollbar) {
            super.onRelease(event);
            draggingScrollbar = false;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        return isMouseOver(mouseX, mouseY) && super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (activeControl != null && activeControl.keyPressed(event)) {
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    private void createControls() {
        int contentX = getX() + 12;
        int controlWidth = Math.max(1, getWidth() - SCROLLBAR_WIDTH - 24);
        var config = RandomBlockPlacementConfig.CLIENT;

        controls.add(GradientControlWidget.checkbox(
                contentX, getY(), controlWidth, 40,
                Component.translatable("screen.randomblockplacement.gradient.enabled_label"),
                RandomBlockPlacementClient::isGradientPlacementMode,
                () -> RandomBlockPlacementClient.toggleGradientPlacementMode(Minecraft.getInstance().player)
        ));

        int axisGap = 8;
        int axisWidth = (controlWidth - axisGap) / 2;
        controls.add(GradientControlWidget.radio(
                contentX, getY(), axisWidth, 40,
                Component.translatable(GradientAxis.HORIZONTAL.translationKey()),
                () -> config.gradientAxis.get() == GradientAxis.HORIZONTAL,
                () -> config.gradientAxis.setAndUpdate(GradientAxis.HORIZONTAL)
        ));
        controls.add(GradientControlWidget.radio(
                contentX + axisWidth + axisGap, getY(), axisWidth, 40,
                Component.translatable(GradientAxis.VERTICAL.translationKey()),
                () -> config.gradientAxis.get() == GradientAxis.VERTICAL,
                () -> config.gradientAxis.setAndUpdate(GradientAxis.VERTICAL)
        ));

        int shapeGap = 8;
        int shapeWidth = (controlWidth - shapeGap * 2) / 3;
        controls.add(GradientControlWidget.radio(
                contentX, getY(), shapeWidth, 40,
                Component.translatable(GradientType.LINEAR.translationKey()),
                () -> config.gradientType.get() == GradientType.LINEAR,
                () -> config.gradientType.setAndUpdate(GradientType.LINEAR)
        ));
        controls.add(GradientControlWidget.radio(
                contentX + shapeWidth + shapeGap, getY(), shapeWidth, 40,
                Component.translatable(GradientType.RADIAL.translationKey()),
                () -> config.gradientType.get() == GradientType.RADIAL,
                () -> config.gradientType.setAndUpdate(GradientType.RADIAL)
        ));
        controls.add(GradientControlWidget.radio(
                contentX + (shapeWidth + shapeGap) * 2, getY(), shapeWidth, 40,
                Component.translatable(GradientType.SQUARE.translationKey()),
                () -> config.gradientType.get() == GradientType.SQUARE,
                () -> config.gradientType.setAndUpdate(GradientType.SQUARE)
        ));

        controls.add(GradientControlWidget.slider(
                contentX, getY(), controlWidth, 56,
                value -> Component.literal(Integer.toString(value)),
                () -> config.gradientLength.get(),
                config.gradientLength::setAndUpdate,
                2,
                64
        ));

        controls.add(GradientControlWidget.checkbox(
                contentX, getY(), controlWidth, 40,
                Component.translatable("screen.randomblockplacement.gradient.dithering_label"),
                () -> config.gradientDithering.get(),
                () -> config.gradientDithering.setAndUpdate(!config.gradientDithering.get())
        ));
    }

    private void positionControls() {
        int yOffset = (int) Math.round(scrollAmount());
        int contentX = getX() + 12;
        int controlWidth = Math.max(1, getWidth() - SCROLLBAR_WIDTH - 24);
        int axisGap = 8;
        int axisWidth = (controlWidth - axisGap) / 2;
        int shapeGap = 8;
        int shapeWidth = (controlWidth - shapeGap * 2) / 3;

        controls.get(0).setPosition(contentX, getY() + 82 - yOffset);
        controls.get(1).setPosition(contentX, getY() + 176 - yOffset);
        controls.get(2).setPosition(contentX + axisWidth + axisGap, getY() + 176 - yOffset);
        controls.get(3).setPosition(contentX, getY() + 270 - yOffset);
        controls.get(4).setPosition(contentX + shapeWidth + shapeGap, getY() + 270 - yOffset);
        controls.get(5).setPosition(contentX + (shapeWidth + shapeGap) * 2, getY() + 270 - yOffset);
        controls.get(6).setPosition(contentX, getY() + 346 - yOffset);
        controls.get(7).setPosition(contentX, getY() + 432 - yOffset);
    }

    private void drawContentLabels(GuiGraphicsExtractor graphics) {
        int textX = getX() + 12;
        int availableWidth = Math.max(1, getWidth() - SCROLLBAR_WIDTH - 24);
        int yOffset = (int) Math.round(scrollAmount());
        int contentY = getY() - yOffset;
        graphics.fill(textX - 2, contentY + 4, textX + availableWidth + 2, contentY + 64, 0xFF303236);
        graphics.textWithWordWrap(
                Minecraft.getInstance().font,
                Component.translatable("screen.randomblockplacement.gradient.help"),
                textX + 8,
                contentY + 12,
                availableWidth - 16,
                TEXT
        );
        drawSectionTitle(graphics, "screen.randomblockplacement.gradient.axis", textX, availableWidth, contentY + 150);
        drawSectionTitle(graphics, "screen.randomblockplacement.gradient.shape", textX, availableWidth, contentY + 244);
        drawSectionTitle(graphics, "screen.randomblockplacement.gradient.palette", textX, availableWidth, contentY + 476);
        graphics.text(
                Minecraft.getInstance().font,
                Component.translatable("screen.randomblockplacement.gradient.length",
                        RandomBlockPlacementConfig.CLIENT.gradientLength.get()),
                textX,
                contentY + 326,
                SUBTLE_TEXT
        );
    }

    private void drawSectionTitle(
            GuiGraphicsExtractor graphics,
            String translationKey,
            int x,
            int sectionWidth,
            int y
    ) {
        graphics.text(
                Minecraft.getInstance().font,
                Component.translatable(translationKey),
                x,
                y,
                SUBTLE_TEXT
        );
        int labelWidth = Minecraft.getInstance().font.width(Component.translatable(translationKey));
        int ruleX = x + labelWidth + 10;
        graphics.fill(ruleX, y + 4, x + sectionWidth, y + 5, 0xFF55575B);
    }

    private void drawPalette(GuiGraphicsExtractor graphics) {
        int yOffset = (int) Math.round(scrollAmount());
        int textX = getX() + 12;
        int availableWidth = Math.max(1, getWidth() - SCROLLBAR_WIDTH - 24);
        int slotTop = getY() + 496 - yOffset;
        int gap = 4;
        int slotWidth = (availableWidth - gap * 8) / 9;
        var player = Minecraft.getInstance().player;

        for (int slot = 0; slot < 9; slot++) {
            int left = textX + slot * (slotWidth + gap);
            int right = slot == 8 ? textX + availableWidth : left + slotWidth;
            graphics.fill(left, slotTop, right, slotTop + 32, 0xFF303236);
            graphics.fill(left, slotTop, right, slotTop + 2, slot == 0 ? GREEN : 0xFF55575B);
            graphics.text(Minecraft.getInstance().font, Integer.toString(slot + 1),
                    left + 3, slotTop + 3, SUBTLE_TEXT);
            if (player != null) {
                ItemStack stack = player.getInventory().getItem(slot);
                if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                    graphics.item(stack, left + Math.max(0, (slotWidth - 16) / 2), slotTop + 11);
                }
            }
        }
    }

    private boolean isInsideContent(double mouseX, double mouseY) {
        return mouseX >= getX()
                && mouseX < getRight() - SCROLLBAR_WIDTH
                && mouseY >= getY()
                && mouseY < getBottom();
    }

    private void drawScrollbar(GuiGraphicsExtractor graphics) {
        if (maxScrollAmount() <= 0) {
            return;
        }
        int trackX = getRight() - SCROLLBAR_WIDTH;
        graphics.fill(trackX, getY(), getRight(), getBottom(), TRACK);
        int thumbHeight = Math.max(24, getHeight() * getHeight() / contentHeight());
        int thumbTravel = getHeight() - thumbHeight;
        int thumbY = getY() + (int) Math.round(thumbTravel * scrollAmount() / maxScrollAmount());
        graphics.fill(trackX + 2, getY() + 2, getRight() - 2, getBottom() - 2, 0xFF34363A);
        graphics.fill(trackX + 1, thumbY, getRight() - 1, thumbY + thumbHeight, 0xFF171819);
        graphics.fill(trackX + 3, thumbY + 2, getRight() - 3, thumbY + thumbHeight - 2, THUMB);
    }
}
