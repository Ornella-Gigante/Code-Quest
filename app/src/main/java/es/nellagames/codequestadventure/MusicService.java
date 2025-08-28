package es.nellagames.codequestadventure;



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
    private final IBinder binder = new MusicBinder();

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
        return START_STICKY; // Reinicia el servicio si es terminado por el sistema
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void playMusic() {
        if (mediaPlayer == null) {
            try {
                mediaPlayer = MediaPlayer.create(this, R.raw.game_sound);
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(0.6f, 0.6f); // Volumen al 60%
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        if (!mediaPlayer.isPlaying() && !isPaused) {
            mediaPlayer.start();
        }
    }

    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }

    public void resumeMusic() {
        if (mediaPlayer != null && isPaused) {
            mediaPlayer.start();
            isPaused = false;
        }
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            isPaused = false;
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    @Override
    public void onDestroy() {
        stopMusic();
        super.onDestroy();
    }

    // Métodos estáticos para controlar el servicio desde cualquier Activity
    public static void startBackgroundMusic(android.content.Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(ACTION_PLAY);
        context.startService(intent);
    }

    public static void pauseBackgroundMusic(android.content.Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(ACTION_PAUSE);
        context.startService(intent);
    }

    public static void resumeBackgroundMusic(android.content.Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(ACTION_RESUME);
        context.startService(intent);
    }

    public static void stopBackgroundMusic(android.content.Context context) {
        Intent intent = new Intent(context, MusicService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }
}
