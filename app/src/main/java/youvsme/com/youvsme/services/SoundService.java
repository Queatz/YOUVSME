package youvsme.com.youvsme.services;

import android.media.MediaPlayer;


/**
 * Created by jacob on 9/24/16.
 */

public class SoundService {
    private static SoundService instance;
    public static SoundService use() {
        if (instance == null) {
            synchronized (SoundService.class) {
                if (instance == null) {
                    instance = new SoundService();
                }
            }
        }

        return instance;
    }

    public void play(int id) {
        MediaPlayer mp = MediaPlayer.create(GameService.use().context(), id);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }
}
