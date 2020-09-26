package xyz.xenus.lib.mongodb.user;

public class Daily {
  private long cd = 0;
  private long streak = 0;

  public long getCd() {
    return cd;
  }

  public void setCd(long cd) {
    this.cd = cd;
  }

  public long getStreak() {
    return streak;
  }

  public void setStreak(long streak) {
    this.streak = streak;
  }
}
