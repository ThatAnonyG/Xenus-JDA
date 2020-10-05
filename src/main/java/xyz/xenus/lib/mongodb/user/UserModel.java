package xyz.xenus.lib.mongodb.user;

import com.mongodb.client.MongoCollection;
import xyz.xenus.lib.mongodb.Model;

import static com.mongodb.client.model.Filters.eq;

public class UserModel extends Model {
    private String bio = "Not set!";
    private Badges badges = new Badges();
    private Economy economy = new Economy();
    private Reps reps = new Reps();

    public UserModel() {
        super(ModelType.USER);
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

    @Override
    public UserModel save() {
        MongoCollection<UserModel> col = getManager().getDb().getCollection("users", UserModel.class);
        UserModel model = col.findOneAndReplace(eq("model_id", getModelId()), this);
        if (model == null) col.insertOne(this);
        getManager().getUserDbCache().put(this.getModelId(), this);
        return this;
    }
}
