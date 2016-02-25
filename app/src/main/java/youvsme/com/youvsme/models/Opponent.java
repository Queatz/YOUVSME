package youvsme.com.youvsme.models;

/**
 * Created by jacob on 2/23/16.
 */
public class Opponent {
    final String facebookId;
    final String firstName;
    final String lastName;

    public Opponent(String facebookId, String firstName, String lastName) {
        this.facebookId = facebookId;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
