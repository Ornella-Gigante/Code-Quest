package es.nellagames.codequestadventure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "game.db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_AVATAR = "avatar";

    private static final String TABLE_LEADERBOARD = "leaderboard";
    private static final String COLUMN_ENTRY_ID = "_id";
    private static final String COLUMN_USER_ID_FK = "user_id";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_AVATAR_URL = "avatar_url";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public LeaderboardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, " +
                COLUMN_AVATAR + " BLOB)";
        db.execSQL(createUsers);

        String createLeaderboard = "CREATE TABLE " + TABLE_LEADERBOARD + " (" +
                COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                COLUMN_SCORE + " INTEGER, " +
                COLUMN_AVATAR_URL + " TEXT, " +
                COLUMN_TIMESTAMP + " LONG, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createLeaderboard);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEADERBOARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Inserta un usuario con avatar pequeño y comprimido
    public long insertUserWithImage(String username, Bitmap avatarBitmap) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);

        if (avatarBitmap != null) {
            // Redimensionar a 64x64 y comprimir mucho
            Bitmap resized = Bitmap.createScaledBitmap(avatarBitmap, 64, 64, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 40, stream);
            byte[] avatarBytes = stream.toByteArray();
            values.put(COLUMN_AVATAR, avatarBytes);
        } else {
            values.putNull(COLUMN_AVATAR);
        }
        return db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    // Borra todos los usuarios de la tabla (para limpiar registros problemáticos)
    public void clearAllUsers() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USERS);
    }

    // Obtener el avatar como Bitmap a partir del username
    public Bitmap getAvatarImageForUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_AVATAR}, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        Bitmap result = null;
        if (cursor.moveToFirst()) {
            byte[] avatarBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_AVATAR));
            if (avatarBytes != null) {
                result = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
            }
        }
        cursor.close();
        return result;
    }

    public Cursor getUserCursor(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
    }

    public Cursor getUserCursorById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
    }

    public long getUserId(String username) {
        long userId = -1;
        try (Cursor c = getUserCursor(username)) {
            if (c.moveToFirst()) {
                userId = c.getLong(c.getColumnIndexOrThrow(COLUMN_USER_ID));
            }
        }
        return userId;
    }

    public void insertEntry(long userId, int score, String avatarUrl) {
        if (userId < 0) return;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userId);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_AVATAR_URL, avatarUrl);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        db.insert(TABLE_LEADERBOARD, null, values);
    }

    // Clase auxiliar para devolver usuario + avatar
    public static class UserWithAvatar {
        public final String username;
        public final Bitmap avatar;
        public UserWithAvatar(String username, Bitmap avatar) {
            this.username = username;
            this.avatar = avatar;
        }
    }
}
