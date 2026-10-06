package io.github.cadealford.bleachfracturedworld.client.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class PauseScreenDebugButton {
    private PauseScreenDebugButton() {
    }

    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!(event.getScreen() instanceof PauseScreen pauseScreen)
                || minecraft.level == null
                || minecraft.getConnection() == null) {
            return;
        }

        event.addListener(Button.builder(
                Component.translatable("screen.bleachfracturedworld.debug.open"),
                button -> minecraft.setScreen(new BwfDebugScreen(pauseScreen)))
                .bounds(pauseScreen.width - 106, 6, 100, 20)
                .build());
    }
}
