package net.minecraftcapes.v1_21_5.mixins;

import net.labymod.api.util.CastUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftcapes.MinecraftCapes;
import net.minecraftcapes.player.PlayerHandler;
import net.minecraftcapes.v1_21_5.ExtendedRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {

    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At(value = "TAIL"))
    public void extractRenderState(AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState, float p_364121_, CallbackInfo ci) {
        ExtendedRenderState extendedRenderState = (ExtendedRenderState) playerRenderState;
        PlayerHandler playerHandler = PlayerHandler.get(abstractClientPlayer.getUUID());
        if(playerHandler.getHasInfo()) {
            playerRenderState.isUpsideDown = playerHandler.isUpsideDown();
            extendedRenderState.minecraftcapes$setCapeEnabled(MinecraftCapes.getConfig().isCapeVisible());
            extendedRenderState.minecraftcapes$setGapeGlint(playerHandler.getHasCapeGlint());
            extendedRenderState.minecraftcapes$setEarsEnabled(MinecraftCapes.getConfig().isEarsVisible());
            extendedRenderState.minecraftcapes$setEarsTexture(CastUtil.cast(playerHandler.getEarLocation()));
        }
    }
}