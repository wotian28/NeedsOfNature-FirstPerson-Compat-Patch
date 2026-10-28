package com.nonfp.compat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Decides whether the compatibility layer can run, and remembers why it cannot.
 */
public final class Compatibility {

    public enum State {
        /** Both mods are present and the hooks were installed. */
        ACTIVE,
        /** At least one of the two mods is not installed. */
        MISSING,
        /** Both mods are installed, but the expected entry points were not found. */
        INCOMPATIBLE
    }

    private static final List<String> MISSING_MODS = new ArrayList<>();

    private static State state = State.MISSING;
    private static String failure = "";

    private Compatibility() {
    }

    public static void evaluate() {
        MISSING_MODS.clear();
        failure = "";

        boolean hasFirstPerson = FabricLoader.getInstance().isModLoaded("firstperson");
        boolean hasNeedsOfNature = FabricLoader.getInstance().isModLoaded("needsofnature");

        if (!hasFirstPerson) {
            MISSING_MODS.add("FirstPerson");
        }
        if (!hasNeedsOfNature) {
            MISSING_MODS.add("NeedsOfNature");
        }
        if (!MISSING_MODS.isEmpty()) {
            state = State.MISSING;
            return;
        }

        try {
            FirstPersonBridge.install();
            AnimationProbe.install();
            state = State.ACTIVE;
        } catch (Throwable t) {
            state = State.INCOMPATIBLE;
            failure = t.getClass().getSimpleName() + ": " + t.getMessage();
            NonFpCompatClient.LOGGER.warn("[NoN x FPM] installed versions are not supported, compatibility disabled", t);
        }
    }

    public static boolean isActive() {
        return state == State.ACTIVE;
    }

    public static State getState() {
        return state;
    }

    public static List<String> getMissingMods() {
        return Collections.unmodifiableList(MISSING_MODS);
    }

    public static String getFailure() {
        return failure;
    }

    public static String describeForLog() {
        return switch (state) {
            case ACTIVE -> "active";
            case MISSING -> "missing mods: " + String.join(", ", MISSING_MODS);
            case INCOMPATIBLE -> "unsupported versions: " + failure;
        };
    }
}
