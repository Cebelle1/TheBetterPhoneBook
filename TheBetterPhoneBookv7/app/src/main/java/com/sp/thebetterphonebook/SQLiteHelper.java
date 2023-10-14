package com.sp.thebetterphonebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "contacts.db";
    private static final int SCHEMA_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Called once when the database is not created

        db.execSQL("CREATE TABLE contactlist_table ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "contactName TEXT, contactNo TEXT, contactAddr TEXT, contactPhoto BLOB);");
        db.execSQL("INSERT INTO contactlist_table (contactName, ContactNo,contactAddr , contactPhoto) VALUES ('','','','')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Will not be called until schema version increases
    }

    //Reads all records from table
    public Cursor getAllContact() {
        Cursor mCursor = getReadableDatabase().rawQuery("SELECT _id, contactName, contactNo, contactAddr, contactPhoto" +
                " FROM contactlist_table ORDER BY contactName", null);
        mCursor.moveToFirst();
            return mCursor;
    }



    //Read a particular contact from the table using id
    public Cursor getContById(String id){
        String[] args = {id};
        return(getReadableDatabase().rawQuery("SELECT _id, contactName, contactNo, contactAddr, contactPhoto" +
                " FROM contactlist_table WHERE _ID=?",args));

    }

    public Cursor getContByName(String name){
        String[] args = {name};
        return(getReadableDatabase().rawQuery("SELECT _id, contactName, contactNo, contactAddr, contactPhoto" +
                " FROM contactlist_table WHERE contactName=?",args));
    }

    public Cursor getAllName(){
        return(getReadableDatabase().rawQuery("SELECT contactName FROM contactlist_table",null));
    }


    public int countDB(){
        Cursor temp = (getReadableDatabase().rawQuery("SELECT * FROM contactlist_table",null));
        int count = temp.getCount();
        return count;
    }


    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void deleteData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from contactlist_table");
        db.execSQL("vacuum ");
        db.close();
    }

    //Adds a contact to the table
    public void insertContact(String contactName, String contactNo, String contactAddr, byte[] contactPhoto)
    {
        ContentValues cv= new ContentValues();
        cv.put("contactName", contactName);
        cv.put("contactNo",contactNo);
        cv.put("contactAddr",contactAddr);
        cv.put("contactPhoto",contactPhoto);
        System.out.println(contactName + contactAddr+contactNo+contactPhoto + "FUCK YOU");

        getWritableDatabase().insert("contactlist_table","contactName",cv);
    }

    //Edit an existing contact
    public void updateContact(String id, String contactName, String contactNo, String contactAddr,byte[] contactPhoto) {

        ContentValues cv = new ContentValues();
        String[] args = {id};
        cv.put("contactName", contactName);
        cv.put("contactNo", contactNo);
        cv.put("contactAddr", contactAddr);
        cv.put("contactPhoto",contactPhoto);

        getWritableDatabase().update("contactlist_table", cv, "_ID = ?", args);
    }




    public String getID(Cursor c) {return (c.getString(0));}
    public String getContactName(Cursor c) {return (c.getString(1));}
    public String getContactNo(Cursor c) {return (c.getString(2));}
    public String getContactAddr(Cursor c) {return (c.getString(3));}
    public byte[] getContactPhoto(Cursor c) {return(c.getBlob(4));}

}
