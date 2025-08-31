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
            // ✅ SOLUCIÓN: Usar un archivo diferente para el sonido de éxito
            // ❌ ANTES: successPlayer = MediaPlayer.create(context, R.raw.game_sound);
            errorPlayer = MediaPlayer.create(context, R.raw.error);
            victoryPlayer = MediaPlayer.create(context, R.raw.victory);
            gameOverPlayer = MediaPlayer.create(context, R.raw.game_over);
            successPlayer = MediaPlayer.create(context, R.raw.victory); // <- aquí

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
        playSound(successPlayer);
    }

    public void playError() {
        playSound(errorPlayer);
    }

    public void playVictory() {
        playSound(victoryPlayer);
    }

    public void playGameOver() {
        playSound(gameOverPlayer);
    }

    // ✅ Método centralizado para reproducir sonidos
    private void playSound(MediaPlayer player) {
        if (soundEnabled && player != null) {
            try {
                if (player.isPlaying()) {
                    player.seekTo(0);
                } else {
                    player.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ✅ Control de música de fondo - Solo iniciar si no está corriendo
    public void startBackgroundMusic() {
        if (!MusicService.isRunning()) {
            MusicService.startBackgroundMusic(context);
        }
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