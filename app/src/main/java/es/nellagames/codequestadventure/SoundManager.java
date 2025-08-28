package es.nellagames.codequestadventure;


import android.content.Context;
import android.media.MediaPlayer;
import es.nellagames.codequestadventure.R;

public class SoundManager {

    private MediaPlayer successPlayer, errorPlayer, victoryPlayer;
    private boolean soundEnabled = true;

    public SoundManager(Context context) {
        try {
            successPlayer = MediaPlayer.create(context, R.raw.success);
            errorPlayer = MediaPlayer.create(context, R.raw.error);
            victoryPlayer = MediaPlayer.create(context, R.raw.victory);
        } catch (Exception e) {
            e.printStackTrace();
            // If sound files are not available, continue without sounds
        }
    }

    public void playSuccess() {
        if (soundEnabled && successPlayer != null) {
            try {
                successPlayer.seekTo(0);
                successPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playError() {
        if (soundEnabled && errorPlayer != null) {
            try {
                errorPlayer.seekTo(0);
                errorPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playVictory() {
        if (soundEnabled && victoryPlayer != null) {
            try {
                victoryPlayer.seekTo(0);
                victoryPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void release() {
        try {
            if (successPlayer != null) {
                successPlayer.release();
                successPlayer = null;
            }
            if (errorPlayer != null) {
                errorPlayer.release();
                errorPlayer = null;
            }
            if (victoryPlayer != null) {
                victoryPlayer.release();
                victoryPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
