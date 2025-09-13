package es.nellagames.codequestadventure;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    private MediaPlayer player;
    private static boolean isRunning = false;

    public static void startBackgroundMusic(Context context) {
        if (!isRunning) {
            Intent intent = new Intent(context, MusicService.class);
            context.startService(intent);
        }
    }

    public static void resumeBackgroundMusic(Context context) {
        // Si quieres que pause/resume se comporten distinto, implementa aquí,
        // para ahora solo inicia el servicio (si no está corriendo)
        if (!isRunning) {
            Intent intent = new Intent(context, MusicService.class);
            context.startService(intent);
        }
    }

    public static void pauseBackgroundMusic(Context context) {
        // No se pausa realmente el servicio aquí. Si quieres, puedes implementar pause real
        // Por simplicidad no hacemos nada
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.game_sound);
            player.setLooping(true);
            player.setVolume(0.5f, 0.5f);
        }
        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (player != null && !player.isPlaying()) player.start();
        isRunning = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        isRunning = false;
        super.onDestroy();
    }

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
