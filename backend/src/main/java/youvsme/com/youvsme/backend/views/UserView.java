package youvsme.com.youvsme.backend.views;

import youvsme.com.youvsme.backend.models.UserModel;

/**
 * Created by jacob on 2/28/16.
 */
public class UserView {
    public String id;
    public String firstName;
    public String lastName;
    public String token;
    public String pictureUrl;
    public String gender;

    public UserView(final UserModel user) {
        this(user, false);
    }

    public UserView(final UserModel user, boolean includesToken) {
        id = user.getId();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        pictureUrl = user.getPictureUrl();
        gender = user.getGender();

        if (includesToken) {
            token = user.getToken();
        }
    }
}
