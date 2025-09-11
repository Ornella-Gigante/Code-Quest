package es.nellagames.codequestadventure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "leaderboard.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "leaderboard";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_PLAYER_NAME = "player_name";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_AVATAR_URL = "avatar_url";
    private static final String COLUMN_TIMESTAMP = "timestamp"; // Para resets basados en tiempo

    public LeaderboardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PLAYER_NAME + " TEXT, " +
                COLUMN_SCORE + " INTEGER, " +
                COLUMN_AVATAR_URL + " TEXT, " +
                COLUMN_TIMESTAMP + " LONG)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Método para insertar una entrada
    public void insertEntry(LeaderboardEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER_NAME, entry.playerName);
        values.put(COLUMN_SCORE, entry.score);
        values.put(COLUMN_AVATAR_URL, entry.avatarUrl);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        db.insert(TABLE_NAME, null, values);
    }

    // Método para cargar todas las entradas (ordenadas por score descendente)
    public List<LeaderboardEntry> getAllEntries() {
        List<LeaderboardEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_SCORE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_NAME));
                int score = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE));
                String avatar = cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR_URL));
                entries.add(new LeaderboardEntry(name, score, avatar));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entries;
    }

    // Método para reset (borrar entradas antiguas, ej. mayores a 7 días)
    public void resetOldEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        long oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
        db.delete(TABLE_NAME, COLUMN_TIMESTAMP + " < ?", new String[]{String.valueOf(oneWeekAgo)});
    }
}
