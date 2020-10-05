package xyz.xenus.lib.mongodb;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

public abstract class Model {
    @BsonIgnore
    private final ModelType type;

    @BsonIgnore
    private DBManager manager;
    private ObjectId id;
    @BsonProperty(value = "model_id")
    private String modelId;

    public Model(ModelType type) {
        this.type = type;
    }

    public ModelType getType() {
        return type;
    }

    public DBManager getManager() {
        return manager;
    }

    public void setManager(DBManager manager) {
        this.manager = manager;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public abstract Model save();

    public enum ModelType {
        GUILD,
        MEMBER,
        USER
    }
}
