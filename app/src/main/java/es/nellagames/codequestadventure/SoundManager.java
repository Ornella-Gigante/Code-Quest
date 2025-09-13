package es.nellagames.codequestadventure;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class SoundManager {

    private SoundPool soundPool;
    private int successSound, errorSound, victorySound, gameOverSound;
    private boolean soundEnabled = true;
    private boolean soundsLoaded = false;
    private Context context;

    public SoundManager(Context context) {
        this.context = context.getApplicationContext();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build();

        soundPool.setOnLoadCompleteListener((sp, sampleId, status) -> {
            if (status == 0) {
                soundsLoaded = true;
            }
        });

        successSound = soundPool.load(this.context, R.raw.correct, 1);
        errorSound = soundPool.load(this.context, R.raw.error, 1);
        victorySound = soundPool.load(this.context, R.raw.victory, 1);
        gameOverSound = soundPool.load(this.context, R.raw.game_over, 1);

        // Inicia música de fondo usando MusicService
        MusicService.startBackgroundMusic(this.context);
    }

    // Efectos de sonido cortos
    public void playSuccess() {
        if (soundEnabled && soundsLoaded) soundPool.play(successSound, 1, 1, 1, 0, 1f);
    }

    public void playError() {
        if (soundEnabled && soundsLoaded) soundPool.play(errorSound, 1, 1, 1, 0, 1f);
    }

    public void playVictory() {
        if (soundEnabled && soundsLoaded) soundPool.play(victorySound, 1, 1, 1, 0, 1f);
    }

    public void playGameOver() {
        if (soundEnabled && soundsLoaded) soundPool.play(gameOverSound, 1, 1, 1, 0, 1f);
    }

    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        if (!enabled) {
            pauseBackgroundMusic();
        } else {
            resumeBackgroundMusic();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        // No detengas la música aquí para que siga sonando
    }

    // Métodos para controlar música de fondo mediante MusicService
    public void resumeBackgroundMusic() {
        try {
            if (MusicService.isRunning()) {
                MusicService.resumeBackgroundMusic(context);
            } else {
                MusicService.startBackgroundMusic(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseBackgroundMusic() {
        try {
            if (MusicService.isRunning()) {
                MusicService.pauseBackgroundMusic(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startBackgroundMusic() {
        try {
            if (!MusicService.isRunning()) {
                MusicService.startBackgroundMusic(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
