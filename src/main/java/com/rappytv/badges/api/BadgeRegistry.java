package com.rappytv.badges.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import net.labymod.utils.request.DownloadServerRequest;
import net.labymod.utils.request.RequestException;
import net.labymod.utils.request.ServerResponse;

public class BadgeRegistry {
    public static final BadgeRegistry instance = new BadgeRegistry();

    private static final String URL_API_BASE = "https://laby.net";
    private static final String URL_API_BADGES = URL_API_BASE + "/api/badges";
    private static final String URL_API_BADGE_USERS = URL_API_BASE + "/api/badge/%s";
    private static final String URL_API_BADGE_ICON = URL_API_BASE + "/texture/badge-small/%s.png";

    private static final int[] EXCLUDED_BADGES = {1, 3};

    private final Executor executor = Executors.newFixedThreadPool(5);
    private final List<Badge> badges = new ArrayList<Badge>();

    private Map<UUID, Badge[]> userCache = new HashMap<UUID, Badge[]>();

    public BadgeRegistry() {
        DownloadServerRequest.getJsonObjectAsync(URL_API_BADGES, new ServerResponse<JsonElement>() {

            private final BadgeRegistry registry = BadgeRegistry.this;

            @Override
            public void success(JsonElement jsonElement) {
                JsonArray array = jsonElement.getAsJsonArray();
                for (JsonElement element : array) {
                    int id = element.getAsJsonObject().get("id").getAsInt();
                    UUID uuid = UUID.fromString(element.getAsJsonObject().get("uuid").getAsString());
                    String name = element.getAsJsonObject().get("name").getAsString();
                    String description = element.getAsJsonObject().get("description").getAsString();

                    if (this.registry.isExcluded(id)) {
                        continue;
                    }

                    Badge badge = new Badge(id, uuid, name, description, String.format(URL_API_BADGE_ICON, uuid));
                    this.registry.registerBadge(badge);
                }
            }

            @Override
            public void failed(RequestException e) {
                e.printStackTrace();
            }
        });
    }

    private void registerBadge(final Badge badge) {
        this.badges.add(badge);

        DownloadServerRequest.getJsonObjectAsync(String.format(URL_API_BADGE_USERS, badge.getId()), new ServerResponse<JsonElement>() {

            private final BadgeRegistry registry = BadgeRegistry.this;

            @Override
            public void success(JsonElement jsonElement) {
                JsonArray array = jsonElement.getAsJsonArray();

                UUID[] uuids = new UUID[array.size()];
                int index = 0;
                for (JsonElement element : array) {
                    uuids[index] = UUID.fromString(element.getAsString());
                    index++;
                }
                badge.setUsers(uuids);

                // Clear cache
                this.registry.userCache = new HashMap<UUID, Badge[]>();
            }

            @Override
            public void failed(RequestException e) {
                e.printStackTrace();
            }
        });
    }

    private Badge[] getBadges(UUID uuid) {
        List<Badge> badges = new ArrayList<Badge>();
        for (Badge badge : this.badges) {
            if (badge.hasUser(uuid)) {
                badges.add(badge);
            }
        }
        return badges.toArray(new Badge[0]);
    }

    public Badge[] getCachedBadgesOfUser(final UUID uuid) {
        Badge[] array = this.userCache.get(uuid);
        if (array == null) {
            this.userCache.put(uuid, array = new Badge[0]);

            this.executor.execute(new Runnable() {
                private final BadgeRegistry registry = BadgeRegistry.this;

                @Override
                public void run() {
                    Badge[] badges = this.registry.getBadges(uuid);
                    this.registry.userCache.put(uuid, badges);
                }
            });
        }
        return array;
    }

    public boolean isExcluded(int id) {
        for (int excludedId : EXCLUDED_BADGES) {
            if (excludedId == id) {
                return true;
            }
        }
        return false;
    }
}
