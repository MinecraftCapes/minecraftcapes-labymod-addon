package net.minecraftcapes.v1_12_2.mixins;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftcapes.MinecraftCapes;
import net.minecraftcapes.player.PlayerHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerCape.class)
public class MixinCapeLayer {

    @Final
    @Shadow
    private RenderPlayer playerRenderer;

    @Unique
    private static final ResourceLocation minecraftcapes$ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    @Inject(method = "doRenderLayer(Lnet/minecraft/client/entity/AbstractClientPlayer;FFFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPlayer;renderCape(F)V", shift = At.Shift.AFTER), cancellable = true)
    public void minecraftcapes$addCapeGlint(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, CallbackInfo ci) {
        // Retrieve the player handler from playerRenderState
        PlayerHandler playerHandler = PlayerHandler.get(entitylivingbaseIn.getUniqueID());

        // Redirect to custom buffer if player has cape glint, otherwise use the default buffer
        if (MinecraftCapes.getConfig().isCapeVisible() && playerHandler.getHasCapeGlint()) {
            LayerArmorBase.renderEnchantedGlint(this.playerRenderer, entitylivingbaseIn, new ModelBase() {
                @Override
                public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
                    playerRenderer.getMainModel().renderCape(0.0625F);
                }
            }, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

}
