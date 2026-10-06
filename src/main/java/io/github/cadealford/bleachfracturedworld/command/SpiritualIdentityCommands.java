package io.github.cadealford.bleachfracturedworld.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.cadealford.bleachfracturedworld.player.BwfServices;
import io.github.cadealford.bleachfracturedworld.player.ProfileMutationResult;
import io.github.cadealford.bleachfracturedworld.player.SpiritualPath;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class SpiritualIdentityCommands {
    private SpiritualIdentityCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("bwf")
                .then(Commands.literal("choose")
                        .then(Commands.literal("shinigami")
                                .executes(context -> choose(context, SpiritualPath.SHINIGAMI)))
                        .then(Commands.literal("quincy")
                                .executes(context -> choose(context, SpiritualPath.QUINCY)))));
    }

    private static int choose(CommandContext<CommandSourceStack> context, SpiritualPath path)
            throws CommandSyntaxException {
        var source = context.getSource();
        var result = BwfServices.PROFILES.choosePath(source.getPlayerOrException(), path);
        Component message = Component.translatable(messageKey(result.status()),
                result.profile().path().serializedName());

        if (result.status() == ProfileMutationResult.Status.ACCEPTED) {
            source.sendSuccess(() -> message, false);
            return 1;
        }

        source.sendFailure(message);
        return 0;
    }

    private static String messageKey(ProfileMutationResult.Status status) {
        return switch (status) {
            case ACCEPTED -> "command.bleachfracturedworld.choose.accepted";
            case INVALID_PATH -> "command.bleachfracturedworld.choose.invalid";
            case ALREADY_CHOSEN -> "command.bleachfracturedworld.choose.already_chosen";
            case REVISION_EXHAUSTED -> "command.bleachfracturedworld.choose.revision_exhausted";
        };
    }
}
