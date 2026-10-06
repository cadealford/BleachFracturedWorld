package io.github.cadealford.bleachfracturedworld.player;

import io.github.cadealford.bleachfracturedworld.config.BwfConfig;
import io.github.cadealford.bleachfracturedworld.debug.DebugProfileService;
import io.github.cadealford.bleachfracturedworld.network.BwfNetworking;

public final class BwfServices {
    public static final ProfileService PROFILES = new ProfileService(BwfNetworking::sendProfileSnapshot);
    public static final DebugProfileService DEBUG = new DebugProfileService(
            PROFILES,
            BwfNetworking::sendProfileSnapshot,
            player -> BwfConfig.DEBUG_TOOLS_ENABLED.get());

    private BwfServices() {
    }
}
