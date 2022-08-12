package com.rappytv.badges.api;

import net.labymod.main.LabyMod;
import net.minecraft.client.renderer.GlStateManager;

import java.util.UUID;

public class Badge {
    private final int id;
    private final UUID uuid;
    private final String name;
    private final String description;
    private final String iconUrl;

    private UUID[] users = new UUID[0];

    public Badge(int id, UUID uuid, String name, String description, String iconUrl) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
    }

    public void setUsers(UUID[] users) {
        this.users = users;
    }

    public UUID[] getUsers() {
        return this.users;
    }

    public int getId() {
        return this.id;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean hasUser(UUID uuid) {
        for (UUID user : this.users) {
            if (user.equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public void renderBadge(double x, double y, double scale, double badgeSize) {
        GlStateManager.enableBlend();
        LabyMod.getInstance().getDrawUtils().drawImageUrl(this.iconUrl, x, y, 256, 256, badgeSize * scale, badgeSize * scale);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
    }
}
