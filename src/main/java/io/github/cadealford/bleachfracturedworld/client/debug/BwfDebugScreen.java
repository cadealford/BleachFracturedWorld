package io.github.cadealford.bleachfracturedworld.client.debug;

import io.github.cadealford.bleachfracturedworld.client.ClientProfileCache;
import io.github.cadealford.bleachfracturedworld.debug.DebugAction;
import io.github.cadealford.bleachfracturedworld.network.DebugActionRequestPayload;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public final class BwfDebugScreen extends Screen {
    private final Screen parent;
    private final List<Button> mutationButtons = new ArrayList<>();

    public BwfDebugScreen(Screen parent) {
        super(Component.translatable("screen.bleachfracturedworld.debug.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        mutationButtons.clear();
        int left = width / 2 - 154;
        int top = height / 2 + 12;
        addAction(DebugAction.CHOOSE_SHINIGAMI, "screen.bleachfracturedworld.debug.choose_shinigami", left, top);
        addAction(DebugAction.CHOOSE_QUINCY, "screen.bleachfracturedworld.debug.choose_quincy", left + 158, top);
        addAction(DebugAction.RESET_PROFILE, "screen.bleachfracturedworld.debug.reset", left, top + 24);
        addAction(DebugAction.ENERGY_ZERO, "screen.bleachfracturedworld.debug.energy_zero", left + 158, top + 24);
        addAction(DebugAction.ENERGY_HALF, "screen.bleachfracturedworld.debug.energy_half", left, top + 48);
        addAction(DebugAction.ENERGY_FULL, "screen.bleachfracturedworld.debug.energy_full", left + 158, top + 48);

        Button refresh = addRenderableWidget(Button.builder(
                Component.translatable("screen.bleachfracturedworld.debug.refresh"),
                button -> send(DebugAction.REFRESH)).bounds(left, top + 72, 150, 20).build());
        refresh.active = connected();
        addRenderableWidget(Button.builder(
                Component.translatable("gui.done"),
                button -> onClose()).bounds(left + 158, top + 72, 150, 20).build());
        updateButtonState();
    }

    private void addAction(DebugAction action, String translationKey, int x, int y) {
        Button button = addRenderableWidget(Button.builder(
                Component.translatable(translationKey),
                ignored -> send(action)).bounds(x, y, 150, 20).build());
        mutationButtons.add(button);
    }

    private void send(DebugAction action) {
        if (connected()) {
            PacketDistributor.sendToServer(new DebugActionRequestPayload(action));
        }
    }

    @Override
    public void tick() {
        updateButtonState();
    }

    private void updateButtonState() {
        boolean enabled = model().mutationsEnabled();
        mutationButtons.forEach(button -> button.active = enabled);
    }

    private DebugScreenModel model() {
        return DebugScreenModel.from(connected(), ClientProfileCache.snapshot(), ClientDebugState.latest());
    }

    private boolean connected() {
        return minecraft != null && minecraft.level != null && minecraft.getConnection() != null;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        DebugScreenModel model = model();
        int left = width / 2 - 154;
        int top = height / 2 - 86;
        graphics.drawCenteredString(font, title, width / 2, top - 22, 0xFFFFFFFF);
        drawLine(graphics, "Connection", model.connected() ? "Connected" : "Disconnected", left, top);
        drawLine(graphics, "Snapshot", model.hasSnapshot() ? "Authoritative" : "Missing", left, top + 14);
        drawLine(graphics, "Path", model.path(), left, top + 28);
        drawLine(graphics, "Spiritual Energy", model.energy(), left, top + 42);
        drawLine(graphics, "Mastery", model.mastery(), left, top + 56);
        drawLine(graphics, "Revision", model.revision(), left, top + 70);
        drawLine(graphics, "Last Result", model.lastResult(), left, top + 84);
    }

    private void drawLine(GuiGraphics graphics, String label, String value, int x, int y) {
        graphics.drawString(font, label + ": " + value, x, y, 0xFFE6E6E6, false);
    }

    @Override
    public void onClose() {
        if (minecraft != null) {
            minecraft.setScreen(parent);
        }
    }
}
