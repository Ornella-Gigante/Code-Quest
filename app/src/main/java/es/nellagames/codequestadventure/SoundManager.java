package es.nellagames.codequestadventure;


import android.content.Context;
import android.media.MediaPlayer;
import es.nellagames.codequestadventure.R;
public class SoundManager {

    private MediaPlayer successPlayer, errorPlayer, victoryPlayer, gameOverPlayer;
    private boolean soundEnabled = true;
    private Context context;

    public SoundManager(Context context) {
        this.context = context;
        initializeSounds();
    }

    private void initializeSounds() {
        try {
            // Efectos de sonido cortos
            successPlayer = MediaPlayer.create(context, R.raw.game_sound);
            errorPlayer = MediaPlayer.create(context, R.raw.error);
            victoryPlayer = MediaPlayer.create(context, R.raw.victory);
            gameOverPlayer = MediaPlayer.create(context, R.raw.game_over);

            // Configurar volúmenes
            if (successPlayer != null) successPlayer.setVolume(0.8f, 0.8f);
            if (errorPlayer != null) errorPlayer.setVolume(0.7f, 0.7f);
            if (victoryPlayer != null) victoryPlayer.setVolume(0.9f, 0.9f);
            if (gameOverPlayer != null) gameOverPlayer.setVolume(0.8f, 0.8f);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSuccess() {
        if (soundEnabled && successPlayer != null) {
            try {
                if (successPlayer.isPlaying()) {
                    successPlayer.seekTo(0);
                } else {
                    successPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playError() {
        if (soundEnabled && errorPlayer != null) {
            try {
                if (errorPlayer.isPlaying()) {
                    errorPlayer.seekTo(0);
                } else {
                    errorPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playVictory() {
        if (soundEnabled && victoryPlayer != null) {
            try {
                if (victoryPlayer.isPlaying()) {
                    victoryPlayer.seekTo(0);
                } else {
                    victoryPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playGameOver() {
        if (soundEnabled && gameOverPlayer != null) {
            try {
                if (gameOverPlayer.isPlaying()) {
                    gameOverPlayer.seekTo(0);
                } else {
                    gameOverPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Control de música de fondo
    public void startBackgroundMusic() {
        MusicService.startBackgroundMusic(context);
    }

    public void pauseBackgroundMusic() {
        MusicService.pauseBackgroundMusic(context);
    }

    public void resumeBackgroundMusic() {
        MusicService.resumeBackgroundMusic(context);
    }

    public void stopBackgroundMusic() {
        MusicService.stopBackgroundMusic(context);
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
            if (gameOverPlayer != null) {
                gameOverPlayer.release();
                gameOverPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}