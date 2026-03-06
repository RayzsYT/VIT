package de.rayzs.vit.api.objects.items;

public record Season(
        String id,      // Season id
        String name,    // Season name
        String type,    // Season type
        boolean active  // Is season currently active?
) { }
