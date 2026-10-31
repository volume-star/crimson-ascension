package com.viyella.crimsonascension.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.viyella.crimsonascension.cultivation.CultivationRules;
import com.viyella.crimsonascension.cultivation.CultivationState;
import com.viyella.crimsonascension.cultivation.ModAttachments;
import com.viyella.crimsonascension.entity.MonsterRealmService;
import com.viyella.crimsonascension.menu.BloodVaultMenu;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class CrimsonCommands {
    private static final int PERMISSION_LEVEL = 2;
    private static final SuggestionProvider<CommandSourceStack> FIELD_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(
                    Arrays.stream(StatField.values()).map(StatField::id).toList(),
                    builder
            );

    private CrimsonCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        var cultivation = Commands.literal("cultivation")
                .then(Commands.literal("get")
                        .executes(context -> get(context, List.of(self(context))))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> get(context, EntityArgument.getPlayers(context, "targets")))))
                .then(Commands.literal("set")
                        .then(fieldArgument()
                                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                        .executes(context -> modify(context, List.of(self(context)), false))
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .executes(context -> modify(
                                                        context,
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        false
                                                ))))))
                .then(Commands.literal("add")
                        .then(fieldArgument()
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(context -> modify(context, List.of(self(context)), true))
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .executes(context -> modify(
                                                        context,
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        true
                                                ))))))
                .then(Commands.literal("reset")
                        .executes(context -> reset(context, List.of(self(context))))
                        .then(Commands.argument("targets", EntityArgument.players())
                                .executes(context -> reset(context, EntityArgument.getPlayers(context, "targets")))));

        var monster = Commands.literal("monster")
                .then(Commands.literal("realm")
                        .then(Commands.literal("get")
                                .then(Commands.argument("targets", EntityArgument.entities())
                                        .executes(context -> getMonsterRealm(
                                                context,
                                                EntityArgument.getEntities(context, "targets")
                                        ))))
                        .then(Commands.literal("set")
                                .then(Commands.argument(
                                                "realm",
                                                IntegerArgumentType.integer(0, CultivationRules.MAX_REALM)
                                        )
                                        .then(Commands.argument("targets", EntityArgument.entities())
                                                .executes(context -> setMonsterRealm(
                                                        context,
                                                        EntityArgument.getEntities(context, "targets")
                                                ))))));

        dispatcher.register(Commands.literal("crimson")
                .requires(source -> source.hasPermission(PERMISSION_LEVEL))
                .then(Commands.literal("vault").executes(CrimsonCommands::openVault))
                .then(cultivation)
                .then(monster));
    }

    private static int openVault(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        BloodVaultMenu.openFor(self(context));
        return 1;
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, String> fieldArgument() {
        return Commands.argument("field", StringArgumentType.word()).suggests(FIELD_SUGGESTIONS);
    }

    private static ServerPlayer self(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return context.getSource().getPlayerOrException();
    }

    private static int get(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets) {
        for (ServerPlayer target : targets) {
            CultivationState state = ModAttachments.get(target);
            context.getSource().sendSuccess(() -> Component.translatable(
                    "commands.crimson_ascension.cultivation.get",
                    target.getDisplayName(),
                    state.getBloodQi(),
                    state.getRefinedBlood(),
                    Component.translatable("crimson_ascension.realm." + state.getRealm()),
                    state.getCorruption()
            ), false);
        }
        return targets.size();
    }

    private static int modify(
            CommandContext<CommandSourceStack> context,
            Collection<ServerPlayer> targets,
            boolean add
    ) throws CommandSyntaxException {
        StatField field = parseField(context);
        int value = IntegerArgumentType.getInteger(context, "value");

        for (ServerPlayer target : targets) {
            CultivationState state = ModAttachments.get(target);
            if (add) {
                field.add(state, value);
            } else {
                field.set(state, value);
            }
            ModAttachments.sync(target);

            int updated = field.get(state);
            if (add) {
                context.getSource().sendSuccess(() -> Component.translatable(
                        "commands.crimson_ascension.cultivation.added",
                        value,
                        target.getDisplayName(),
                        field.label(),
                        updated
                ), false);
            } else {
                context.getSource().sendSuccess(() -> Component.translatable(
                        "commands.crimson_ascension.cultivation.set",
                        target.getDisplayName(),
                        field.label(),
                        value,
                        updated
                ), false);
            }
        }
        return targets.size();
    }

    private static int reset(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets) {
        for (ServerPlayer target : targets) {
            ModAttachments.get(target).reset();
            ModAttachments.sync(target);
            context.getSource().sendSuccess(() -> Component.translatable(
                    "commands.crimson_ascension.cultivation.reset",
                    target.getDisplayName()
            ), false);
        }
        return targets.size();
    }

    private static int getMonsterRealm(
            CommandContext<CommandSourceStack> context,
            Collection<? extends Entity> targets
    ) {
        int matched = 0;
        for (Entity target : targets) {
            if (!(target instanceof Mob mob) || !(target instanceof Enemy)) {
                continue;
            }
            int realm = MonsterRealmService.getRealm(mob);
            context.getSource().sendSuccess(() -> Component.translatable(
                    "commands.crimson_ascension.monster.realm.get",
                    target.getDisplayName(),
                    Component.translatable("crimson_ascension.realm." + realm)
            ), false);
            matched++;
        }
        if (matched == 0) {
            context.getSource().sendFailure(Component.translatable(
                    "commands.crimson_ascension.monster.no_targets"
            ));
        }
        return matched;
    }

    private static int setMonsterRealm(
            CommandContext<CommandSourceStack> context,
            Collection<? extends Entity> targets
    ) {
        int requestedRealm = IntegerArgumentType.getInteger(context, "realm");
        int matched = 0;
        for (Entity target : targets) {
            if (!(target instanceof Mob mob) || !(target instanceof Enemy)) {
                continue;
            }
            int realm = MonsterRealmService.setRealm(mob, requestedRealm);
            context.getSource().sendSuccess(() -> Component.translatable(
                    "commands.crimson_ascension.monster.realm.set",
                    target.getDisplayName(),
                    Component.translatable("crimson_ascension.realm." + realm)
            ), false);
            matched++;
        }
        if (matched == 0) {
            context.getSource().sendFailure(Component.translatable(
                    "commands.crimson_ascension.monster.no_targets"
            ));
        }
        return matched;
    }

    private static StatField parseField(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String id = StringArgumentType.getString(context, "field");
        return Arrays.stream(StatField.values())
                .filter(field -> field.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new SimpleCommandExceptionType(Component.translatable(
                        "commands.crimson_ascension.cultivation.unknown_field",
                        id
                )).create());
    }

    private enum StatField {
        BLOOD_QI("blood_qi", "commands.crimson_ascension.cultivation.field.blood_qi"),
        REFINED_BLOOD("refined_blood", "commands.crimson_ascension.cultivation.field.refined_blood"),
        REALM("realm", "commands.crimson_ascension.cultivation.field.realm"),
        CORRUPTION("corruption", "commands.crimson_ascension.cultivation.field.corruption");

        private final String id;
        private final String translationKey;

        StatField(String id, String translationKey) {
            this.id = id;
            this.translationKey = translationKey;
        }

        public String id() {
            return id;
        }

        public Component label() {
            return Component.translatable(translationKey);
        }

        public int get(CultivationState state) {
            return switch (this) {
                case BLOOD_QI -> state.getBloodQi();
                case REFINED_BLOOD -> state.getRefinedBlood();
                case REALM -> state.getRealm();
                case CORRUPTION -> state.getCorruption();
            };
        }

        public void set(CultivationState state, int value) {
            switch (this) {
                case BLOOD_QI -> state.setBloodQi(value);
                case REFINED_BLOOD -> state.setRefinedBlood(value);
                case REALM -> state.setRealm(value);
                case CORRUPTION -> state.setCorruption(value);
            }
        }

        public void add(CultivationState state, int value) {
            switch (this) {
                case BLOOD_QI -> state.addBloodQi(value);
                case REFINED_BLOOD -> state.addRefinedBlood(value);
                case REALM -> state.addRealm(value);
                case CORRUPTION -> state.addCorruption(value);
            }
        }
    }
}
