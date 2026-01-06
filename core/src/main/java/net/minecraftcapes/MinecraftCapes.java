package net.minecraftcapes;

import lombok.Getter;
import net.labymod.api.Constants;
import net.labymod.api.LabyAPI;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.configuration.loader.Config;
import net.labymod.api.models.addon.annotation.AddonMain;
import net.labymod.api.util.logging.Logging;
import net.minecraftcapes.listeners.PlayerListener;

import java.nio.file.Path;

@AddonMain
public class MinecraftCapes extends LabyAddon<MinecraftCapesConfig> {

    public static final String MOD_ID = "minecraftcapes";
    public static String MINECRAFT_VERSION;

    @Getter
    private static LabyAPI labyAPI;
    @Getter
    private static Logging logger;
    @Getter
    private static Path configDir = Constants.Files.CONFIGS.resolve(MOD_ID);
    @Getter
    private static MinecraftCapesConfig config;

    @Override
    protected void enable() {
        this.registerSettingCategory();
        this.saveConfiguration();

        this.registerListener(new PlayerListener());

        labyAPI = this.labyAPI();
        logger = this.logger();
        config = this.configuration();

        MINECRAFT_VERSION = "labymod-" + this.labyAPI().minecraft().getVersion();

        this.logger().info("Enabled MinecraftCapes");
    }

    @Override
    protected Class<MinecraftCapesConfig> configurationClass() {
        return MinecraftCapesConfig.class;
    }
}
