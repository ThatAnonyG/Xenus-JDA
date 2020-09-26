package xyz.xenus.lib.mongodb.member;

public class Economy {
  private long xp = 0;
  private long cd = 0;
  private long level = 0;
  private long nxtLvl = 500;

  public long getXp() {
    return xp;
  }

  public void setXp(long xp) {
    this.xp = xp;
  }

  public long getLevel() {
    return level;
  }

  public void setLevel(long level) {
    this.level = level;
  }

  public long getCd() {
    return cd;
  }

  public void setCd(long cd) {
    this.cd = cd;
  }

  public long getNxtLvl() {
    return nxtLvl;
  }

  public void setNxtLvl(long nxtLvl) {
    this.nxtLvl = nxtLvl;
  }
}
