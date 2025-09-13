package es.nellagames.codequestadventure;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import es.nellagames.codequestadventure.LeaderboardEntry;

public class LeaderboardDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "game.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "_id";
    private static final String COLUMN_USERNAME = "username";

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
        String createUsers =
                "CREATE TABLE " + TABLE_USERS + " (" +
                        COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_USERNAME + " TEXT UNIQUE NOT NULL)";
        db.execSQL(createUsers);

        String createLeaderboard =
                "CREATE TABLE " + TABLE_LEADERBOARD + " (" +
                        COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_USER_ID_FK + " INTEGER, " +
                        COLUMN_SCORE + " INTEGER, " +
                        COLUMN_AVATAR_URL + " TEXT, " +
                        COLUMN_TIMESTAMP + " LONG," +
                        "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createLeaderboard);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEADERBOARD);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }

    public long insertUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        return db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + "=?",
                new String[]{username},
                null, null, null);
        long userId = -1;
        if (c.moveToFirst()) {
            userId = c.getLong(c.getColumnIndexOrThrow(COLUMN_USER_ID));
        }
        c.close();
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

    public List<LeaderboardEntry> getEntriesForUser(long userId) {
        List<LeaderboardEntry> entries = new ArrayList<>();
        if (userId < 0) return entries;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LEADERBOARD,
                null,
                COLUMN_USER_ID_FK + "=?",
                new String[]{String.valueOf(userId)},
                null, null,
                COLUMN_SCORE + " DESC");
        while (cursor.moveToNext()) {
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_URL));
            entries.add(new LeaderboardEntry("", score, avatar));
        }
        cursor.close();
        return entries;
    }
}
