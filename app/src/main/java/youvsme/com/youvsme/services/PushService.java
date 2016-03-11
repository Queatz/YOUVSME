package youvsme.com.youvsme.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonObject;

import youvsme.com.youvsme.R;
import youvsme.com.youvsme.util.Config;

/**
 * Created by jacob on 3/6/16.
 */
public class PushService {

    private static PushService instance;
    public static PushService use() {
        if (instance == null) {
            synchronized (PushService.class) {
                if (instance == null) {
                    instance = new PushService();
                }
            }
        }

        return instance;
    }

    public void notify(JsonObject push) {
        if (!push.has("action")) {
            return;
        }

        String action = push.get("action").getAsString();

        if (action == null) {
            return;
        }

        Notification notification;
        Context context = GameService.use().context();
        String opponentName = push.get("user").getAsString();

        switch (action) {
            case Config.PUSH_NEW_CHALLENGE:
                notification = nn().setContentTitle(context.getString(
                        push.get("isWager").getAsBoolean()
                                ? R.string.they_challenged_you
                                : R.string.they_challenged_you_to_a_wager, opponentName)).build();
                break;
            case Config.PUSH_FINISHED_ANSWERING:
                int resource;

                if (push.has("isComplete") && push.get("isComplete").getAsBoolean()) {
                    resource = R.string.they_finished_see_who_won;
                } else {
                    resource = R.string.they_finished_now_answer_theirs;
                }

                notification = nn()
                        .setContentTitle(context.getString(resource, opponentName))
                        .build();
                break;
            case Config.PUSH_KICK_IN_THE_FACE:
                notification = nn()
                        .setContentTitle(context.getString(R.string.kicked_you, opponentName))
                        .setContentText(context.getString(R.string.this_we_know))
                        .build();
                break;
            default:
                return;
        }

        show(notification);
    }

    private void show(Notification notification) {
        ((NotificationManager) GameService.use().context().getSystemService(Context.NOTIFICATION_SERVICE))
                .notify("YOUVSME", notification.hashCode(), notification);
    }

    private NotificationCompat.Builder nn() {
        return new NotificationCompat.Builder(GameService.use().context())
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_DEFAULT);
    }
}
