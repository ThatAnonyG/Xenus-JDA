package xyz.xenus.lib.mongodb.user;

import java.util.ArrayList;

public class Reps {
    private ArrayList<String> pos = new ArrayList<>();
    private ArrayList<String> neg = new ArrayList<>();
    private long cd = 0;

    public ArrayList<String> getPos() {
        return pos;
    }

    public void setPos(ArrayList<String> pos) {
        this.pos = pos;
    }

    public ArrayList<String> getNeg() {
        return neg;
    }

    public void setNeg(ArrayList<String> neg) {
        this.neg = neg;
    }

    public long getCd() {
        return cd;
    }

    public void setCd(long cd) {
        this.cd = cd;
    }
}
