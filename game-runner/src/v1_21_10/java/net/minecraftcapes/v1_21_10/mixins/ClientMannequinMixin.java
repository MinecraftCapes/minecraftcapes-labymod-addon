package net.minecraftcapes.v1_21_10.mixins;

import net.labymod.api.util.CastUtil;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.entity.ClientMannequin;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.level.Level;
import net.minecraftcapes.MinecraftCapes;
import net.minecraftcapes.player.DownloadManager;
import net.minecraftcapes.player.PlayerHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientMannequin.class)
public abstract class ClientMannequinMixin extends Mannequin implements ClientAvatarEntity {

    public ClientMannequinMixin(EntityType<Mannequin> p_446465_, Level p_446512_) {
        super(p_446465_, p_446512_);
    }

    @Inject(method = "updateSkin", at = @At("HEAD"))
    public void updateSkin(CallbackInfo ci) {
        PlayerHandler playerHandler = PlayerHandler.get(this.uuid);
        playerHandler.setPlayerUUID(this.getProfile().partialProfile().id());
        DownloadManager.prepareDownload(playerHandler);
    }

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    public void getSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        PlayerHandler playerHandler = PlayerHandler.get(this.uuid);

        //Check player handler is loaded
        if(playerHandler.getHasInfo()) {
            //Set initial values
            PlayerSkin original = cir.getReturnValue();
            ClientAsset.Texture capeTexture = original.cape();
            ClientAsset.Texture elytraTexture = original.elytra();

            //If we have a cape, lets load it
            if(MinecraftCapes.getConfig().isCapeVisible() && playerHandler.getCapeLocation() != null) {
                ResourceLocation capeLocation = CastUtil.cast(playerHandler.getCapeLocation());
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
