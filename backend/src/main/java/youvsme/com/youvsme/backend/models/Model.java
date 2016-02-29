package youvsme.com.youvsme.backend.models;

import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;

import java.util.Date;

/**
 * Created by jacob on 2/25/16.
 */
public class Model {
    public @Id String id;
    public @Ignore String localId;
    public Date created;

    @Override
    public boolean equals(Object other) {
        return other.getClass() == getClass() && id != null && id.equals(((Model) other).id);
    }

    @Override
    public int hashCode() {
        return id == null ? 1234567890 : id.hashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
