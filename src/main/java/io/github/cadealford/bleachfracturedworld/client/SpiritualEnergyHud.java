package io.github.cadealford.bleachfracturedworld.client;

import io.github.cadealford.bleachfracturedworld.BleachFracturedWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public final class SpiritualEnergyHud {
    private static final ResourceLocation LAYER_ID = ResourceLocation.fromNamespaceAndPath(
            BleachFracturedWorld.MODID, "spiritual_energy");

    private SpiritualEnergyHud() {
    }

    public static void register(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, LAYER_ID, (graphics, deltaTracker) ->
                ClientProfileCache.snapshot().filter(snapshot -> snapshot.path().isChosen()).ifPresent(snapshot -> {
                    var minecraft = Minecraft.getInstance();
                    String text = "Spiritual Energy: " + snapshot.currentEnergy() + "/" + snapshot.maxEnergy();
                    graphics.drawString(minecraft.font, text, 10, graphics.guiHeight() - 40, 0xFFFFFFFF, true);
                }));
    }
}
