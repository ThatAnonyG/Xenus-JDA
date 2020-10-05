package xyz.xenus.lib.mongodb.member;

import com.mongodb.client.MongoCollection;
import xyz.xenus.lib.mongodb.Model;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public class MemberModel extends Model {
    private int mute = 0;
    private ArrayList<Actions> warns = new ArrayList<>();
    private ArrayList<Actions> reports = new ArrayList<>();
    private ArrayList<CD> cd = new ArrayList<>();
    private Economy economy = new Economy();

    public MemberModel() {
        super(ModelType.MEMBER);
    }

    public int getMute() {
        return mute;
    }

    public void setMute(int mute) {
        this.mute = mute;
    }

    public ArrayList<Actions> getWarns() {
        return warns;
    }

    public void setWarns(ArrayList<Actions> warns) {
        this.warns = warns;
    }

    public ArrayList<Actions> getReports() {
        return reports;
    }

    public void setReports(ArrayList<Actions> reports) {
        this.reports = reports;
    }

    public ArrayList<CD> getCd() {
        return cd;
    }

    public void setCd(ArrayList<CD> cd) {
        this.cd = cd;
    }

    public Economy getEconomy() {
        return economy;
    }

    public void setEconomy(Economy economy) {
        this.economy = economy;
    }

    @Override
    public MemberModel save() {
        MongoCollection<MemberModel> col = getManager().getDb().getCollection("members", MemberModel.class);
        MemberModel model = col.findOneAndReplace(eq("model_id", getModelId()), this);
        if (model == null) col.insertOne(this);
        getManager().getMemberDbCache().put(this.getModelId(), this);
        return this;
    }
}
