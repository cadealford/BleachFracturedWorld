package io.github.cadealford.bleachfracturedworld.debug;

import io.github.cadealford.bleachfracturedworld.player.PlayerProfile;
import io.github.cadealford.bleachfracturedworld.player.PlayerProfiles;
import io.github.cadealford.bleachfracturedworld.player.ProfileMutationResult;
import io.github.cadealford.bleachfracturedworld.player.ProfileService;
import io.github.cadealford.bleachfracturedworld.player.ProfileSnapshotSender;
import io.github.cadealford.bleachfracturedworld.player.SpiritualPath;
import net.minecraft.server.level.ServerPlayer;

public final class DebugProfileService {
    private final ProfileService profileService;
    private final ProfileSnapshotSender snapshotSender;
    private final DebugAccessPolicy accessPolicy;

    public DebugProfileService(
            ProfileService profileService,
            ProfileSnapshotSender snapshotSender,
            DebugAccessPolicy accessPolicy) {
        this.profileService = profileService;
        this.snapshotSender = snapshotSender;
        this.accessPolicy = accessPolicy;
    }

    public DebugActionResult perform(ServerPlayer player, DebugAction action) {
        PlayerProfile current = player.getData(PlayerProfiles.PROFILE);
        if (!accessPolicy.canAccess(player)) {
            return new DebugActionResult(DebugActionResult.Code.DEBUG_DISABLED, current);
        }
        if (action == null) {
            return new DebugActionResult(DebugActionResult.Code.INVALID_ACTION, current);
        }

        return switch (action) {
            case CHOOSE_SHINIGAMI -> choose(player, SpiritualPath.SHINIGAMI);
            case CHOOSE_QUINCY -> choose(player, SpiritualPath.QUINCY);
            case RESET_PROFILE -> reset(player, current);
            case ENERGY_ZERO -> setEnergy(player, current, 0);
            case ENERGY_HALF -> setEnergy(player, current, current.maxEnergy() / 2);
            case ENERGY_FULL -> setEnergy(player, current, current.maxEnergy());
            case REFRESH -> refresh(player, current);
        };
    }

    private DebugActionResult choose(ServerPlayer player, SpiritualPath path) {
        ProfileMutationResult result = profileService.choosePath(player, path);
        return new DebugActionResult(map(result.status()), result.profile());
    }

    private DebugActionResult reset(ServerPlayer player, PlayerProfile current) {
        if (current.revision() == Long.MAX_VALUE) {
            return exhausted(current);
        }
        PlayerProfile replacement = new PlayerProfile(
                SpiritualPath.UNCHOSEN,
                PlayerProfile.DEFAULT_MAX_ENERGY,
                PlayerProfile.DEFAULT_MAX_ENERGY,
                0,
                current.revision() + 1L);
        return persist(player, replacement);
    }

    private DebugActionResult setEnergy(ServerPlayer player, PlayerProfile current, int energy) {
        if (current.revision() == Long.MAX_VALUE) {
            return exhausted(current);
        }
        return persist(player, current.withCurrentEnergy(energy, current.revision() + 1L));
    }

    private DebugActionResult refresh(ServerPlayer player, PlayerProfile current) {
        snapshotSender.send(player);
        return new DebugActionResult(DebugActionResult.Code.ACCEPTED, current);
    }

    private DebugActionResult persist(ServerPlayer player, PlayerProfile replacement) {
        player.setData(PlayerProfiles.PROFILE, replacement);
        snapshotSender.send(player);
        return new DebugActionResult(DebugActionResult.Code.ACCEPTED, replacement);
    }

    private static DebugActionResult exhausted(PlayerProfile current) {
        return new DebugActionResult(DebugActionResult.Code.REVISION_EXHAUSTED, current);
    }

    private static DebugActionResult.Code map(ProfileMutationResult.Status status) {
        return switch (status) {
            case ACCEPTED -> DebugActionResult.Code.ACCEPTED;
            case INVALID_PATH -> DebugActionResult.Code.INVALID_PATH;
            case ALREADY_CHOSEN -> DebugActionResult.Code.ALREADY_CHOSEN;
            case REVISION_EXHAUSTED -> DebugActionResult.Code.REVISION_EXHAUSTED;
        };
    }
}
