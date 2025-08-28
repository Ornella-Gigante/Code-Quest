package es.nellagames.codequestadventure;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import es.nellagames.codequestadventure.R;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import es.nellagames.codequestadventure.R;

public class MusicService extends Service {

    private static final String ACTION_PLAY = "PLAY";
    private static final String ACTION_PAUSE = "PAUSE";
    private static final String ACTION_RESUME = "RESUME";
    private static final String ACTION_STOP = "STOP";

    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;
    private static boolean isServiceRunning = false; // ✅ Control de estado
    private final IBinder binder = new MusicBinder();

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true; // ✅ Marcar servicio como activo
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_PLAY:
                        playMusic();
                        break;
                    case ACTION_PAUSE:
                        pauseMusic();
                        break;
                    case ACTION_RESUME:
                        resumeMusic();
                        break;
                    case ACTION_STOP:
                        stopMusic();
                        break;
                }
            }
        }
        return START_NOT_STICKY; // ✅ Cambiar a NOT_STICKY para evitar reinicios automáticos
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void playMusic() {
        // ✅ Verificar si ya está reproduciéndose ANTES de crear nueva instancia
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return; // Ya está reproduciéndose, no hacer nada
        }

        if (mediaPlayer == null) {
            try {
                mediaPlayer = MediaPlayer.create(this, R.raw.game_sound);
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(0.6f, 0.6f);
                } else {
                    return; // No se pudo crear el MediaPlayer
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        if (!mediaPlayer.isPlaying() && !isPaused) {
            try {
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.pause();
                isPaused = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void resumeMusic() {
        if (mediaPlayer != null && isPaused) {
            try {
                mediaPlayer.start();
                isPaused = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mediaPlayer = null;
                isPaused = false;
            }
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setVolume(volume, volume);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        stopMusic();
        isServiceRunning = false; // ✅ Marcar servicio como inactivo
        super.onDestroy();
    }

    // ✅ Métodos estáticos mejorados con verificación de estado
    public static void startBackgroundMusic(android.content.Context context) {
        if (!isServiceRunning) { // Solo iniciar si no está activo
            Intent intent = new Intent(context, MusicService.class);
            intent.setAction(ACTION_PLAY);
            context.startService(intent);
        }
    }

    public static void pauseBackgroundMusic(android.content.Context context) {
        if (isServiceRunning) {
            Intent intent = new Intent(context, MusicService.class);
            intent.setAction(ACTION_PAUSE);
            context.startService(intent);
        }
    }

    public static void resumeBackgroundMusic(android.content.Context context) {
        if (isServiceRunning) {
            Intent intent = new Intent(context, MusicService.class);
            intent.setAction(ACTION_RESUME);
            context.startService(intent);
        }
    }

    public static void stopBackgroundMusic(android.content.Context context) {
        if (isServiceRunning) {
            Intent intent = new Intent(context, MusicService.class);
            intent.setAction(ACTION_STOP);
            context.stopService(intent);
        }
    }

    // ✅ Método para verificar estado externamente
    public static boolean isRunning() {
        return isServiceRunning;
    }
}
