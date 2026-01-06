package net.minecraftcapes.listeners;

import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.network.playerinfo.PlayerInfoAddEvent;
import net.labymod.api.event.client.network.playerinfo.PlayerInfoRemoveEvent;
import net.labymod.api.mojang.GameProfile;
import net.minecraftcapes.player.DownloadManager;
import net.minecraftcapes.player.PlayerHandler;

public class PlayerListener {

    @Subscribe
    public void onPlayerAdd(PlayerInfoAddEvent event) {
        GameProfile gameProfile = event.playerInfo().profile();
        DownloadManager.prepareDownload(gameProfile.getUniqueId(), gameProfile.getUsername(), false);
    }

    @Subscribe
    public void onPlayerRemove(PlayerInfoRemoveEvent event) {
        GameProfile gameProfile = event.playerInfo().profile();
        PlayerHandler.remove(gameProfile.getUniqueId());
    }

}
