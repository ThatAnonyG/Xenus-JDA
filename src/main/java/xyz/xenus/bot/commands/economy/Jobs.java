package xyz.xenus.bot.commands.economy;

import net.dv8tion.jda.api.EmbedBuilder;
import xyz.xenus.lib.Utils;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;
import xyz.xenus.lib.mongodb.Model;
import xyz.xenus.lib.mongodb.user.Job;

import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;

public class Jobs extends Command {
    public Jobs() {
        super("jobs");
        setCategory(Categories.ECONOMY);
        setCd(5000);
        setDescription("Join a job and see info about your current job.");
        setUsage(
                "<info> - Shows info about your current job\n" +
                        "<join> [Job Name] - Join a job. Use without 'Job Name' to see available jobs"
        );
    }

    @Override
    public void run(CommandContext ctx) {
        if (!ctx.hasSub(new String[]{"info", "join"}, 0)) {
            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getCross() + " Use a proper subcommand - `info` or `join`!",
                    Utils.Embeds.ERROR
            ).queue();
            return;
        }

        if (ctx.hasSub(new String[]{"info"}, 0)) {
            if (ctx.getUserModel().getEconomy().getJob().getName() == null) {
                Utils.sendEm(
                        ctx.getEvent().getChannel(),
                        ctx.getClient().getCross() + " You don't have a job yet!",
                        Utils.Embeds.ERROR
                ).queue();
                return;
            }

            Job job = ctx.getUserModel().getEconomy().getJob();
            EmbedBuilder embed = Utils.embed()
                    .setTitle("Job Info")
                    .setColor(Utils.getHex())
                    .addField(
                            job.getName(),
                            "**Salary:** $" + job.getSalary() + "\n" +
                                    "**Working People:** " + ctx.getClient().getDbManager().findMany(
                                    eq("economy.job.name", job.getName()), Model.ModelType.USER
                            ).size() + "\n" +
                                    "**Last Shift:** " + Utils.formatDate(job.getReady() - 86400000),
                            false
                    );
            ctx.getEvent().getChannel().sendMessage(embed.build()).queue();
        } else {
            String[] jobNames = new String[]{"developer", "chef", "designer", "freelancer", "bartender"};
            int[] jobSalary = new int[]{700, 600, 500, 400, 400};

            if (!ctx.hasSub(jobNames, 1)) {
                EmbedBuilder embed = Utils.embed()
                        .setTitle("Available Jobs")
                        .setDescription("To join a job use `=job join <Name>`");
                for (int i = 0; i < jobNames.length; i++)
                    embed.addField(
                            jobNames[i].substring(0, 1).toUpperCase() + jobNames[i].substring(1),
                            "**Salary:** $" + jobSalary[i] + "\n" +
                                    "**Working People:** " + ctx.getClient().getDbManager().findMany(
                                    eq("economy.job.name", jobNames[i]), Model.ModelType.USER
                            ).size(),
                            false
                    );
                ctx.getEvent().getChannel().sendMessage(embed.build()).queue();
                return;
            }

            String name = ctx.getArgs().get(1).toLowerCase();
            ctx.getUserModel().getEconomy().getJob().setName(name.substring(0, 1).toUpperCase() + name.substring(1));
            ctx.getUserModel().getEconomy().getJob().setSalary(jobSalary[Arrays.asList(jobNames).indexOf(name)]);
            ctx.getUserModel().save();

            Utils.sendEm(
                    ctx.getEvent().getChannel(),
                    ctx.getClient().getTick() + " You successfully joined the job of " +
                            name.substring(0, 1).toUpperCase() + name.substring(1) + "!",
                    Utils.Embeds.SUCCESS
            ).queue();
        }
    }
}
