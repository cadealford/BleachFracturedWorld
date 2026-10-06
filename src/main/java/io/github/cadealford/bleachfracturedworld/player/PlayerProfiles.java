package io.github.cadealford.bleachfracturedworld.player;

import io.github.cadealford.bleachfracturedworld.BleachFracturedWorld;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class PlayerProfiles {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BleachFracturedWorld.MODID);

    public static final Supplier<AttachmentType<PlayerProfile>> PROFILE = ATTACHMENT_TYPES.register(
            "player_profile",
            () -> AttachmentType.builder(PlayerProfile::unchosen)
                    .serialize(PlayerProfile.CODEC)
                    .copyOnDeath()
                    .build());

    private PlayerProfiles() {
    }

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }
}
