package net.minecraftcapes.v1_8_9.mixins;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftcapes.MinecraftCapes;
import net.minecraftcapes.player.PlayerHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {

    @Shadow
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Inject(method = "hasPlayerInfo", at = @At(value = "HEAD"), cancellable = true)
    private void minecraftcapes$updatePlayerInfo(CallbackInfoReturnable<Boolean> cir) {
        PlayerHandler playerHandler = PlayerHandler.get(((AbstractClientPlayer) (Object) this).getUniqueID());
        if(playerHandler.getHasInfo() || this.getPlayerInfo() != null) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getLocationCape", at = @At(value = "RETURN"), cancellable = true)
    private void minecraftcapes$getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        PlayerHandler playerHandler = PlayerHandler.get(((AbstractClientPlayer) (Object) this).getUniqueID());
        if(playerHandler.getCapeLocation() != null && MinecraftCapes.getConfig().isCapeVisible().get()) {
            cir.setReturnValue((ResourceLocation) playerHandler.getCapeLocation());
        }
    }

}
