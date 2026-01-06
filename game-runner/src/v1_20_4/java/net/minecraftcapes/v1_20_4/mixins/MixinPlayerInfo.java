package net.minecraftcapes.v1_20_4.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraftcapes.MinecraftCapes;
import net.minecraftcapes.player.DownloadManager;
import net.minecraftcapes.player.PlayerHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(PlayerInfo.class)
public class MixinPlayerInfo {

    @Shadow
    @Final
    private GameProfile profile;

    @Inject(method = "createSkinLookup", at = @At("HEAD"))
    private static void minecraftcapes$downloadPlayerInfo(GameProfile profile, CallbackInfoReturnable<Supplier<PlayerSkin>> cir) {
        DownloadManager.prepareDownload(profile.getId(), profile.getName(), false);
    }

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    public void minecraftcapes$getSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        PlayerHandler playerHandler = PlayerHandler.get(profile.getId());
        //Check player handler is loaded
        if(playerHandler.getHasInfo()) {
            //Set initial values
            PlayerSkin original = cir.getReturnValue();
            ResourceLocation capeTexture = original.capeTexture();
            ResourceLocation elytraTexture = original.elytraTexture();

            //If we have a cape, lets load it
            if(MinecraftCapes.getConfig().isCapeVisible() && playerHandler.getCapeLocation() != null) {
                capeTexture = (ResourceLocation) playerHandler.getCapeLocation();
                elytraTexture = capeTexture;
            }

            //Return new player skin
            cir.setReturnValue(new PlayerSkin(
                    original.texture(), original.textureUrl(),
                    capeTexture, elytraTexture,
                    original.model(), original.secure()
            ));
        }
    }

}
