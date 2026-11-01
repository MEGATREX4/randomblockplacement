package com.megatrex4;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;

final class GradientControlWidget extends AbstractWidget {
    enum Kind {
        BUTTON,
        CHECKBOX,
        RADIO,
        SLIDER
    }

    private static final int PANEL = 0xFF3E4044;
    private static final int BORDER = 0xFF282A2D;
    private static final int GREEN = 0xFF438C2C;
    private static final int GREEN_HOVER = 0xFF54A638;
    private static final int GREEN_PRESSED = 0xFF2D681F;
    private static final int TEXT = 0xFFF2F2F2;

    private final Kind kind;
    private final Runnable action;
    private final BooleanSupplier selected;
    private final IntSupplier sliderValue;
    private final IntConsumer sliderSetter;
    private final IntFunction<Component> sliderLabel;
    private final int sliderMin;
    private final int sliderMax;
    private boolean dragging;

    private GradientControlWidget(
            int x,
            int y,
            int width,
            int height,
            Component label,
            Kind kind,
            BooleanSupplier selected,
            Runnable action,
            IntSupplier sliderValue,
            IntConsumer sliderSetter,
            IntFunction<Component> sliderLabel,
            int sliderMin,
            int sliderMax
    ) {
        super(x, y, width, height, label);
        this.kind = kind;
        this.selected = selected;
        this.action = action;
        this.sliderValue = sliderValue;
        this.sliderSetter = sliderSetter;
        this.sliderLabel = sliderLabel;
        this.sliderMin = sliderMin;
        this.sliderMax = sliderMax;
    }

    static GradientControlWidget button(int x, int y, int width, int height, Component label, Runnable action) {
        return new GradientControlWidget(
                x, y, width, height, label, Kind.BUTTON, () -> false, action, null, null, null, 0, 0
        );
    }

    @Override
    public void setPosition(int x, int y) {
        setX(x);
        setY(y);
    }

    static GradientControlWidget checkbox(
            int x,
            int y,
            int width,
            int height,
            Component label,
            BooleanSupplier selected,
            Runnable action
    ) {
        return new GradientControlWidget(
                x, y, width, height, label, Kind.CHECKBOX, selected, action, null, null, null, 0, 0
        );
    }

    static GradientControlWidget radio(
            int x,
            int y,
            int width,
            int height,
            Component label,
            BooleanSupplier selected,
            Runnable action
    ) {
        return new GradientControlWidget(
                x, y, width, height, label, Kind.RADIO, selected, action, null, null, null, 0, 0
        );
    }

    static GradientControlWidget slider(
            int x,
            int y,
            int width,
            int height,
            IntFunction<Component> label,
            IntSupplier value,
            IntConsumer setter,
            int min,
            int max
    ) {
        return new GradientControlWidget(
                x, y, width, height, label.apply(value.getAsInt()), Kind.SLIDER, () -> false, () -> { },
                value, setter, label, min, max
        );
    }

    @Override
    protected void extractWidgetRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float deltaTracker
    ) {
        boolean highlighted = isHoveredOrFocused();
        int outer = highlighted ? 0xFFF2F2F2 : BORDER;
        int background = switch (kind) {
            case BUTTON -> !active ? GREEN_PRESSED : highlighted ? GREEN_HOVER : GREEN;
            case RADIO -> selected.getAsBoolean() ? 0xFF315F27 : highlighted ? 0xFF4A4D51 : PANEL;
            default -> highlighted ? 0xFF4A4D51 : PANEL;
        };
        graphics.fill(getX(), getY(), getX() + width, getY() + height, outer);
        graphics.fill(getX() + 2, getY() + 2, getX() + width - 2, getY() + height - 2, background);

        switch (kind) {
            case BUTTON -> drawButton(graphics);
            case CHECKBOX -> drawCheckbox(graphics);
            case RADIO -> drawRadio(graphics);
            case SLIDER -> drawSlider(graphics);
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        if (kind == Kind.SLIDER) {
            dragging = true;
            updateSlider(event.x());
        } else {
            action.run();
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        isHovered = isMouseOver(mouseX, mouseY);
    }

    @Override
    public void onRelease(MouseButtonEvent event) {
        dragging = false;
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double deltaX, double deltaY) {
        if (dragging && kind == Kind.SLIDER) {
            updateSlider(event.x());
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    private void drawButton(GuiGraphicsExtractor graphics) {
        graphics.centeredText(
                Minecraft.getInstance().font,
                getMessage(),
                getX() + width / 2,
                getY() + (height - 8) / 2,
                TEXT
        );
    }

    private void drawCheckbox(GuiGraphicsExtractor graphics) {
        int boxSize = 12;
        int boxX = getX() + width - boxSize - 14;
        int boxY = getY() + (height - boxSize) / 2;
        graphics.text(
                Minecraft.getInstance().font,
                getMessage(),
                getX() + 12,
                getY() + (height - 8) / 2,
                TEXT
        );
        graphics.fill(boxX - 2, boxY - 2, boxX + boxSize + 2, boxY + boxSize + 2, 0xFF222426);
        graphics.fill(boxX, boxY, boxX + boxSize, boxY + boxSize,
                selected.getAsBoolean() ? GREEN : 0xFFBFC0C2);
        if (selected.getAsBoolean()) {
            graphics.fill(boxX + 3, boxY + 3, boxX + 9, boxY + 5, 0xFFFFFFFF);
            graphics.fill(boxX + 3, boxY + 5, boxX + 5, boxY + 9, 0xFFFFFFFF);
            graphics.fill(boxX + 5, boxY + 7, boxX + 9, boxY + 9, 0xFFFFFFFF);
        }
    }

    private void drawRadio(GuiGraphicsExtractor graphics) {
        int centerX = getX() + 15;
        int centerY = getY() + height / 2;
        graphics.text(
                Minecraft.getInstance().font,
                getMessage(),
                getX() + 30,
                getY() + (height - 8) / 2,
                TEXT
        );
        graphics.fill(centerX - 7, centerY - 7, centerX + 7, centerY + 7, 0xFF222426);
        graphics.fill(centerX - 5, centerY - 5, centerX + 5, centerY + 5, 0xFFBFC0C2);
        if (selected.getAsBoolean()) {
            graphics.fill(centerX - 3, centerY - 1, centerX + 3, centerY + 2, GREEN);
            graphics.fill(centerX - 1, centerY - 3, centerX + 2, centerY + 4, GREEN);
        }
    }

    private void drawSlider(GuiGraphicsExtractor graphics) {
        int trackLeft = getX() + 12;
        int trackRight = getX() + width - 12;
        int trackY = getY() + height - 13;
        double progress = (sliderValue.getAsInt() - sliderMin) / (double) (sliderMax - sliderMin);
        int thumbX = trackLeft + (int) Math.round((trackRight - trackLeft) * progress);
        graphics.text(
                Minecraft.getInstance().font,
                sliderLabel.apply(sliderValue.getAsInt()),
                getX() + 12,
                getY() + 4,
                TEXT
        );
        graphics.fill(trackLeft, trackY, trackRight, trackY + 5, 0xFF88898B);
        graphics.fill(trackLeft, trackY, thumbX, trackY + 5, GREEN);
        graphics.fill(thumbX - 5, trackY - 5, thumbX + 5, trackY + 10, 0xFF222426);
        graphics.fill(thumbX - 3, trackY - 3, thumbX + 3, trackY + 8, 0xFFD8D8D8);
    }

    private void updateSlider(double mouseX) {
        int trackLeft = getX() + 12;
        int trackRight = getX() + width - 12;
        double progress = Math.max(0.0, Math.min(1.0, (mouseX - trackLeft) / (trackRight - trackLeft)));
        int value = sliderMin + (int) Math.round(progress * (sliderMax - sliderMin));
        sliderSetter.accept(value);
        setMessage(sliderLabel.apply(value));
    }
}
