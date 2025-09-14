package es.nellagames.codequestadventure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class LeaderboardDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "game.db";
    private static final int DATABASE_VERSION = 5; // Increased version for new methods

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

    // ===============================
    // USER MANAGEMENT METHODS
    // ===============================

    public long insertUserWithImage(String username, Bitmap avatarBitmap) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);

        if (avatarBitmap != null) {
            // Resize and compress avatar
            Bitmap resized = Bitmap.createScaledBitmap(avatarBitmap, 64, 64, true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.JPEG, 40, stream);
            byte[] avatarBytes = stream.toByteArray();

            // Ensure avatar data isn't too large
            if (avatarBytes.length > 100000) {
                stream.reset();
                resized.compress(Bitmap.CompressFormat.JPEG, 20, stream);
                avatarBytes = stream.toByteArray();
            }

            values.put(COLUMN_AVATAR, avatarBytes);
        } else {
            values.putNull(COLUMN_AVATAR);
        }

        return db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        long userId = -1;

        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                }
            } finally {
                cursor.close();
            }
        }

        return userId;
    }

    public Cursor getUserCursor(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
    }

    public Cursor getUserCursorById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
    }

    public Bitmap getAvatarImageForUser(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Bitmap result = null;

        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_AVATAR},
                COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    byte[] avatarBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_AVATAR));
                    if (avatarBytes != null && avatarBytes.length > 0) {
                        result = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return result;
    }

    public Bitmap getAvatarImageForUserId(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Bitmap result = null;

        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_AVATAR},
                COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    byte[] avatarBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_AVATAR));
                    if (avatarBytes != null && avatarBytes.length > 0) {
                        result = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return result;
    }

    // ===============================
    // LEADERBOARD METHODS
    // ===============================

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

    public int getUserMaxScore(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int maxScore = 0;

        String query = "SELECT MAX(score) as max_score FROM " + TABLE_LEADERBOARD +
                " WHERE " + COLUMN_USER_ID_FK + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    maxScore = cursor.getInt(cursor.getColumnIndexOrThrow("max_score"));
                }
            } finally {
                cursor.close();
            }
        }

        return maxScore;
    }

    public void clearUserScores(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LEADERBOARD, COLUMN_USER_ID_FK + "=?", new String[]{String.valueOf(userId)});
    }

    public int getUserScoreCount(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LEADERBOARD +
                        " WHERE " + COLUMN_USER_ID_FK + " = ?",
                new String[]{String.valueOf(userId)});

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    count = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }

        return count;
    }

    // ===============================
    // CLEANUP METHODS
    // ===============================

    public void clearAllData() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LEADERBOARD + ";");
        db.execSQL("DELETE FROM " + TABLE_USERS + ";");
    }

    public void clearAllUsers() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LEADERBOARD + ";");
        db.execSQL("DELETE FROM " + TABLE_USERS + ";");
    }

    public void deleteUser(long userId) {
        SQLiteDatabase db = getWritableDatabase();

        // First delete all leaderboard entries for this user
        db.delete(TABLE_LEADERBOARD, COLUMN_USER_ID_FK + "=?", new String[]{String.valueOf(userId)});

        // Then delete the user
        db.delete(TABLE_USERS, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
    }

    // ===============================
    // UTILITY CLASSES
    // ===============================

    public static class UserWithAvatar {
        public final String username;
        public final Bitmap avatar;
        public final long userId;

        public UserWithAvatar(long userId, String username, Bitmap avatar) {
            this.userId = userId;
            this.username = username;
            this.avatar = avatar;
        }
    }

    public UserWithAvatar getUserWithAvatar(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        UserWithAvatar result = null;

        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USERNAME, COLUMN_AVATAR},
                COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    String username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
                    byte[] avatarBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_AVATAR));

                    Bitmap avatar = null;
                    if (avatarBytes != null && avatarBytes.length > 0) {
                        avatar = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
                    }

                    result = new UserWithAvatar(userId, username, avatar);
                }
            } finally {
                cursor.close();
            }
        }

        return result;
    }

    public boolean userExists(String username) {
        return getUserId(username) != -1;
    }

    public boolean userExistsById(long userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean exists = false;

        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID},
                COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null) {
            try {
                exists = cursor.moveToFirst();
            } finally {
                cursor.close();
            }
        }

        return exists;
    }

    public void deleteAllUsers(Context context) {
        LeaderboardDbHelper dbHelper = new LeaderboardDbHelper(context);
        dbHelper.clearAllData();  // Borra todos los usuarios y sus scores
        dbHelper.close();

        Toast.makeText(context, "All users have been erased.", Toast.LENGTH_SHORT).show();
    }

}