package net.minecraftcapes.v1_21_11.mixins;

import com.mojang.authlib.GameProfile;
import net.labymod.api.util.CastUtil;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
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
        DownloadManager.prepareDownload(profile.id(), profile.name(), false);
    }

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    public void minecraftcapes$getSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        PlayerHandler playerHandler = PlayerHandler.get(profile.id());
        //Check player handler is loaded
        if(playerHandler.getHasInfo()) {
            //Set initial values
            PlayerSkin original = cir.getReturnValue();
            ClientAsset.Texture capeTexture = original.cape();
            ClientAsset.Texture elytraTexture = original.elytra();

            //If we have a cape, lets load it
            if(MinecraftCapes.getConfig().isCapeVisible() && playerHandler.getCapeLocation() != null) {
                Identifier capeLocation = CastUtil.cast(playerHandler.getCapeLocation());
                capeTexture = new ClientAsset.ResourceTexture(capeLocation, capeLocation);
                elytraTexture = capeTexture;
            }

            //Return new player skin
            cir.setReturnValue(new PlayerSkin(
                    original.body(),
                    capeTexture, elytraTexture,
                    original.model(), original.secure()
            ));
        }
    }

}
