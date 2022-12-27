package org.embeddedt.startuptimer.mixin;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.client.SplashProgress;
import org.embeddedt.startuptimer.StartupTimer;
import org.embeddedt.startuptimer.core.LoadingPlugin;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;

@Mixin(targets = { "net/minecraftforge/fml/client/SplashProgress$2" })
public abstract class SplashProgressMixin {
    @Shadow @Final private int textHeight2;

    @Shadow protected abstract void setColor(int color);

    private static FontRenderer fontRenderer = null;

    private static int fontColor = 0;

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 1, remap = false, shift = At.Shift.AFTER), remap = false)
    private void injectStartupTime(CallbackInfo ci) {
        if (fontRenderer == null) {
            try {
                Field f = SplashProgress.class.getDeclaredField("fontRenderer");
                f.setAccessible(true);
                fontRenderer = (FontRenderer) f.get(null);
                f = SplashProgress.class.getDeclaredField("fontColor");
                f.setAccessible(true);
                fontColor = f.getInt(null);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                return;
            }
        }
        glPushMatrix();
        setColor(fontColor);
        glTranslatef(320 - Display.getWidth() / 2 + 4, 240 + Display.getHeight() / 2 - textHeight2, 0);
        glScalef(2, 2, 1);
        glEnable(GL_TEXTURE_2D);
        String renderString = getString();
        fontRenderer.drawString(renderString, 0, 0, 0x000000);
        glDisable(GL_TEXTURE_2D);
        glPopMatrix();
    }

    /**
     * Get formatted timer + estimate string
     */
    private String getString(){
        long startupTime = ManagementFactory.getRuntimeMXBean().getUptime();

        if(StartupTimer.doneTime > 0) startupTime = StartupTimer.doneTime;

        long minutes = (startupTime / 1000) / 60;
        long seconds = (startupTime / 1000) % 60;

        String str = "Startup: " + minutes + "m " + seconds + "s";

        if(LoadingPlugin.expectedTime > 0){
            long ex_minutes = (LoadingPlugin.expectedTime / 1000) / 60;
            long ex_seconds = (LoadingPlugin.expectedTime / 1000) % 60;

            str += " / ~" + ex_minutes + "m " + ex_seconds + "s";
        }

        return str;
    }
}
