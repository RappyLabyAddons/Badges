package com.rappytv.badges.events;

import com.rappytv.badges.BadgesAddon;
import com.rappytv.badges.api.Badge;
import com.rappytv.badges.api.BadgeRegistry;
import net.labymod.api.events.RenderEntityEvent;
import net.labymod.main.LabyMod;
import net.labymod.user.User;
import net.labymod.user.group.EnumGroupDisplayType;
import net.labymod.user.group.LabyGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.lwjgl.opengl.GL11;

public class RenderEvent implements RenderEntityEvent {

    @Override
    public void onRender(Entity entity, double x, double y, double z, float partialTicks) {
        boolean canRender = Minecraft.isGuiEnabled() && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player) && !entity.isBeingRidden();
        if (!canRender) {
            return;
        }
        if (!(LabyMod.getSettings()).showMyName && entity.getUniqueID().equals(LabyMod.getInstance().getPlayerUUID())) {
            return;
        }

        float fixedPlayerViewX = Minecraft.getMinecraft().getRenderManager().playerViewX * (float) (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2 ? -1 : 1);
        User user = entity instanceof EntityPlayer ? LabyMod.getInstance().getUserManager().getUser(entity.getUniqueID()) : null;
        if (user != null && !entity.isSneaking() && BadgesAddon.enabled && (BadgesAddon.showOwnBadge || user.getUuid() != LabyMod.getInstance().getPlayerUUID()) && user.getUuid().version() == 4 && user.getUuid().getLeastSignificantBits() != 0 && user.getUuid().getMostSignificantBits() != 0) {
            LabyGroup labyGroup = user.getGroup();
            if (labyGroup != null) {
                int loops = 0;
                Badge[] badges = BadgeRegistry.instance.getCachedBadgesOfUser(user.getUuid());
                if (badges != null) {
                    double scale = (((float) BadgesAddon.size) / 100);
                    double badgeSize = 8;
                    double linewidth = badgeSize * scale * badges.length + 2 * (badges.length - 1);
                    double xPos = -(linewidth / 2);

                    for (Badge badge : badges) {
                        double xOffset = xPos + badgeSize * scale * loops + 2 * loops;
                        double yOffset = -10D;
                        if (labyGroup.getDisplayType() == EnumGroupDisplayType.ABOVE_HEAD) {
                            yOffset -= 6.5D;
                        }
                        double size;
                        if (user.getSubTitle() != null) {
                            size = user.getSubTitleSize();
                            yOffset -= size*6;
                        }

                        Scoreboard scoreboard = ((EntityPlayer) entity).getWorldScoreboard();
                        if (scoreboard != null) {
                            ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
                            if (scoreobjective != null)
                                yOffset -= ((LabyMod.getInstance().getDrawUtils().getFontRenderer()).FONT_HEIGHT);
                        }

                        yOffset += badgeSize-(badgeSize*scale);

                        float maxNameTagHeight = LabyMod.getSettings().cosmetics ? user.getMaxNameTagHeight() : 0.0F;

                        GlStateManager.pushMatrix();
                        GlStateManager.translate((float) x, (float) y + entity.height + 0.5F + maxNameTagHeight, (float) z);
                        GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
                        GlStateManager.translate((float) 0, yOffset, (float) 0);
                        GlStateManager.rotate(Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(-fixedPlayerViewX, 1.0F, 0.0F, 0.0F);
                        GlStateManager.disableLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        badge.renderBadge(xOffset, 0, scale, badgeSize);
                        GlStateManager.enableLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.resetColor();
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.popMatrix();

                        loops++;
                    }
                }
            }
        }
    }
}
