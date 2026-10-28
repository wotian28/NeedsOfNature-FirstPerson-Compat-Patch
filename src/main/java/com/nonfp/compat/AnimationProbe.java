package com.nonfp.compat;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import net.minecraft.client.MinecraftClient;

/**
 * Asks NeedsOfNature whether the local player currently takes part in an animation.
 *
 * <p>NeedsOfNature keeps the client side state of every running animation in
 * {@code ClientAnimationRuntime}. {@code isActorPendingOrActive(UUID)} is true from the
 * moment the start packet is queued until the stop packet is fully processed, which is
 * exactly the window in which the body should stay hidden.</p>
 */
public final class AnimationProbe {

    private static Method probe;
    private static int consecutiveFailures;

    private AnimationProbe() {
    }

    public static void install() throws ReflectiveOperationException {
        Class<?> runtime = Class.forName(
                "com.nonid.internal.animation.client.runtime.ClientAnimationRuntime",
                false,
                AnimationProbe.class.getClassLoader());

        Method candidate = find(runtime, "isActorPendingOrActive");
        if (candidate == null) {
            candidate = find(runtime, "isActorActive");
        }
        if (candidate == null) {
            throw new NoSuchMethodException("ClientAnimationRuntime has no actor state query");
        }
        if (!Modifier.isStatic(candidate.getModifiers())) {
            throw new IllegalStateException("ClientAnimationRuntime actor state query is not static");
        }

        candidate.setAccessible(true);
        probe = candidate;
        consecutiveFailures = 0;
    }

    private static Method find(Class<?> owner, String name) {
        try {
            Method method = owner.getMethod(name, UUID.class);
            return method.getReturnType() == boolean.class ? method : null;
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    /**
     * Called from the render thread on every first person frame.
     */
    public static boolean isLocalPlayerAnimating() {
        Method method = probe;
        if (method == null) {
            return false;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return false;
        }

        try {
            return (Boolean) method.invoke(null, client.player.getUuid());
        } catch (Throwable t) {
            if (++consecutiveFailures > 3) {
                probe = null;
                NonFpCompatClient.LOGGER.warn("[NoN x FPM] could not read the animation state, hiding is turned off for this session", t);
            }
            return false;
        }
    }
}
