package com.rappytv.badges;

import com.rappytv.badges.events.RenderEvent;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.settings.elements.SliderElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;

import java.util.List;

public class BadgesAddon extends LabyModAddon {

    public static BadgesAddon instance;
    public static boolean enabled = true;
    public static boolean showOwnBadge = true;
    public static int size = 100;

    public static BadgesAddon get() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        getApi().getEventManager().register(new RenderEvent());
    }

    public void loadConfig() {
        enabled = getConfig().has("enabled") ? getConfig().get("enabled").getAsBoolean() : enabled;
        showOwnBadge = getConfig().has("showOwnBadge") ? getConfig().get("showOwnBadge").getAsBoolean() : showOwnBadge;
        size = getConfig().has("size") ? getConfig().get("size").getAsInt() : size;
    }

    protected void fillSettings(List<SettingsElement> list) {
        BooleanElement enabledEl = new BooleanElement("Enabled", new ControlElement.IconData(Material.LEVER), new Consumer<Boolean>() {

            @Override
            public void accept(Boolean value) {
                enabled = value;

                getConfig().addProperty("enabled", enabled);
                saveConfig();
            }
        }, enabled);

        BooleanElement showOwnBadgeEl = new BooleanElement("Show own Badges", new ControlElement.IconData(Material.SKULL_ITEM), new Consumer<Boolean>() {

            @Override
            public void accept(Boolean value) {
                showOwnBadge = value;

                getConfig().addProperty("showOwnBadge", showOwnBadge);
                saveConfig();
            }
        }, showOwnBadge);

        SliderElement badgeSizeEl = new SliderElement("Badge Size", new ControlElement.IconData("labymod/textures/settings/default/gui_scaling.png"), size)
                .setRange(20, 200)
                .setSteps(5)
                .addCallback(new Consumer<Integer>() {

                    @Override
                    public void accept(Integer value) {
                        size = value;

                        getConfig().addProperty("size", size);
                        saveConfig();
                    }
                });

        list.add(enabledEl);
        list.add(showOwnBadgeEl);
        list.add(badgeSizeEl);
    }
}
