package com.nonfp.compat;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client entrypoint.
 *
 * <p>Boots the compatibility layer. Nothing here touches NeedsOfNature or FirstPerson
 * types directly: both hooks are installed through reflection, so the mod can stay
 * loaded (and simply do nothing) when either mod is absent.</p>
 */
public final class NonFpCompatClient implements ClientModInitializer {

    public static final String MOD_ID = "nonfpcompat";
    public static final Logger LOGGER = LoggerFactory.getLogger("NoN-FirstPerson-Compat");

    @Override
    public void onInitializeClient() {
        Compatibility.evaluate();
        if (Compatibility.isActive()) {
            LOGGER.info("[NoN x FPM] compatibility active: your own first-person body is hidden while you take part in a NeedsOfNature animation");
        } else {
            LOGGER.info("[NoN x FPM] compatibility disabled ({})", Compatibility.describeForLog());
        }
    }
}
