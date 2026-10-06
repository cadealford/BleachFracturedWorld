package io.github.cadealford.bleachfracturedworld.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class BwfConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue DEBUG_TOOLS_ENABLED = BUILDER
            .comment("Enable the private-development BWF debug console and server actions.")
            .define("debugToolsEnabled", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private BwfConfig() {
    }
}
