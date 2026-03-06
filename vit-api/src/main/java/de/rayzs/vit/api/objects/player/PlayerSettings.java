package de.rayzs.vit.api.objects.player;

public record PlayerSettings(
        boolean levelHidden,    // Are levels hidden?
        boolean incognito       // Is player in incognito? (Hides level + name)
) { }
