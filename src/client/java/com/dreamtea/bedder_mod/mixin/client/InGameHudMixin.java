package com.dreamtea.bedder_mod.mixin.client;

import com.dreamtea.bedder_mod.SpamTracker;
import com.dreamtea.bedder_mod.imixin.ISpamClick;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public abstract class InGameHudMixin {

    @Shadow @Final private Minecraft minecraft;

    @Redirect(
            method = "renderOverlayMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V"
            )
    )
    public void adjustFont(
            GuiGraphics instance,
            Font textRenderer,
            Component text,
            int x,
            int y,
            int width,
            int color,
            GuiGraphics context,
            DeltaTracker tickCounter
    ){
        int drawColor = color;
        if(this.minecraft.player instanceof ISpamClick isc) {
            SpamTracker spam = isc.getClicker();
            spam.setSpammedText(text);
            if(isc.getClicker().active()){
                spam.matricesTranslate(context.pose());
                drawColor = isc.getClicker().getColor();
            }
        }
        context.drawStringWithBackdrop(textRenderer, text, x, y, width, drawColor);
    }
}