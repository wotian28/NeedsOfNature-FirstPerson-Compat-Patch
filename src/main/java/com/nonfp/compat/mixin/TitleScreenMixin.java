package com.nonfp.compat.mixin;

import com.nonfp.compat.Notice;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void nonfpcompat$showMissingModNotice(CallbackInfo ci) {
        Notice.onTitleScreen((Screen) (Object) this);
    }
}
