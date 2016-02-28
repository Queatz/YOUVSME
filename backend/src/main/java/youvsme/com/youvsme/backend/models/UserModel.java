package youvsme.com.youvsme.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by jacob on 2/25/16.
 */

@Entity
public class UserModel extends Model {
    @Index public String firstName;
    @Index public String lastName;
    @Index public String facebookId;
    @Index public String facebookToken;
    public String facebookPictureUrl;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setFacebookPictureUrl(String facebookPictureUrl) {
        this.facebookPictureUrl = facebookPictureUrl;
    }

    public String getFacebookPictureUrl() {
        return facebookPictureUrl;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }
}
