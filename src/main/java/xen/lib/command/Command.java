package xen.lib.command;

import net.dv8tion.jda.api.Permission;

public class Command {
  private final String name;
  private String[] aliases = new String[]{};
  private boolean premium = false;
  private long cd = 0;
  private Categories category = Categories.INFO;
  private String description = "A cool command.";
  private String usage = "Try using the command";
  private Permission[] perms = new Permission[]{};
  private Permission[] botPerms = new Permission[]{};

  public Command(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String[] getAliases() {
    return aliases;
  }

  public void setAliases(String[] aliases) {
    this.aliases = aliases;
  }

  public boolean isPremium() {
    return premium;
  }

  public void setPremium(boolean premium) {
    this.premium = premium;
  }

  public long getCd() {
    return cd;
  }

  public void setCd(long cd) {
    this.cd = cd;
  }

  public Categories getCategory() {
    return category;
  }

  public void setCategory(Categories category) {
    this.category = category;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUsage() {
    return usage;
  }

  public void setUsage(String usage) {
    this.usage = usage;
  }

  public Permission[] getPerms() {
    return perms;
  }

  public void setPerms(Permission[] perms) {
    this.perms = perms;
  }

  public Permission[] getBotPerms() {
    return botPerms;
  }

  public void setBotPerms(Permission[] botPerms) {
    this.botPerms = botPerms;
  }

  public void run(CommandContext ctx) {
  }

  public enum Categories {
    ANIME,
    CONFIG,
    DEV,
    ECONOMY,
    FUN,
    INFO,
    MODERATION,
    MUSIC,
    UTILS
  }
}
