package net.minecraftcapes.player;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import net.labymod.api.LabyAPI;
import net.labymod.api.client.Minecraft;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.client.network.PlayerSkin;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.client.resources.texture.DynamicTexture;
import net.labymod.api.client.resources.texture.GameImage;
import net.labymod.api.client.resources.texture.SimpleTexture;
import net.minecraftcapes.MinecraftCapes;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static net.minecraftcapes.MinecraftCapes.MOD_ID;

public class PlayerHandler {

    private static HashMap<UUID, PlayerHandler> instances = new HashMap<UUID, PlayerHandler>();

    @Setter
    private boolean hasStaticCape = false;
    @Setter
    private boolean hasEars = false;
    @Setter
    private boolean hasAnimatedCape = false;
    @Getter
    @Setter
    private Boolean showCape = true;
    @Getter
    @Setter
    private Boolean hasCapeGlint = false;
    @Getter
    @Setter
    private boolean upsideDown = false;
    @Getter
    @Setter
    private Boolean hasInfo = false;
    @Setter
    @Getter
    private UUID playerUUID;

    @Getter
    private Int2ObjectMap<GameImage> animatedCape;

    //Animated Cape Settings
    private long lastFrameTime = 0;
    private int lastFrame = 0;
    private int capeInterval = 100;

    public PlayerHandler(UUID uuid) {
        this.playerUUID = uuid;
        PlayerHandler.instances.put(playerUUID, this);
    }

    @Deprecated
    public PlayerHandler(Player player) {
        this.playerUUID = player.getUniqueId();
        PlayerHandler.instances.put(playerUUID, this);
    }

    /**
     * Tries to get the PlayerHandler instance from a player
     *
     * @param uuid the players uuid
     * @return The player handler
     */
    public static PlayerHandler get(UUID uuid) {
        PlayerHandler playerHandler = PlayerHandler.instances.get(uuid);
        return playerHandler == null ? new PlayerHandler(uuid) : playerHandler;
    }

    public static void remove(UUID uuid) {
        PlayerHandler playerHandler = PlayerHandler.instances.get(uuid);
        if(playerHandler != null) {
            playerHandler.removeCape();
            playerHandler.removeEars();
        }

        PlayerHandler.instances.remove(uuid);
    }

    /**
     * Tries to get the PlayerHandler instance from a player
     *
     * @param player
     * @return
     */
    @Deprecated
    public static PlayerHandler getFromPlayer(Player player) {
        return get(player.getUniqueId());
    }

    /**
     * Gets the cape texture and resizes or splits it accordingly
     *
     * @param capeImage
     */
    public void applyCape(GameImage capeImage) {
        //If the height is not 1/2 the width (32 == 64/2) then its an animated cape
        if (capeImage.getHeight() != capeImage.getWidth() / 2) {
            Int2ObjectMap<GameImage> animatedCapeFrames = new Int2ObjectOpenHashMap<>();
            int totalFrames = capeImage.getHeight() / (capeImage.getWidth() / 2);
            for (int currentFrame = 0; currentFrame < totalFrames; currentFrame++) {
                GameImage frame = GameImage.IMAGE_PROVIDER.createImage(capeImage.getWidth(),
                        capeImage.getWidth() / 2);
                for (int x = 0; x < frame.getWidth(); x++) {
                    for (int y = 0; y < frame.getHeight(); y++) {
                        frame.setARGB(x, y,
                                capeImage.getARGB(x, y + (currentFrame * (capeImage.getWidth() / 2))));
                    }
                }
                animatedCapeFrames.put(currentFrame, frame);
            }
            this.setAnimatedCape(animatedCapeFrames);
            MinecraftCapes.getLogger().debug("Animated cape loaded for {}", playerUUID);
        } else {
            int imageWidth = 64;
            int imageHeight = 32;

            for (int srcWidth = capeImage.getWidth(), srcHeight = capeImage.getHeight();
                 imageWidth < srcWidth || imageHeight < srcHeight;
                 imageWidth *= 2, imageHeight *= 2) {
            }

            final GameImage imgNew = GameImage.IMAGE_PROVIDER.createImage(imageWidth, imageHeight);
            for (int x = 0; x < capeImage.getWidth(); x++) {
                for (int y = 0; y < capeImage.getHeight(); y++) {
                    imgNew.setARGB(x, y, capeImage.getARGB(x, y));
                }
            }

            capeImage.close();
            this.applyTexture(ResourceLocation.create(MOD_ID, "capes/" + playerUUID),
                    imgNew);
            this.setHasStaticCape(true);
            this.setHasAnimatedCape(false);
            MinecraftCapes.getLogger().debug("Static cape loaded for {}", playerUUID);
        }
    }

    public void applyEars(GameImage earImage) {
        applyTexture(ResourceLocation.create(MOD_ID, "ears/" + playerUUID), earImage);
        this.setHasEars(true);
    }

    /**
     * Unregister the cape
     */
    public void removeCape() {
        if (!hasStaticCape && !hasAnimatedCape) {
            return;
        }

        MinecraftCapes.getLogger().debug("Removing cape for {}", playerUUID);

        this.setHasAnimatedCape(false);
        this.setHasStaticCape(false);

        MinecraftCapes.getLabyAPI().minecraft().executeOnRenderThread(() -> {
            SimpleTexture.simple(ResourceLocation.create(MOD_ID, "capes/" + playerUUID)).release();

            for (int i = 0; i < getAnimatedCape().size() - 1; i++) {
                SimpleTexture.simple(ResourceLocation.create(MOD_ID, String.format("capes/%s/%d", playerUUID, i))).release();
            }
        });
    }

    /**
     * Unregister the ears
     */
    public void removeEars() {
        if (!hasEars) {
            return;
        }

        MinecraftCapes.getLogger().debug("Removing ears for {}", playerUUID);

        this.setHasEars(false);
        MinecraftCapes.getLabyAPI().minecraft().executeOnRenderThread(() -> SimpleTexture.simple(ResourceLocation.create(MOD_ID, "ears/" + playerUUID)).release());
    }

    /**
     * Sets the animated cape textures and loads all resources to memory
     *
     * @param animatedCape
     */
    public void setAnimatedCape(Int2ObjectMap<GameImage> animatedCape) {
        MinecraftCapes.getLogger().debug("Setting animated cape for {}", playerUUID);
        this.animatedCape = animatedCape;
        this.setHasStaticCape(false);
        this.setHasAnimatedCape(true);
        this.loadFramesToResource();
    }

    /**
     * Load all GameImages into a ResourceLocation
     */
    private void loadFramesToResource() {
        MinecraftCapes.getLogger().debug("Loading resources to memory for {}", playerUUID);
        for (final HashMap.Entry<Integer, GameImage> entry : getAnimatedCape().entrySet()) {
            ResourceLocation currentResource = ResourceLocation.create(MOD_ID,
                    String.format("capes/%s/%d", playerUUID, entry.getKey()));
            applyTexture(currentResource, entry.getValue());
        }
    }

    /**
     * Gets the current frame for the player
     *
     * @return ResourceLocation
     */
    private ResourceLocation getFrame() {
        final long time = System.currentTimeMillis();
        if (time > lastFrameTime + capeInterval) {
            int currentFrameNo = (lastFrame + 1 > getAnimatedCape().size() - 1) ? 0 : lastFrame + 1;

            lastFrame = currentFrameNo;
            lastFrameTime = time;

            return ResourceLocation.create(MOD_ID,
                    String.format("capes/%s/%d", playerUUID, currentFrameNo));
        }
        return ResourceLocation.create(MOD_ID, String.format("capes/%s/%d", playerUUID, lastFrame));
    }

    /**
     * Returns the player current cape resource
     *
     * @return
     */
    public ResourceLocation getCapeLocation() {
        return hasStaticCape ? ResourceLocation.create(MOD_ID, "capes/" + playerUUID)
                : hasAnimatedCape ? getFrame() : null;
    }

    /**
     * Returns the players ear resource
     *
     * @return
     */
    public ResourceLocation getEarLocation() {
        return hasEars ? ResourceLocation.create(MOD_ID, "ears/" + playerUUID) : null;
    }

    /**
     * Applys a texture on the render thread
     *
     * @param resourceLocation
     * @param bufferedImage
     */
    private void applyTexture(final ResourceLocation resourceLocation, final GameImage bufferedImage) {
        MinecraftCapes.getLabyAPI().minecraft().executeOnRenderThread(() -> bufferedImage.uploadTextureAt(resourceLocation));

    }

    /**
     * A nice to string thing
     *
     * @return
     */
    @Override
    public String toString() {
        return "PlayerHandler{" +
                "hasStaticCape=" + hasStaticCape +
                ", hasEars=" + hasEars +
                ", hasAnimatedCape=" + hasAnimatedCape +
                ", hasCapeGlint=" + hasCapeGlint +
                ", upsideDown=" + upsideDown +
                ", hasInfo=" + hasInfo +
                ", playerUUID=" + playerUUID +
                ", animatedCape=" + animatedCape +
                ", lastFrameTime=" + lastFrameTime +
                ", lastFrame=" + lastFrame +
                ", capeInterval=" + capeInterval +
                '}';
    }
}