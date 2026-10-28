package com.nonfp.compat;

import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;

/**
 * Shows the "required mod is missing" window once per game launch.
 */
public final class Notice {

    private static final AtomicBoolean SHOWN = new AtomicBoolean(false);

    private Notice() {
    }

    public static void onTitleScreen(Screen screen) {
        if (Compatibility.isActive() || !(screen instanceof TitleScreen)) {
            return;
        }
        if (!SHOWN.compareAndSet(false, true)) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }

        // Defer by one client tick: opening a screen from inside TitleScreen#init can
        // leave the title screen half initialised.
        client.execute(() -> {
            Screen current = client.currentScreen;
            if (current instanceof TitleScreen) {
                client.setScreen(new MissingDepsScreen(current));
            }
        });
    }
}
