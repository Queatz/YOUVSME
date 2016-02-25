package youvsme.com.youvsme.backend.models;

/**
 * Created by jacob on 2/25/16.
 */
public class UserModel extends Model {
    public String firstName;
    public String lastName;
    public String facebookId;
    private String facebookPictureUrl;

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
}
