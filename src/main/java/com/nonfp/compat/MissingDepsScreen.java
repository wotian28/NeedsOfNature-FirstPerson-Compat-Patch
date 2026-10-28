package com.nonfp.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Small modal window that lists the missing mod and can be dismissed.
 */
public class MissingDepsScreen extends Screen {

    private static final int TITLE_COLOR = 0xFFFFFFFF;
    private static final int BODY_COLOR = 0xFFB8B8B8;
    private static final int ACCENT_COLOR = 0xFFFFC94D;

    private final Screen parent;

    public MissingDepsScreen(Screen parent) {
        super(Text.translatable("nonfpcompat.screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int closeWidth = 120;
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("gui.done"), button -> this.close())
                .dimensions(this.width / 2 - closeWidth / 2, this.height / 2 + 46, closeWidth, 20)
                .build());

        // Top right "x" button.
        this.addDrawableChild(ButtonWidget.builder(Text.literal("\u2715"), button -> this.close())
                .dimensions(this.width - 28, 8, 20, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xC0101010);
        super.render(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, this.height / 2 - 62, TITLE_COLOR);

        List<Text> lines = this.buildLines();
        int y = this.height / 2 - 34;
        for (Text line : lines) {
            context.drawCenteredTextWithShadow(this.textRenderer, line, centerX, y, BODY_COLOR);
            y += 14;
        }

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("nonfpcompat.screen.hint").formatted(Formatting.DARK_GRAY),
                centerX, this.height / 2 + 20, 0xFF6E6E6E);
    }

    private List<Text> buildLines() {
        List<Text> lines = new ArrayList<>();
        if (Compatibility.getState() == Compatibility.State.MISSING) {
            String names = String.join(" + ", Compatibility.getMissingMods());
            lines.add(Text.translatable("nonfpcompat.screen.missing", names).formatted(Formatting.YELLOW));
        } else {
            lines.add(Text.translatable("nonfpcompat.screen.incompatible").formatted(Formatting.YELLOW));
        }
        lines.add(Text.translatable("nonfpcompat.screen.disabled"));
        return lines;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }
}
