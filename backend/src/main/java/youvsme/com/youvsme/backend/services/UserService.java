package youvsme.com.youvsme.backend.services;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.User;

import javax.annotation.Nullable;

import youvsme.com.youvsme.backend.models.UserModel;

/**
 * Created by jacob on 2/25/16.
 */
public class UserService {

    @Nullable
    public UserModel userFromToken(@Nullable String token, @Nullable String facebookToken) {
        if (token != null) {
            UserModel user = ModelService.get(UserModel.class)
                    .filter("token", token).first().now();

            if (user != null) {
                return user;
            }
        }

        if (facebookToken != null) {
            UserModel user = ModelService.get(UserModel.class)
                    .filter("facebookToken", facebookToken).first().now();

            if (user != null) {
                return user;
            } else {
                FacebookClient facebookClient = new DefaultFacebookClient(facebookToken, Version.LATEST);

                User facebookUser = facebookClient.fetchObject("me", User.class,
                        Parameter.with("fields", "id,first_name,last_name,picture.width(512).height(512)"));

                if (facebookUser == null) {
                    return null;
                }

                UserModel newUser = userFromFacebookUser(facebookUser);
                newUser.setFacebookToken(facebookToken);
                ModelService.save(newUser);

                return newUser;
            }
        }

        return null;
    }

    public UserModel userFromFacebookUser(User facebookUser) {
        // Try to find existing facebook user without token
        // (usually created from a friend of someone else)
        UserModel newUser = ModelService.get(UserModel.class)
                .filter("facebookId", facebookUser.getId()).first().now();

        if (newUser == null) {
            newUser = ModelService.create(UserModel.class);
            newUser.setFacebookId(facebookUser.getId());
        }

        newUser.setFirstName(facebookUser.getFirstName());
        newUser.setLastName(facebookUser.getLastName());

        if (facebookUser.getPicture() != null) {
            newUser.setFacebookPictureUrl(facebookUser.getPicture().getUrl());
        }

        return newUser;
    }
}
