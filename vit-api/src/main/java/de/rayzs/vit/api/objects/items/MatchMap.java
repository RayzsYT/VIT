package de.rayzs.vit.api.objects.items;

import java.util.HashMap;

public record MatchMap(
        String mapName,
        String mapUrl,
        String mapId
) {

    private final static HashMap<String, MatchMap> MAP_NAMES = new HashMap<>();
    private final static HashMap<String, MatchMap> MAP_URLS = new HashMap<>();
    private final static HashMap<String, MatchMap> MAP_IDS = new HashMap<>();


    /**
     * Registers a map.
     *
     * @param mapId Map id.
     * @param mapUrl Map url.
     * @param mapName Map name.
     */
    public static void loadMap(
            final String mapId,
            final String mapUrl,
            final String mapName
    ) {
        final MatchMap map = new MatchMap(mapId, mapUrl, mapName);

        MAP_NAMES.putIfAbsent(mapName, map);
        MAP_URLS.putIfAbsent(mapUrl, map);
        MAP_IDS.putIfAbsent(mapId, map);
    }

    /**
     * Get map by map id.
     * @param mapId Map id.
     *
     * @return Map if found. NULL otherwise.
     */
    public static MatchMap getMapById(final String mapId) {
        return MAP_IDS.get(mapId);
    }

    /**
     * Get map by map url.
     * @param mapUrl Map url.
     *
     * @return Map if found. NULL otherwise.
     */
    public static MatchMap getMapByUrl(final String mapUrl) {
        return MAP_URLS.get(mapUrl);
    }

    /**
     * Get map by map name.
     * @param mapName Map name.
     *
     * @return Map if found. NULL otherwise.
     */
    public static MatchMap getMapByName(final String mapName) {
        return MAP_NAMES.get(mapName);
    }
}
