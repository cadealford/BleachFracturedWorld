package io.github.cadealford.bleachfracturedworld.network;

import com.mojang.logging.LogUtils;
import io.github.cadealford.bleachfracturedworld.client.ClientProfileCache;
import io.github.cadealford.bleachfracturedworld.client.debug.ClientDebugState;
import io.github.cadealford.bleachfracturedworld.debug.DebugAction;
import io.github.cadealford.bleachfracturedworld.debug.DebugActionResult;
import io.github.cadealford.bleachfracturedworld.player.BwfServices;
import io.github.cadealford.bleachfracturedworld.player.PlayerProfiles;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import org.slf4j.Logger;

public final class BwfNetworking {
    public static final String PROTOCOL_VERSION = "1";
    private static final Logger LOGGER = LogUtils.getLogger();

    private BwfNetworking() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(
                ProfileSnapshotPayload.TYPE,
                ProfileSnapshotPayload.STREAM_CODEC,
                (payload, context) -> ClientProfileCache.accept(payload));
        registrar.playToServer(
                DebugActionRequestPayload.TYPE,
                DebugActionRequestPayload.STREAM_CODEC,
                BwfNetworking::handleDebugRequest);
        registrar.playToClient(
                DebugActionResultPayload.TYPE,
                DebugActionResultPayload.STREAM_CODEC,
                (payload, context) -> {
                    ClientProfileCache.accept(payload.snapshot());
                    ClientDebugState.accept(payload);
                });
    }

    public static void sendProfileSnapshot(ServerPlayer player) {
        if (NetworkRegistry.hasChannel(player.connection, ProfileSnapshotPayload.TYPE.id())) {
            PacketDistributor.sendToPlayer(player, ProfileSnapshotPayload.from(player.getData(PlayerProfiles.PROFILE)));
        }
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sendProfileSnapshot(player);
        }
    }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sendProfileSnapshot(player);
        }
    }

    private static void handleDebugRequest(
            DebugActionRequestPayload payload,
            net.neoforged.neoforge.network.handling.IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        var action = DebugAction.fromId(payload.actionId());
        DebugActionResult result = action
                .map(value -> BwfServices.DEBUG.perform(player, value))
                .orElseGet(() -> new DebugActionResult(
                        DebugActionResult.Code.INVALID_ACTION,
                        player.getData(PlayerProfiles.PROFILE)));

        if (NetworkRegistry.hasChannel(player.connection, DebugActionResultPayload.TYPE.id())) {
            PacketDistributor.sendToPlayer(player, DebugActionResultPayload.from(result));
        }
        LOGGER.info(
                "BWF debug actor={} action={} result={} revision={}",
                player.getUUID(),
                action.map(Enum::name).orElse("UNKNOWN:" + payload.actionId()),
                result.code(),
                result.profile().revision());
    }
}
