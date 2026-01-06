package net.minecraftcapes.v1_19_2.mixins;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
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
    protected abstract PlayerInfo getPlayerInfo();

    @Inject(method = "isCapeLoaded", at = @At(value = "HEAD"), cancellable = true)
    private void minecraftcapes$hasCape(CallbackInfoReturnable<Boolean> cir) {
        PlayerHandler playerHandler = PlayerHandler.get(((AbstractClientPlayer) (Object) this).getUUID());
        if(playerHandler.getHasInfo() || this.getPlayerInfo() != null) {
            cir.setReturnValue(true);
        }
}

    @Inject(method = "getCloakTextureLocation", at = @At(value = "RETURN"), cancellable = true)
    private void minecraftcapes$getLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        PlayerHandler playerHandler = PlayerHandler.get(((AbstractClientPlayer) (Object) this).getUUID());
        if(playerHandler.getCapeLocation() != null && MinecraftCapes.getConfig().isCapeVisible()) {
            cir.setReturnValue((ResourceLocation) playerHandler.getCapeLocation());
        }
    }

}
