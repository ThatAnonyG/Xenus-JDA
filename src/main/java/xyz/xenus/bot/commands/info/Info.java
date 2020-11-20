package xyz.xenus.bot.commands.info;

import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

public class Info extends Command {
    public Info() {
        super("info");
        setCd(5000);
        setDescription("Info about the bot and important links.");
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
        EmbedBuilder embed = new EmbedBuilder()
                .setAuthor(
                        ctx.getEvent().getJDA().getSelfUser().getName(),
                        "https://xenus.xyz",
                        ctx.getEvent().getJDA().getSelfUser().getAvatarUrl()
                )
                .setColor(Utils.getHex())
                .setImage("https://imgur.com/Vpi7ReD.png")
                .addField("Version", "20w47a-SNAPSHOT", true)
                .addField(
                        "Founder",
                        "[ThatAnonymousG](https://github.com/ThatAnonymousG)",
                        true
                )
                .addField(
                        "Library",
                        "[JDA](https://ci.dv8tion.net/job/JDA/javadoc/index.html)",
                        true
                )
                .addField(
                        "Current Users",
                        String.valueOf(ctx.getEvent().getJDA().getUsers().size()),
                        true
                )
                .addField(
                        "Current Guilds",
                        String.valueOf(ctx.getEvent().getJDA().getGuilds().size()),
                        true
                )
                .addField("Website", "[xenus.xyz](https://xenus.xyz)", true)
                .addField("Support Server", "[Join Now](https://xenus.xyz/discord)", true)
                .addField("Invite Bot", "[Click Here](https://xenus.xyz/invite)", true)
                .addField("Donate", "[PayPal](https://xenus.xyz/donate)", true)
                .setFooter("Uptime: " +
                        Utils.formatTime(
                                System.currentTimeMillis() - ctx.getClient().getStartTime()
                        )
                );

        ctx.getEvent().getChannel().sendMessage(embed.build()).queue();
    }
}
