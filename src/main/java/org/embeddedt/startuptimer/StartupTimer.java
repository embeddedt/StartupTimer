package org.embeddedt.startuptimer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.lang.management.ManagementFactory;

@Mod(
        modid = StartupTimer.MOD_ID,
        name = StartupTimer.MOD_NAME,
        version = StartupTimer.VERSION,
        clientSideOnly = true
)
public class StartupTimer {

    public static final String MOD_ID = "startuptimer";
    public static final String MOD_NAME = "StartupTimer";
    public static final String VERSION = "1.0.0";
    private static final Logger LOGGER = LogManager.getLogger("StartupTimer");

    @Mod.Instance(MOD_ID)
    public static StartupTimer INSTANCE;

    public static long doneTime = 0;

    boolean triggered = false;
    boolean trueFullscreen;

    long startupTime;
    boolean hasBeenMainMenu = false;
    boolean hasLeftMainMenu = false;

    public StartupTimer() {
        MinecraftForge.EVENT_BUS.register(this);
        trueFullscreen = Minecraft.getMinecraft().gameSettings.fullScreen;
        Minecraft.getMinecraft().gameSettings.fullScreen = false;
    }

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {

    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (!triggered && event.getGui() instanceof GuiMainMenu) {
            triggered = true;

            Minecraft.getMinecraft().gameSettings.fullScreen = trueFullscreen;
            if (Minecraft.getMinecraft().gameSettings.fullScreen && !Minecraft.getMinecraft().isFullScreen()) {
                Minecraft.getMinecraft().toggleFullscreen();
                Minecraft.getMinecraft().gameSettings.fullScreen = Minecraft.getMinecraft().isFullScreen();
            }

            startupTime = ManagementFactory.getRuntimeMXBean().getUptime();
            LOGGER.info("Startup took " + startupTime + "ms.");

            doneTime = startupTime;

            StartupTimerConfig.addStartupTime(startupTime);
        }
    }

    @SubscribeEvent
    public void onGuiDraw(GuiScreenEvent.DrawScreenEvent event){
        if(!hasLeftMainMenu && event.getGui() instanceof GuiMainMenu){
            hasBeenMainMenu = true;

            if(StartupTimerConfig.displayStartupTimeOnMainMenu) {
                GuiMainMenu mainMenu = (GuiMainMenu)event.getGui();
                long minutes = (startupTime / 1000) / 60;
                long seconds = (startupTime / 1000) % 60;

                float guiScale = (float)Minecraft.getMinecraft().gameSettings.guiScale;
                if(guiScale <= 0) guiScale = 1; // failsafe to prevent divide by 0

                String txt = "Startup took " + minutes + "m " + seconds + "s.";
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(txt, (mainMenu.width - Minecraft.getMinecraft().fontRenderer.getStringWidth(txt))/2, 10, Color.YELLOW.getRGB());
            }

        }else if(hasBeenMainMenu){
            hasLeftMainMenu = true;
        }
    }
}
