package xyz.xenus.commands.economy;

import xyz.xenus.lib.command.Command;

public class Jobs extends Command {
    public Jobs() {
        super("jobs");
        setCategory(Categories.ECONOMY);
        setCd(5000);
        setDescription("Join a job and see info about your current job.");
        setUsage(
                "<info> - Shows info about your current job\n" +
                        "<join> <Job Name> - Join a job. Use without 'Job Name' to see available jobs"
        );
    }
}
