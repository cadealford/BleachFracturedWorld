package io.github.cadealford.bleachfracturedworld.debug;

import net.minecraft.server.level.ServerPlayer;

@FunctionalInterface
public interface DebugAccessPolicy {
    boolean canAccess(ServerPlayer player);
}
