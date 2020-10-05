package xyz.xenus.commands.config;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import xyz.xenus.lib.command.Command;
import xyz.xenus.lib.command.CommandContext;

// Todo - Complete the command
public class Verify extends Command {
    public Verify() {
        super("verify");
        setCategory(Categories.CONFIG);
        setDescription("Let's you setups a server-wide verification system and allows admins to change settings.");
        setUsage(
                "<setup> - Start the verification system setup!\n" +
                        "<remove> - Remove the verification system from the server!"
        );
        setPerms(new Permission[]{Permission.ADMINISTRATOR});
    }

    @Override
    public void run(@NotNull CommandContext ctx) {
    }
}
