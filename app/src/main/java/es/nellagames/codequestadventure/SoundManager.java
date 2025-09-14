package es.nellagames.codequestadventure;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

public class SoundManager {

    private static final String TAG = "SoundManager";
    private static final String PREFS_NAME = "CodeQuestSoundSettings";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";

    private SoundPool soundPool;
    private int successSound = -1, errorSound = -1, victorySound = -1, gameOverSound = -1;
    private boolean soundEnabled = true;
    private boolean soundsLoaded = false;
    private Context context;
    private SharedPreferences prefs;
    private int loadedSounds = 0;
    private final int totalSounds = 4;
    private boolean isReleased = false;
    private Handler mainHandler;

    public SoundManager(Context context) {
        try {
            this.context = context.getApplicationContext();
            this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            this.mainHandler = new Handler(Looper.getMainLooper());

            // Load sound preference
            soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true);

            initializeSoundPool();
            loadSounds();

            Log.d(TAG, "SoundManager initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SoundManager", e);
            soundEnabled = false;
        }
    }

    private void initializeSoundPool() {
        try {
            if (isReleased) return;

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(audioAttributes)
                    .build();

            soundPool.setOnLoadCompleteListener((sp, sampleId, status) -> {
                if (isReleased) return;

                if (status == 0) {
                    loadedSounds++;
                    Log.d(TAG, "Sound loaded: " + sampleId + " (" + loadedSounds + "/" + totalSounds + ")");

                    if (loadedSounds >= totalSounds) {
                        soundsLoaded = true;
                        Log.d(TAG, "All sounds loaded successfully");
                    }
                } else {
                    Log.e(TAG, "Failed to load sound: " + sampleId + ", status: " + status);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error initializing SoundPool", e);
            soundPool = null;
        }
    }

    private void loadSounds() {
        if (soundPool == null || isReleased) return;

        try {
            successSound = soundPool.load(context, R.raw.correct, 1);
            errorSound = soundPool.load(context, R.raw.error, 1);
            victorySound = soundPool.load(context, R.raw.victory, 1);
            gameOverSound = soundPool.load(context, R.raw.game_over, 1);

            Log.d(TAG, "Sound loading initiated");
        } catch (Exception e) {
            Log.e(TAG, "Error loading sounds", e);
            // Reset sound IDs on error
            successSound = errorSound = victorySound = gameOverSound = -1;
        }
    }

    public void playSuccess() {
        playSound(successSound, "success");
    }

    public void playError() {
        playSound(errorSound, "error");
    }

    public void playVictory() {
        playSound(victorySound, "victory");
    }

    public void playGameOver() {
        playSound(gameOverSound, "gameOver");
    }

    private void playSound(int soundId, String soundName) {
        if (!soundEnabled || !soundsLoaded || soundId == -1 || soundPool == null || isReleased) {
            return;
        }

        try {
            int result = soundPool.play(soundId, 0.7f, 0.7f, 1, 0, 1.0f);
            if (result == 0) {
                Log.w(TAG, "Failed to play " + soundName + " sound - no available streams");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing " + soundName + " sound", e);
        }
    }

    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;

        try {
            prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving sound preference", e);
        }

        // Manage background music based on sound setting
        if (enabled) {
            startBackgroundMusic();
        } else {
            pauseBackgroundMusic();
        }

        Log.d(TAG, "Sound enabled: " + enabled);
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    // Background music control methods - with improved error handling
    public void startBackgroundMusic() {
        if (!soundEnabled || isReleased) return;

        try {
            MusicService.startBackgroundMusic(context);
        } catch (Exception e) {
            Log.e(TAG, "Error starting background music", e);
        }
    }

    public void pauseBackgroundMusic() {
        if (isReleased) return;

        try {
            MusicService.pauseBackgroundMusic(context);
        } catch (Exception e) {
            Log.e(TAG, "Error pausing background music", e);
        }
    }

    public void resumeBackgroundMusic() {
        if (!soundEnabled || isReleased) return;

        try {
            MusicService.resumeBackgroundMusic(context);
        } catch (Exception e) {
            Log.e(TAG, "Error resuming background music", e);
        }
    }

    public void stopBackgroundMusic() {
        if (isReleased) return;

        try {
            MusicService.stopBackgroundMusic(context);
        } catch (Exception e) {
            Log.e(TAG, "Error stopping background music", e);
        }
    }

    public void release() {
        if (isReleased) return;

        isReleased = true;

        try {
            if (mainHandler != null) {
                mainHandler.removeCallbacksAndMessages(null);
            }

            if (soundPool != null) {
                soundPool.release();
                soundPool = null;
                soundsLoaded = false;
                loadedSounds = 0;
                Log.d(TAG, "SoundPool released");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error releasing SoundPool", e);
        } finally {
            soundPool = null;
            soundsLoaded = false;
            loadedSounds = 0;
        }
    }

    public boolean areSoundsLoaded() {
        return soundsLoaded && !isReleased;
    }

    public int getLoadedSoundsCount() {
        return loadedSounds;
    }

    public boolean isReleased() {
        return isReleased;
    }
}