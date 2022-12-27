package org.embeddedt.startuptimer.core;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;
import org.embeddedt.startuptimer.StartupTimerConfig;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("Startup Timer")
public class LoadingPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {
    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }
    public static long expectedTime = 0;

    public LoadingPlugin() {
        if(FMLLaunchHandler.side() == Side.CLIENT) {
            StartupTimerConfig.loadConfig();
            expectedTime = StartupTimerConfig.getTimeEstimate();
        }
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        return ImmutableList.of("mixins.startuptimer.json");
    }
}
