package io.github.cadealford.bleachfracturedworld.player;

import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface ProfileSnapshotSender {
    void send(ServerPlayer player);
}
