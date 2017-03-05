package ua.com.hse.notifyhseq;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
    SQLiteDatabase database_ob;
    NotifyOpenHelper openHelper_ob;
    Context context;

    public DBAdapter(Context c) {
        context = c;
    }

    public DBAdapter opnToRead() {
        openHelper_ob = new NotifyOpenHelper(context,
                openHelper_ob.DATABASE_NAME, null, openHelper_ob.VERSION);
        database_ob = openHelper_ob.getReadableDatabase();
        return this;

    }

    public DBAdapter opnToWrite() {
        openHelper_ob = new NotifyOpenHelper(context,
                openHelper_ob.DATABASE_NAME, null, openHelper_ob.VERSION);
        database_ob = openHelper_ob.getWritableDatabase();
        return this;

    }

    public void Close() {
        database_ob.close();
    }


    public long insertDetails(int mainNumber, int sync, String dateRegistration, String timeRegistration,
                              String dateHappened, String timeHappened, String type, String place,
                              String department, String description, String photoPath, String photoName,
                              int status, String namePerson, String emailPerson, String phonePerson, String departmentPerson) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(openHelper_ob.MAIN_NUMBER, mainNumber);
        contentValues.put(openHelper_ob.SYNC, sync);
        contentValues.put(openHelper_ob.DATE_REGISTRATION, dateRegistration);
        contentValues.put(openHelper_ob.TIME_REGISTRATION, timeRegistration);
        contentValues.put(openHelper_ob.DATE_HAPPENED, dateHappened);
        contentValues.put(openHelper_ob.TIME_HAPPENED, timeHappened);
        contentValues.put(openHelper_ob.TYPE, type);
        contentValues.put(openHelper_ob.PLACE, place);
        contentValues.put(openHelper_ob.DEPARTMENT, department);
        contentValues.put(openHelper_ob.DESCRIPTION, description);
        contentValues.put(openHelper_ob.PHOTO_PATH, photoPath);
        contentValues.put(openHelper_ob.PHOTO_NAME, photoName);
        contentValues.put(openHelper_ob.STATUS, status);
        contentValues.put(openHelper_ob.NAME_PERSON, namePerson);
        contentValues.put(openHelper_ob.EMAIL_PERSON, emailPerson);
        contentValues.put(openHelper_ob.PHONE_PERSON, phonePerson);
        contentValues.put(openHelper_ob.DEPARTMENT_PERSON, departmentPerson);
        opnToWrite();
        long val = database_ob.insert(openHelper_ob.TABLE_NAME, null,
                contentValues);
        Close();
        return val;

    }

    public Cursor queryName() {
        String[] cols = {openHelper_ob.KEY_ID,
                openHelper_ob.MAIN_NUMBER,
                openHelper_ob.SYNC,
                openHelper_ob.DATE_REGISTRATION,
                openHelper_ob.TIME_REGISTRATION,
                openHelper_ob.DATE_HAPPENED,
                openHelper_ob.TIME_HAPPENED,
                openHelper_ob.TYPE,
                openHelper_ob.PLACE,
                openHelper_ob.DEPARTMENT,
                openHelper_ob.DESCRIPTION,
                openHelper_ob.PHOTO_PATH,
                openHelper_ob.PHOTO_NAME,
                openHelper_ob.STATUS,
                openHelper_ob.NAME_PERSON,
                openHelper_ob.EMAIL_PERSON,
                openHelper_ob.PHONE_PERSON,
                openHelper_ob.DEPARTMENT_PERSON};
        opnToWrite();
        Cursor c = database_ob.query(openHelper_ob.TABLE_NAME, cols, null,
                null, null, null, null);

        return c;

    }

    public Cursor queryAll(int id) {
        String[] cols = {openHelper_ob.KEY_ID,
                openHelper_ob.MAIN_NUMBER,
                openHelper_ob.SYNC,
                openHelper_ob.DATE_REGISTRATION,
                openHelper_ob.TIME_REGISTRATION,
                openHelper_ob.DATE_HAPPENED,
                openHelper_ob.TIME_HAPPENED,
                openHelper_ob.TYPE,
                openHelper_ob.PLACE,
                openHelper_ob.DEPARTMENT,
                openHelper_ob.DESCRIPTION,
                openHelper_ob.PHOTO_PATH,
                openHelper_ob.PHOTO_NAME,
                openHelper_ob.STATUS,
                openHelper_ob.NAME_PERSON,
                openHelper_ob.EMAIL_PERSON,
                openHelper_ob.PHONE_PERSON,
                openHelper_ob.DEPARTMENT_PERSON};
        opnToWrite();
        Cursor c = database_ob.query(openHelper_ob.TABLE_NAME, cols,
                openHelper_ob.KEY_ID + "=" + id, null, null, null, null);

        return c;

    }

    public long updateDetail(int rowId, int mainNumber, int sync, String dateRegistration, String timeRegistration,
                             String dateHappened, String timeHappened, String type, String place,
                             String department, String description, String photoPath, String photoName,
                             int status, String namePerson, String emailPerson, String phonePerson, String departmentPerson) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(openHelper_ob.MAIN_NUMBER, mainNumber);
        contentValues.put(openHelper_ob.SYNC, sync);
        contentValues.put(openHelper_ob.DATE_REGISTRATION, dateRegistration);
        contentValues.put(openHelper_ob.TIME_REGISTRATION, timeRegistration);
        contentValues.put(openHelper_ob.DATE_HAPPENED, dateHappened);
        contentValues.put(openHelper_ob.TIME_HAPPENED, timeHappened);
        contentValues.put(openHelper_ob.TYPE, type);
        contentValues.put(openHelper_ob.PLACE, place);
        contentValues.put(openHelper_ob.DEPARTMENT, department);
        contentValues.put(openHelper_ob.DESCRIPTION, description);
        contentValues.put(openHelper_ob.PHOTO_PATH, photoPath);
        contentValues.put(openHelper_ob.PHOTO_NAME, photoName);
        contentValues.put(openHelper_ob.STATUS, status);
        contentValues.put(openHelper_ob.NAME_PERSON, namePerson);
        contentValues.put(openHelper_ob.EMAIL_PERSON, emailPerson);
        contentValues.put(openHelper_ob.PHONE_PERSON, phonePerson);
        contentValues.put(openHelper_ob.DEPARTMENT_PERSON, departmentPerson);
        opnToWrite();
        long val = database_ob.update(openHelper_ob.TABLE_NAME, contentValues,
                openHelper_ob.KEY_ID + "=" + rowId, null);
        Close();
        return val;
    }

    public int deletOneRecord(int rowId) {
        // TODO Auto-generated method stub
        opnToWrite();
        int val = database_ob.delete(openHelper_ob.TABLE_NAME,
                openHelper_ob.KEY_ID + "=" + rowId, null);
        Close();
        return val;
    }

}
