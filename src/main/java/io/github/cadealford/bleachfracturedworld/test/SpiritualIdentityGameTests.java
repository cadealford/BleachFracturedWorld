package io.github.cadealford.bleachfracturedworld.test;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.github.cadealford.bleachfracturedworld.BleachFracturedWorld;
import io.github.cadealford.bleachfracturedworld.client.ClientProfileCache;
import io.github.cadealford.bleachfracturedworld.client.debug.ClientDebugState;
import io.github.cadealford.bleachfracturedworld.client.debug.DebugScreenModel;
import io.github.cadealford.bleachfracturedworld.debug.DebugAction;
import io.github.cadealford.bleachfracturedworld.debug.DebugActionResult;
import io.github.cadealford.bleachfracturedworld.debug.DebugProfileService;
import io.github.cadealford.bleachfracturedworld.network.DebugActionResultPayload;
import io.github.cadealford.bleachfracturedworld.network.ProfileSnapshotPayload;
import io.github.cadealford.bleachfracturedworld.player.PlayerProfile;
import io.github.cadealford.bleachfracturedworld.player.PlayerProfiles;
import io.github.cadealford.bleachfracturedworld.player.ProfileMutationResult;
import io.github.cadealford.bleachfracturedworld.player.ProfileService;
import io.github.cadealford.bleachfracturedworld.player.SpiritualPath;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Optional;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BleachFracturedWorld.MODID)
@PrefixGameTestTemplate(false)
public final class SpiritualIdentityGameTests {
    private SpiritualIdentityGameTests() {
    }

    @GameTest(template = "empty")
    public static void newProfileHasSafeDefaults(GameTestHelper helper) {
        PlayerProfile profile = PlayerProfile.unchosen();

        assertProfile(helper, profile, SpiritualPath.UNCHOSEN, 100, 100, 0, 0L);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void invalidProfileBoundsAreRejected(GameTestHelper helper) {
        expectIllegalArgument(helper, () -> new PlayerProfile(SpiritualPath.UNCHOSEN, -1, 100, 0, 0));
        expectIllegalArgument(helper, () -> new PlayerProfile(SpiritualPath.UNCHOSEN, 101, 100, 0, 0));
        expectIllegalArgument(helper, () -> new PlayerProfile(SpiritualPath.UNCHOSEN, 0, 0, 0, 0));
        expectIllegalArgument(helper, () -> new PlayerProfile(SpiritualPath.UNCHOSEN, 100, 100, -1, 0));
        expectIllegalArgument(helper, () -> new PlayerProfile(SpiritualPath.UNCHOSEN, 100, 100, 0, -1));
        expectIllegalArgument(helper, () -> new PlayerProfile(null, 100, 100, 0, 0));
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void codecRejectsInvalidPersistedBounds(GameTestHelper helper) {
        var invalid = JsonParser.parseString("""
                {"path":"unchosen","current_energy":101,"max_energy":100,"mastery":0,"revision":0}
                """);

        helper.assertTrue(PlayerProfile.CODEC.parse(JsonOps.INSTANCE, invalid).result().isEmpty(),
                "Profile codec accepted current energy above maximum energy");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void attachmentMaterializesSafeDefault(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        PlayerProfile profile = player.getData(PlayerProfiles.PROFILE);

        assertProfile(helper, profile, SpiritualPath.UNCHOSEN, 100, 100, 0, 0L);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void firstSelectionPersistsAndSynchronizesOnce(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        var sends = new AtomicInteger();
        var service = new ProfileService(ignored -> sends.incrementAndGet());

        ProfileMutationResult result = service.choosePath(player, SpiritualPath.SHINIGAMI);

        helper.assertValueEqual(result.status(), ProfileMutationResult.Status.ACCEPTED, "selection status");
        assertProfile(helper, result.profile(), SpiritualPath.SHINIGAMI, 100, 100, 0, 1L);
        assertProfile(helper, player.getData(PlayerProfiles.PROFILE), SpiritualPath.SHINIGAMI, 100, 100, 0, 1L);
        helper.assertValueEqual(sends.get(), 1, "snapshot send count");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void repeatSelectionRejectsWithoutMutationOrSync(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        var existing = new PlayerProfile(SpiritualPath.QUINCY, 40, 100, 3, 8L);
        player.setData(PlayerProfiles.PROFILE, existing);
        var sends = new AtomicInteger();
        var service = new ProfileService(ignored -> sends.incrementAndGet());

        ProfileMutationResult result = service.choosePath(player, SpiritualPath.SHINIGAMI);

        helper.assertValueEqual(result.status(), ProfileMutationResult.Status.ALREADY_CHOSEN, "selection status");
        helper.assertValueEqual(player.getData(PlayerProfiles.PROFILE), existing, "stored profile");
        helper.assertValueEqual(sends.get(), 0, "snapshot send count");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void unchosenSelectionRejects(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        var sends = new AtomicInteger();
        var service = new ProfileService(ignored -> sends.incrementAndGet());

        ProfileMutationResult result = service.choosePath(player, SpiritualPath.UNCHOSEN);

        helper.assertValueEqual(result.status(), ProfileMutationResult.Status.INVALID_PATH, "selection status");
        assertProfile(helper, player.getData(PlayerProfiles.PROFILE), SpiritualPath.UNCHOSEN, 100, 100, 0, 0L);
        helper.assertValueEqual(sends.get(), 0, "snapshot send count");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void revisionOverflowRejects(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        var existing = new PlayerProfile(SpiritualPath.UNCHOSEN, 100, 100, 0, Long.MAX_VALUE);
        player.setData(PlayerProfiles.PROFILE, existing);
        var sends = new AtomicInteger();
        var service = new ProfileService(ignored -> sends.incrementAndGet());

        ProfileMutationResult result = service.choosePath(player, SpiritualPath.QUINCY);

        helper.assertValueEqual(result.status(), ProfileMutationResult.Status.REVISION_EXHAUSTED, "selection status");
        helper.assertValueEqual(player.getData(PlayerProfiles.PROFILE), existing, "stored profile");
        helper.assertValueEqual(sends.get(), 0, "snapshot send count");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void snapshotCopiesEveryPresentationField(GameTestHelper helper) {
        ProfileSnapshotPayload snapshot = ProfileSnapshotPayload.from(
                new PlayerProfile(SpiritualPath.QUINCY, 37, 120, 9, 14L));

        helper.assertValueEqual(snapshot.path(), SpiritualPath.QUINCY, "snapshot path");
        helper.assertValueEqual(snapshot.currentEnergy(), 37, "snapshot current energy");
        helper.assertValueEqual(snapshot.maxEnergy(), 120, "snapshot maximum energy");
        helper.assertValueEqual(snapshot.mastery(), 9, "snapshot mastery");
        helper.assertValueEqual(snapshot.revision(), 14L, "snapshot revision");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cacheRejectsOlderRevision(GameTestHelper helper) {
        ClientProfileCache.clear();
        ClientProfileCache.accept(new ProfileSnapshotPayload(SpiritualPath.SHINIGAMI, 80, 100, 2, 7L));

        boolean accepted = ClientProfileCache.accept(
                new ProfileSnapshotPayload(SpiritualPath.QUINCY, 10, 100, 1, 6L));

        helper.assertFalse(accepted, "Cache accepted an older snapshot");
        helper.assertValueEqual(ClientProfileCache.snapshot().orElseThrow().path(), SpiritualPath.SHINIGAMI,
                "cached path");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cacheAcceptsEqualOrNewerRevision(GameTestHelper helper) {
        ClientProfileCache.clear();
        ClientProfileCache.accept(new ProfileSnapshotPayload(SpiritualPath.SHINIGAMI, 80, 100, 2, 7L));

        boolean equalAccepted = ClientProfileCache.accept(
                new ProfileSnapshotPayload(SpiritualPath.SHINIGAMI, 60, 100, 2, 7L));
        boolean newerAccepted = ClientProfileCache.accept(
                new ProfileSnapshotPayload(SpiritualPath.SHINIGAMI, 40, 100, 2, 8L));

        helper.assertTrue(equalAccepted, "Cache rejected an equal-revision snapshot");
        helper.assertTrue(newerAccepted, "Cache rejected a newer snapshot");
        helper.assertValueEqual(ClientProfileCache.snapshot().orElseThrow().currentEnergy(), 40,
                "cached current energy");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void cacheClearRemovesSnapshot(GameTestHelper helper) {
        ClientProfileCache.accept(new ProfileSnapshotPayload(SpiritualPath.QUINCY, 50, 100, 0, 2L));

        ClientProfileCache.clear();

        helper.assertTrue(ClientProfileCache.snapshot().isEmpty(), "Cache retained a snapshot after clear");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void disabledDebugAccessRejectsWithoutMutationOrSync(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        var sends = new AtomicInteger();
        var sender = (io.github.cadealford.bleachfracturedworld.player.ProfileSnapshotSender)
                ignored -> sends.incrementAndGet();
        var service = new DebugProfileService(new ProfileService(sender), sender, ignored -> false);
        PlayerProfile before = player.getData(PlayerProfiles.PROFILE);

        DebugActionResult result = service.perform(player, DebugAction.RESET_PROFILE);

        helper.assertValueEqual(result.code(), DebugActionResult.Code.DEBUG_DISABLED, "debug result");
        helper.assertValueEqual(player.getData(PlayerProfiles.PROFILE), before, "stored profile");
        helper.assertValueEqual(sends.get(), 0, "snapshot send count");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void debugResetRestoresDefaultsAndAdvancesRevision(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        player.setData(PlayerProfiles.PROFILE, new PlayerProfile(SpiritualPath.SHINIGAMI, 7, 100, 4, 12L));
        var sends = new AtomicInteger();
        var sender = (io.github.cadealford.bleachfracturedworld.player.ProfileSnapshotSender)
                ignored -> sends.incrementAndGet();
        var service = new DebugProfileService(new ProfileService(sender), sender, ignored -> true);

        DebugActionResult result = service.perform(player, DebugAction.RESET_PROFILE);

        helper.assertValueEqual(result.code(), DebugActionResult.Code.ACCEPTED, "debug result");
        assertProfile(helper, result.profile(), SpiritualPath.UNCHOSEN, 100, 100, 0, 13L);
        helper.assertValueEqual(sends.get(), 1, "snapshot send count");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void energyPresetsPreservePathMaxAndMastery(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        player.setData(PlayerProfiles.PROFILE, new PlayerProfile(SpiritualPath.QUINCY, 75, 101, 6, 3L));
        var sends = new AtomicInteger();
        var sender = (io.github.cadealford.bleachfracturedworld.player.ProfileSnapshotSender)
                ignored -> sends.incrementAndGet();
        var service = new DebugProfileService(new ProfileService(sender), sender, ignored -> true);

        service.perform(player, DebugAction.ENERGY_ZERO);
        assertProfile(helper, player.getData(PlayerProfiles.PROFILE), SpiritualPath.QUINCY, 0, 101, 6, 4L);
        service.perform(player, DebugAction.ENERGY_HALF);
        assertProfile(helper, player.getData(PlayerProfiles.PROFILE), SpiritualPath.QUINCY, 50, 101, 6, 5L);
        service.perform(player, DebugAction.ENERGY_FULL);
        assertProfile(helper, player.getData(PlayerProfiles.PROFILE), SpiritualPath.QUINCY, 101, 101, 6, 6L);
        helper.assertValueEqual(sends.get(), 3, "snapshot send count");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void refreshSynchronizesWithoutAdvancingRevision(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        var existing = new PlayerProfile(SpiritualPath.SHINIGAMI, 33, 100, 1, 5L);
        player.setData(PlayerProfiles.PROFILE, existing);
        var sends = new AtomicInteger();
        var sender = (io.github.cadealford.bleachfracturedworld.player.ProfileSnapshotSender)
                ignored -> sends.incrementAndGet();
        var service = new DebugProfileService(new ProfileService(sender), sender, ignored -> true);

        DebugActionResult result = service.perform(player, DebugAction.REFRESH);

        helper.assertValueEqual(result.code(), DebugActionResult.Code.ACCEPTED, "debug result");
        helper.assertValueEqual(result.profile(), existing, "result profile");
        helper.assertValueEqual(sends.get(), 1, "snapshot send count");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void debugSelectionUsesOrdinaryDuplicateRule(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        player.setData(PlayerProfiles.PROFILE, new PlayerProfile(SpiritualPath.SHINIGAMI, 100, 100, 0, 1L));
        var sends = new AtomicInteger();
        var sender = (io.github.cadealford.bleachfracturedworld.player.ProfileSnapshotSender)
                ignored -> sends.incrementAndGet();
        var service = new DebugProfileService(new ProfileService(sender), sender, ignored -> true);

        DebugActionResult result = service.perform(player, DebugAction.CHOOSE_QUINCY);

        helper.assertValueEqual(result.code(), DebugActionResult.Code.ALREADY_CHOSEN, "debug result");
        helper.assertValueEqual(sends.get(), 0, "snapshot send count");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void debugMutationRevisionOverflowRejects(GameTestHelper helper) {
        var player = helper.makeMockServerPlayerInLevel();
        var existing = new PlayerProfile(SpiritualPath.QUINCY, 100, 100, 0, Long.MAX_VALUE);
        player.setData(PlayerProfiles.PROFILE, existing);
        var sends = new AtomicInteger();
        var sender = (io.github.cadealford.bleachfracturedworld.player.ProfileSnapshotSender)
                ignored -> sends.incrementAndGet();
        var service = new DebugProfileService(new ProfileService(sender), sender, ignored -> true);

        DebugActionResult result = service.perform(player, DebugAction.ENERGY_ZERO);

        helper.assertValueEqual(result.code(), DebugActionResult.Code.REVISION_EXHAUSTED, "debug result");
        helper.assertValueEqual(player.getData(PlayerProfiles.PROFILE), existing, "stored profile");
        helper.assertValueEqual(sends.get(), 0, "snapshot send count");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void unknownDebugActionIdRejects(GameTestHelper helper) {
        helper.assertTrue(DebugAction.fromId(999).isEmpty(), "Unknown debug action ID was accepted");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void debugClientStateStoresLatestResult(GameTestHelper helper) {
        ClientDebugState.clear();
        var payload = new DebugActionResultPayload(
                DebugActionResult.Code.ACCEPTED,
                new ProfileSnapshotPayload(SpiritualPath.QUINCY, 50, 100, 2, 4L));

        ClientDebugState.accept(payload);

        helper.assertValueEqual(ClientDebugState.latest().orElseThrow(), payload, "latest debug result");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void debugClientStateClearsOnDisconnect(GameTestHelper helper) {
        ClientDebugState.accept(new DebugActionResultPayload(
                DebugActionResult.Code.DEBUG_DISABLED,
                new ProfileSnapshotPayload(SpiritualPath.UNCHOSEN, 100, 100, 0, 0L)));

        ClientDebugState.clear();

        helper.assertTrue(ClientDebugState.latest().isEmpty(), "Debug state survived disconnect clear");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void debugScreenModelDisablesActionsWithoutSnapshot(GameTestHelper helper) {
        DebugScreenModel model = DebugScreenModel.from(true, Optional.empty(), Optional.empty());

        helper.assertTrue(model.connected(), "Model did not retain connection state");
        helper.assertFalse(model.hasSnapshot(), "Model fabricated a snapshot");
        helper.assertFalse(model.mutationsEnabled(), "Model enabled mutations without a snapshot");
        helper.assertValueEqual(model.path(), "Unavailable", "path presentation");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void debugScreenModelExposesAuthoritativeProfile(GameTestHelper helper) {
        var snapshot = new ProfileSnapshotPayload(SpiritualPath.SHINIGAMI, 45, 100, 3, 9L);
        var result = new DebugActionResultPayload(DebugActionResult.Code.ACCEPTED, snapshot);

        DebugScreenModel model = DebugScreenModel.from(true, Optional.of(snapshot), Optional.of(result));

        helper.assertTrue(model.mutationsEnabled(), "Model disabled connected authoritative actions");
        helper.assertValueEqual(model.path(), "Shinigami", "path presentation");
        helper.assertValueEqual(model.energy(), "45 / 100", "energy presentation");
        helper.assertValueEqual(model.mastery(), "3", "mastery presentation");
        helper.assertValueEqual(model.revision(), "9", "revision presentation");
        helper.assertValueEqual(model.lastResult(), "Accepted", "result presentation");
        helper.succeed();
    }

    private static void expectIllegalArgument(GameTestHelper helper, Runnable action) {
        try {
            action.run();
            helper.fail("Expected invalid profile construction to fail");
        } catch (IllegalArgumentException expected) {
            // Expected validation failure.
        }
    }

    private static void assertProfile(
            GameTestHelper helper,
            PlayerProfile profile,
            SpiritualPath path,
            int currentEnergy,
            int maxEnergy,
            int mastery,
            long revision) {
        helper.assertValueEqual(profile.path(), path, "path");
        helper.assertValueEqual(profile.currentEnergy(), currentEnergy, "current energy");
        helper.assertValueEqual(profile.maxEnergy(), maxEnergy, "maximum energy");
        helper.assertValueEqual(profile.mastery(), mastery, "mastery");
        helper.assertValueEqual(profile.revision(), revision, "revision");
    }
}
