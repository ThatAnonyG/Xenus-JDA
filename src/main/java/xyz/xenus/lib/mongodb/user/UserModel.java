package xyz.xenus.lib.mongodb.user;

import org.bson.types.ObjectId;

public class UserModel {
    private ObjectId id;

    private String uid;
    private String bio = "Not set!";
    private Badges badges = new Badges();
    private Economy economy = new Economy();
    private Reps reps = new Reps();

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Badges getBadges() {
        return badges;
    }

    public void setBadges(Badges badges) {
        this.badges = badges;
    }

    public Economy getEconomy() {
        return economy;
    }

    public void setEconomy(Economy economy) {
        this.economy = economy;
    }

    public Reps getReps() {
        return reps;
    }

    public void setReps(Reps reps) {
        this.reps = reps;
    }
}
