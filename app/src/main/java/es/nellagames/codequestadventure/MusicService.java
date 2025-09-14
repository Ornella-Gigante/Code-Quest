package es.nellagames.codequestadventure;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private MediaPlayer player;
    private static MusicService instance;
    private boolean isPaused = false;
    private boolean isInitialized = false;
    private boolean isDestroyed = false;
    private Handler mainHandler;
    private final Object lock = new Object();

    public static void startBackgroundMusic(Context context) {
        try {
            if (context != null) {
                Intent intent = new Intent(context, MusicService.class);
                intent.setAction("START");
                context.startService(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting music service", e);
        }
    }

    public static void pauseBackgroundMusic(Context context) {
        try {
            if (context != null) {
                Intent intent = new Intent(context, MusicService.class);
                intent.setAction("PAUSE");
                context.startService(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error pausing music service", e);
        }
    }

    public static void resumeBackgroundMusic(Context context) {
        try {
            if (context != null) {
                Intent intent = new Intent(context, MusicService.class);
                intent.setAction("RESUME");
                context.startService(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error resuming music service", e);
        }
    }

    public static void stopBackgroundMusic(Context context) {
        try {
            if (context != null) {
                Intent intent = new Intent(context, MusicService.class);
                context.stopService(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping music service", e);
        }
    }

    public static boolean isRunning() {
        return instance != null && instance.isInitialized && !instance.isDestroyed;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MusicService created");
        instance = this;
        isDestroyed = false;
        mainHandler = new Handler(Looper.getMainLooper());

        // Initialize player on main thread with delay to ensure stability
        mainHandler.post(() -> {
            try {
                initializePlayer();
            } catch (Exception e) {
                Log.e(TAG, "Error in onCreate initialization", e);
            }
        });
    }

    private void initializePlayer() {
        synchronized (lock) {
            try {
                if (player == null && !isDestroyed) {
                    player = MediaPlayer.create(this, R.raw.game_sound);
                    if (player != null) {
                        player.setLooping(true);
                        player.setVolume(0.3f, 0.3f);

                        player.setOnErrorListener((mp, what, extra) -> {
                            Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                            mainHandler.post(() -> {
                                releasePlayer();
                                // Try to reinitialize after a delay
                                mainHandler.postDelayed(this::initializePlayer, 1000);
                            });
                            return true;
                        });

                        player.setOnCompletionListener(mp -> {
                            Log.d(TAG, "MediaPlayer completed");
                            if (player != null && !isDestroyed) {
                                try {
                                    if (player.isLooping()) {
                                        player.start();
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error restarting looped music", e);
                                }
                            }
                        });

                        player.setOnPreparedListener(mp -> {
                            Log.d(TAG, "MediaPlayer prepared");
                            isInitialized = true;
                        });

                        isInitialized = true;
                        Log.d(TAG, "MediaPlayer initialized successfully");
                    } else {
                        Log.e(TAG, "Failed to create MediaPlayer");
                        // Retry initialization after delay
                        if (!isDestroyed) {
                            mainHandler.postDelayed(this::initializePlayer, 2000);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error initializing player", e);
                releasePlayer();
                // Retry after delay if not destroyed
                if (!isDestroyed) {
                    mainHandler.postDelayed(this::initializePlayer, 2000);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isDestroyed) {
            Log.w(TAG, "Service is destroyed, ignoring command");
            return START_NOT_STICKY;
        }

        String action = "START";
        if (intent != null && intent.getAction() != null) {
            action = intent.getAction();
        }

        Log.d(TAG, "Action received: " + action);

        final String finalAction = action;
        mainHandler.post(() -> {
            try {
                switch (finalAction) {
                    case "START":
                        startMusic();
                        break;
                    case "PAUSE":
                        pauseMusic();
                        break;
                    case "RESUME":
                        resumeMusic();
                        break;
                    default:
                        startMusic();
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in onStartCommand", e);
            }
        });

        return START_STICKY; // Restart automatically if the system kills it
    }

    private void startMusic() {
        synchronized (lock) {
            try {
                if (isDestroyed) return;

                if (!isInitialized) {
                    initializePlayer();
                    // Delay start to allow initialization
                    mainHandler.postDelayed(this::startMusic, 500);
                    return;
                }

                if (player != null && !player.isPlaying() && !isPaused) {
                    player.start();
                    Log.d(TAG, "Music started");
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "IllegalStateException starting music", e);
                releasePlayer();
                initializePlayer();
            } catch (Exception e) {
                Log.e(TAG, "Error starting music", e);
            }
        }
    }

    private void pauseMusic() {
        synchronized (lock) {
            try {
                if (player != null && player.isPlaying()) {
                    player.pause();
                    isPaused = true;
                    Log.d(TAG, "Music paused");
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "IllegalStateException pausing music", e);
                isPaused = true;
            } catch (Exception e) {
                Log.e(TAG, "Error pausing music", e);
                isPaused = true;
            }
        }
    }

    private void resumeMusic() {
        synchronized (lock) {
            try {
                if (isDestroyed) return;

                if (player != null && isPaused) {
                    player.start();
                    isPaused = false;
                    Log.d(TAG, "Music resumed");
                } else if (player != null && !player.isPlaying() && !isPaused) {
                    startMusic();
                }
            } catch (IllegalStateException e) {
                Log.e(TAG, "IllegalStateException resuming music", e);
                releasePlayer();
                initializePlayer();
                isPaused = false;
            } catch (Exception e) {
                Log.e(TAG, "Error resuming music", e);
                isPaused = false;
            }
        }
    }

    private void releasePlayer() {
        synchronized (lock) {
            try {
                if (player != null) {
                    try {
                        if (player.isPlaying()) {
                            player.stop();
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Error stopping player during release", e);
                    }

                    try {
                        player.reset();
                    } catch (Exception e) {
                        Log.w(TAG, "Error resetting player during release", e);
                    }

                    try {
                        player.release();
                    } catch (Exception e) {
                        Log.w(TAG, "Error releasing player", e);
                    }

                    player = null;
                }
                isInitialized = false;
                isPaused = false;
                Log.d(TAG, "MediaPlayer released");
            } catch (Exception e) {
                Log.e(TAG, "Error in releasePlayer", e);
                player = null;
                isInitialized = false;
                isPaused = false;
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "MusicService destroyed");
        isDestroyed = true;

        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }

        releasePlayer();
        instance = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // Keep music alive when task is removed
        Log.d(TAG, "Task removed, but keeping music alive");
        super.onTaskRemoved(rootIntent);
    }
}