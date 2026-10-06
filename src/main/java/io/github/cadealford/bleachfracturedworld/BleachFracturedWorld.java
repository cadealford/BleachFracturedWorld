package io.github.cadealford.bleachfracturedworld;

import io.github.cadealford.bleachfracturedworld.command.SpiritualIdentityCommands;
import io.github.cadealford.bleachfracturedworld.config.BwfConfig;
import io.github.cadealford.bleachfracturedworld.network.BwfNetworking;
import io.github.cadealford.bleachfracturedworld.player.PlayerProfiles;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(BleachFracturedWorld.MODID)
public class BleachFracturedWorld {
    public static final String MODID = "bleachfracturedworld";

    public BleachFracturedWorld(IEventBus modEventBus, ModContainer modContainer) {
        PlayerProfiles.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.SERVER, BwfConfig.SPEC);
        modEventBus.addListener(BwfNetworking::register);
        NeoForge.EVENT_BUS.addListener(BwfNetworking::onLogin);
        NeoForge.EVENT_BUS.addListener(BwfNetworking::onRespawn);
        NeoForge.EVENT_BUS.addListener(SpiritualIdentityCommands::register);
    }
}
