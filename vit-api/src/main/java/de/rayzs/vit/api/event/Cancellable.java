package de.rayzs.vit.api.event;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancel);
}
