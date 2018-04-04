package perfectride.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import perfectride.Model.User;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final int SINGLE_USER_ID = 100;
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "usersDatabase";

    private static final String TABLE_USERS = "users";

    private static final String KEY_ID = "id";
    private static final String KEY_F_NAME = "fname";
    private static final String KEY_L_NAME = "lname";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_CARD_NUMBER = "cardnumber";
    private static final String KEY_EXPIRY = "expiry";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGUTUDE = "longutude";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_F_NAME + " TEXT," + KEY_L_NAME + " TEXT," + KEY_CARD_NUMBER + " TEXT," + KEY_EXPIRY + " TEXT," + KEY_PHOTO + " TEXT," + KEY_LATITUDE + " TEXT,"
                + KEY_LONGUTUDE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, user.get_id());
        values.put(KEY_F_NAME, user.get_fname());
        values.put(KEY_L_NAME, user.get_lname());
        values.put(KEY_PHOTO, user.get_photo());

        long insert = db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public int updateContact(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_F_NAME, user.get_fname());
        values.put(KEY_L_NAME, user.get_lname());
        values.put(KEY_PHOTO, user.get_photo());
        values.put(KEY_CARD_NUMBER, user.get_cardNum());
        values.put(KEY_EXPIRY, user.get_expiry());

        // updating row
        int update = db.update(TABLE_USERS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(user.get_id())});
        return update;
    }

    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_ID,
                        KEY_F_NAME, KEY_L_NAME, KEY_PHOTO, KEY_CARD_NUMBER, KEY_EXPIRY}, KEY_ID + "=?",

                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {


            if (cursor.getCount() == 0) {
                user = new User(SINGLE_USER_ID, "narender", "reddy", "", "", "");
                addUser(user);
            } else {
                cursor.moveToFirst();
                user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
            }
        }

        return user;
    }

}