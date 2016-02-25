package youvsme.com.youvsme.backend.services;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.User;
import com.sun.istack.internal.Nullable;

import youvsme.com.youvsme.backend.models.UserModel;

/**
 * Created by jacob on 2/25/16.
 */
public class UserService {

    @Nullable
    public UserModel userFromToken(@Nullable String token, @Nullable String facebookToken) {
        if (token != null) {
            UserModel user = ModelService.get(UserModel.class).filter("token", token).first().now();

            if (user != null) {
                return user;
            }
        }

        if (facebookToken != null) {
            UserModel user = ModelService.get(UserModel.class).filter("facebookToken", facebookToken).first().now();

            if (user == null) {
                FacebookClient facebookClient = new DefaultFacebookClient(facebookToken, Version.LATEST);

                User facebookUser = facebookClient.fetchObject("me", User.class);

                if (facebookUser == null) {
                    return null;
                }

                UserModel newUser = ModelService.create(UserModel.class);
                newUser.setFirstName(facebookUser.getFirstName());
                newUser.setLastName(facebookUser.getLastName());
                newUser.setFacebookId(facebookUser.getId());

                if (facebookUser.getPicture() != null) {
                    newUser.setFacebookPictureUrl(facebookUser.getPicture().getUrl());
                }

                ModelService.save(newUser);

                return newUser;
            }
        }

        return null;
    }
}
