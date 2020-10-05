package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

public class WelcomeText extends Command {
    public WelcomeText() {
        super("welcomeText");
        setCategory(Categories.CONFIG);
        setDescription("Let's you set a welcome message for new users.");
        setUsage("<Message>");
        setPerms(new Permission[]{Permission.ADMINISTRATOR});
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
        if (ctx.getArgs().isEmpty()) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " The message can't be empty!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        String message = String.join(" ", ctx.getArgs());
        ctx.getGuildModel().getWelcome().setMessage(message);
        ctx.getGuildModel().save();

        Utils.sendEm(
                ctx.getEvent().getChannel(),
                ctx.getClient().getTick() + " Welcome message set to:\n\n" +
                        ctx.getGuildModel().getWelcome().getMessage(),
                Utils.Embeds.SUCCESS
        ).queue();
        Utils.sendConfigLog(
                ctx.getEvent(),
                ctx.getGuildModel(),
                "Changed Welcome Message",
                "Welcome message changed to\n\n" + ctx.getGuildModel().getWelcome().getMessage()
        );
    }
}
