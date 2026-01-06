package net.minecraftcapes;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.models.OperatingSystem;
import net.labymod.api.util.MethodOrder;
import net.minecraftcapes.player.DownloadManager;

import java.net.URI;

@ConfigName("settings")
public class MinecraftCapesConfig extends AddonConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @MethodOrder(after = "enabled")
    @ButtonWidget.ButtonSetting
    public void openWebsite() {
        URI uri = URI.create("https://minecraftcapes.net");
        OperatingSystem.getPlatform().openUri(uri);
    }

    @SwitchSetting
    private final ConfigProperty<Boolean> capeVisible = new ConfigProperty<>(true);

    @SwitchSetting
    private final ConfigProperty<Boolean> earsVisible = new ConfigProperty<>(true);

    @MethodOrder(after = "earsVisible")
    @ButtonWidget.ButtonSetting
    public void forceRefresh() {
        Player player = MinecraftCapes.getLabyAPI().minecraft().getClientPlayer();
        if(player != null) {
            DownloadManager.prepareDownload(
                    player.getUniqueId(),
                    player.getName(),
                    true
            );
        }
    }

    @Override
    public ConfigProperty<Boolean> enabled() {
        return this.enabled;
    }

    public boolean isCapeVisible() {
        return this.enabled.get() && this.capeVisible.get();
    }

    public boolean isEarsVisible() {
        return this.enabled.get() && this.earsVisible.get();
    }
}
