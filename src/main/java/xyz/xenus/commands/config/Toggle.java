package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.guild.Logs;

import java.util.Arrays;
import java.util.Optional;

public class Toggle extends Command {
    public Toggle() {
        super("toggle");
        setCategory(Categories.CONFIG);
        setDescription("Toggle bot categories and moderation logs.");
        setUsage("<cat | logs> <Feature Name>");
        setPerms(new Permission[]{Permission.ADMINISTRATOR});
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
        if (!ctx.hasSub(new String[]{"cat", "logs"}, 0)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() +
                            " Please specify the type - `cat` for Category `logs` for Moderation Logs!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        String sub = ctx.getArgs().remove(0);
        if (ctx.getArgs().isEmpty()) ctx.getArgs().add(0, "none");

        switch (sub.toLowerCase()) {
            case "cat" -> {
                Optional<Categories> optionalCategory = Arrays.stream(Categories.values()).filter(
                        (x) -> x.name().toLowerCase().equals(ctx.getArgs().get(0).toLowerCase())
                ).findFirst();
                if (optionalCategory.isEmpty()) {
                    Utils.sendEm(
                            ctx.getEvent().getChannel(),
                            ctx.getClient().getCross() +
                                    " Please specify a proper category:\n\n" +
                                    String.join(
                                            " | ",
                                            Arrays.stream(Categories.values()).map(
                                                    (x) -> "`" + x.name().toLowerCase() + "`"
                                            ).toArray(String[]::new)
                                    ),
                            Utils.Embeds.ERROR
                    ).queue();
                    return;
                }
                Categories selected = optionalCategory.get();

                String msg;
                if (ctx.getGuildModel().getEnabled().contains(selected)) {
                    ctx.getGuildModel().getEnabled().remove(selected);
                    msg = "Disabled the `" + selected.name().toLowerCase() + "` category!";
                } else {
                    ctx.getGuildModel().getEnabled().add(selected);
                    msg = "Enabled the `" + selected.name().toLowerCase() + "` category!";
                }

                ctx.getGuildModel().save();
                Utils.sendEm(
                        ctx.getEvent().getChannel(),
                        ctx.getClient().getTick() + " " + msg,
                        Utils.Embeds.SUCCESS
                ).queue();
                Utils.sendConfigLog(ctx.getEvent(), ctx.getGuildModel(), "Toggled Command Category", msg);
            }

            case "logs" -> {
                Optional<Logs.LogTypes> optionalType = Arrays.stream(Logs.LogTypes.values()).filter(
                        (x) -> x.name().toLowerCase().equals(ctx.getArgs().get(0).toLowerCase())
                ).findFirst();

                if (optionalType.isEmpty()) {
                    Utils.sendEm(
                            ctx.getEvent().getChannel(),
                            ctx.getClient().getCross() +
                                    " Please specify a proper log type:\n\n" +
                                    String.join(
                                            " | ",
                                            Arrays.stream(Logs.LogTypes.values()).map(
                                                    (x) -> "`" + x.name().toLowerCase() + "`"
                                            ).toArray(String[]::new)
                                    ),
                            Utils.Embeds.ERROR
                    ).queue();
                    return;
                }

                Logs.LogTypes selected = optionalType.get();
                String msg;
                if (
                        ctx.getGuildModel().getLogs().containsKey(selected.name()) &&
                                ctx.getGuildModel().getLogs().get(selected.name()).isEnabled()
                ) {
                    ctx.getGuildModel().getLogs().get(selected.name()).setEnabled(false);
                    msg = "Disabled the `" + selected.name().toLowerCase() + "` moderation log!";
                } else {
                    Logs newLog = new Logs();
                    newLog.setType(selected);
                    newLog.setEnabled(true);
                    ctx.getGuildModel().getLogs().put(selected.name(), newLog);
                    msg = "Enabled the `" + selected.name().toLowerCase() + "` moderation log!";
                }

                ctx.getGuildModel().save();
                Utils.sendEm(
                        ctx.getEvent().getChannel(),
                        ctx.getClient().getTick() + " " + msg,
                        Utils.Embeds.SUCCESS
                ).queue();
                Utils.sendConfigLog(ctx.getEvent(), ctx.getGuildModel(), "Toggled Moderation Logs", msg);
            }
        }
    }
}
