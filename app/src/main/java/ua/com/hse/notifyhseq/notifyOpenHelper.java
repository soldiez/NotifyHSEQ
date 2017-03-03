package ua.com.hse.notifyhseq;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class NotifyOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "NotifyHSEQ_DB";
    public static final String TABLE_NAME = "NOTIFY_TABLE";
    public static final int VERSION = 1;
    public static final String KEY_ID = "_id";
    public static final String MAIN_NUMBER = "MAIN_NUMBER";
    public static final String SYNC = "SYNC";
    public static final String DATE_REGISTRATION = "DATE_REGISTRATION";
    public static final String TIME_REGISTRATION = "TIME_REGISTRATION";
    public static final String DATE_HAPPENED = "DATE_HAPPENED";
    public static final String TIME_HAPPENED = "TIME_HAPPENED";
    public static final String TYPE = "TYPE";
    public static final String PLACE = "PLACE";
    public static final String DEPARTMENT = "DEPARTMENT";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String PHOTO_PATH = "PHOTO_PATH";
    public static final String PHOTO_NAME = "PHOTO_NAME";
    public static final String STATUS = "STATUS";
    public static final String NAME_PERSON = "NAME_PERSON";
    public static final String EMAIL_PERSON = "EMAIL_PERSON";
    public static final String PHONE_PERSON = "PHONE_PERSON";
    public static final String DEPARTMENT_PERSON = "DEPARTMENT_PERSON";


    public static final String SCRIPT = "create table " + TABLE_NAME + " ("
            + KEY_ID + " integer primary key autoincrement, " +
            MAIN_NUMBER + ", " +
            SYNC + " text not null, " +
            DATE_REGISTRATION + " text not null, " +
            TIME_REGISTRATION + " text not null, " +
            DATE_HAPPENED + ", " +
            TIME_HAPPENED + ", " +
            TYPE + ", " +
            PLACE + ", " +
            DEPARTMENT + ", " +
            DESCRIPTION + ", " +
            PHOTO_PATH + ", " +
            PHOTO_NAME + ", " +
            STATUS + ", " +
            NAME_PERSON + ", " +
            EMAIL_PERSON + ", " +
            PHONE_PERSON + ", " +
            DEPARTMENT_PERSON +
            ");";

    public NotifyOpenHelper(Context context, String name,
                            CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }

}
