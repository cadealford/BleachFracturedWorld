package io.github.cadealford.bleachfracturedworld;

import io.github.cadealford.bleachfracturedworld.client.ClientProfileCache;
import io.github.cadealford.bleachfracturedworld.client.SpiritualEnergyHud;
import io.github.cadealford.bleachfracturedworld.client.debug.ClientDebugState;
import io.github.cadealford.bleachfracturedworld.client.debug.PauseScreenDebugButton;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = BleachFracturedWorld.MODID, dist = Dist.CLIENT)
public class BleachFracturedWorldClient {
    public BleachFracturedWorldClient(IEventBus modEventBus) {
        modEventBus.addListener(SpiritualEnergyHud::register);
        NeoForge.EVENT_BUS.addListener(BleachFracturedWorldClient::onLogout);
        NeoForge.EVENT_BUS.addListener(PauseScreenDebugButton::onScreenInit);
    }

    private static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientProfileCache.clear();
        ClientDebugState.clear();
    }
}
