package devilseye.android.timetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import devilseye.android.timetracker.model.*;

public class DBHelper extends SQLiteOpenHelper {
    //db settings
    public static final String DATABASE_NAME = "timeTracker";
    public static final int DATABASE_VERSION = 1;
    public static final String COLUMN_ID = "id";
    //table category
    public static final String TABLE_CATEGORY = "category";
    public static final String COLUMN_CATEGORY_NAME = "name";
    //table record
    public static final String TABLE_RECORD = "record";
    public static final String COLUMN_RECORD_TIME_START = "time_start";
    public static final String COLUMN_RECORD_TIME_END = "time_end";
    public static final String COLUMN_RECORD_CATEGORY_ID = "category_id";
    public static final String COLUMN_RECORD_TIME_MINUTES = "time_minutes";
    public static final String COLUMN_RECORD_DESCRIPTION = "description";
    //table photo
    public static final String TABLE_PHOTO = "photo";
    public static final String COLUMN_PHOTO_RECORD_ID = "record_id";
    public static final String COLUMN_PHOTO_IMAGE = "image";
    //create table category
    public static final String DATABASE_CREATE_CATEGORY = "create table "
            + TABLE_CATEGORY + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CATEGORY_NAME + " string );";
    //create table record
    public static final String DATABASE_CREATE_RECORD = "create table "
            + TABLE_RECORD + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_RECORD_TIME_START + " datetime, "
            + COLUMN_RECORD_TIME_END + " datetime, "
            + COLUMN_RECORD_CATEGORY_ID + " integer, "
            + COLUMN_RECORD_TIME_MINUTES + " integer, "
            + COLUMN_RECORD_DESCRIPTION + " text, "
            + "FOREIGN KEY ("+COLUMN_RECORD_CATEGORY_ID+") REFERENCES "+TABLE_CATEGORY+"("+COLUMN_ID+") ON DELETE CASCADE);";
    //create table photo
    public static final String DATABASE_CREATE_PHOTO = "create table "
            + TABLE_PHOTO + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PHOTO_RECORD_ID + " integer, "
            + COLUMN_PHOTO_IMAGE + " blob, "
            + "FOREIGN KEY ("+COLUMN_PHOTO_RECORD_ID+") REFERENCES "+TABLE_RECORD+"("+COLUMN_ID+") ON DELETE CASCADE);";
    //delete tables
    public static final String DATABASE_DROP_PHOTO = "DROP TABLE IF EXISTS " + TABLE_PHOTO;
    public static final String DATABASE_DROP_RECORD = "DROP TABLE IF EXISTS " + TABLE_RECORD;
    public static final String DATABASE_DROP_CATEGORY = "DROP TABLE IF EXISTS " + TABLE_CATEGORY;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_CATEGORY);
        db.execSQL(DATABASE_CREATE_RECORD);
        db.execSQL(DATABASE_CREATE_PHOTO);
        ContentValues values=new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, "Work");
        db.insert(TABLE_CATEGORY, null, values);
        values=new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, "Food");
        db.insert(TABLE_CATEGORY, null, values);
        values=new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, "Rest");
        db.insert(TABLE_CATEGORY, null, values);
        values=new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, "Cleaning");
        db.insert(TABLE_CATEGORY, null, values);
        values=new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, "Sleep");
        db.insert(TABLE_CATEGORY, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATABASE_DROP_PHOTO);
        db.execSQL(DATABASE_DROP_RECORD);
        db.execSQL(DATABASE_DROP_CATEGORY);
        onCreate(db);
    }

    @Override
    public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATABASE_DROP_PHOTO);
        db.execSQL(DATABASE_DROP_RECORD);
        db.execSQL(DATABASE_DROP_CATEGORY);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    public void addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, category.get_name());

        db.insert(TABLE_CATEGORY, null, values);

    }

    public Category getCategory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORY, new String[] { COLUMN_ID,
                        COLUMN_CATEGORY_NAME}, COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
            return new Category(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1));
        } else {
            return null;
        }
    }

    public void deleteCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORY, COLUMN_ID + " = ?",
                new String[]{String.valueOf(category.get_id())});

    }

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<Category>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.set_id(Integer.parseInt(cursor.getString(0)));
                category.set_name(cursor.getString(1));

                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        return categoryList;
    }

    public LinkedHashMap<String,Long> getPopularCategories(String dateOne, String dateTwo) {
        LinkedHashMap<String,Long> hashMap=new LinkedHashMap<String, Long>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{COLUMN_RECORD_CATEGORY_ID+", count("+COLUMN_RECORD_CATEGORY_ID+") as counter"};
        String orderBy = "counter DESC";
        String groupBy = COLUMN_RECORD_CATEGORY_ID;
        String selection = COLUMN_RECORD_TIME_START+" BETWEEN ? AND ?";
        String[] selectionArgs = new String[]{dateOne, dateTwo};
        Cursor cursor = db.query(TABLE_RECORD, columns, selection, selectionArgs, groupBy, null, orderBy, "5");

        if (cursor.moveToFirst()) {
            do {
                hashMap.put(getCategory(cursor.getInt(0)).get_name(), cursor.getLong(1));
            } while (cursor.moveToNext());
        }
        return hashMap;
    }

    public LinkedHashMap<String,Long> getLargeCategories(String dateOne, String dateTwo) {

        LinkedHashMap<String,Long> hashMap=new LinkedHashMap<String, Long>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = new String[]{COLUMN_RECORD_CATEGORY_ID+",sum("+COLUMN_RECORD_TIME_MINUTES+") as adder"};
        String orderBy = "adder DESC";
        String groupBy = COLUMN_RECORD_CATEGORY_ID;
        String selection = COLUMN_RECORD_TIME_START+" BETWEEN ? AND ?";
        String[] selectionArgs = new String[]{dateOne, dateTwo};
        Cursor cursor=db.query(TABLE_RECORD, columns, selection, selectionArgs, groupBy, null, orderBy, "5");

        if (cursor.moveToFirst()) {
            do {
                hashMap.put(getCategory(cursor.getInt(0)).get_name(),cursor.getLong(1));
            } while (cursor.moveToNext());
        }
        return hashMap;
    }

    public LinkedHashMap<Integer,Long> getTimeOfCategories(int[] ids, String dateOne, String dateTwo) {
        if (ids!=null && ids.length>0) {
            LinkedHashMap<Integer, Long> hashMap = new LinkedHashMap<Integer, Long>();
            String selectQuery = "SELECT " + "SUM(" + TABLE_RECORD + "." + COLUMN_RECORD_TIME_MINUTES + "), "
                    + TABLE_CATEGORY + "." + COLUMN_ID
                    + " FROM " + TABLE_RECORD + " LEFT OUTER JOIN " + TABLE_CATEGORY
                    + " ON " + TABLE_RECORD + "." + COLUMN_RECORD_CATEGORY_ID + "=" + TABLE_CATEGORY + "." + COLUMN_ID
                    + " WHERE " + TABLE_RECORD + "." + COLUMN_RECORD_CATEGORY_ID + " IN (";
            for (int id : ids) {
                selectQuery += id;
                if (id != ids[ids.length - 1]) {
                    selectQuery += ", ";
                }
            }
            selectQuery += ") AND " +TABLE_RECORD+"."+COLUMN_RECORD_TIME_START+" BETWEEN \'"+dateOne+"\' AND \'"+dateTwo+"\'"
                    + " GROUP BY " + TABLE_CATEGORY + "." + COLUMN_ID
                    + " ORDER BY 1 DESC";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    hashMap.put(cursor.getInt(1), cursor.getLong(0));
                } while (cursor.moveToNext());
            }
            return hashMap;
        } else
            return null;
    }

    public LinkedHashMap<Integer,Long> getTimeOfCategories(String dateOne, String dateTwo) {
        LinkedHashMap<Integer, Long> hashMap = new LinkedHashMap<Integer, Long>();
        String selectQuery = "SELECT " + "SUM(" + TABLE_RECORD + "." + COLUMN_RECORD_TIME_MINUTES + "), "
                + TABLE_CATEGORY + "." + COLUMN_ID
                + " FROM " + TABLE_RECORD + " LEFT OUTER JOIN " + TABLE_CATEGORY
                + " ON " + TABLE_RECORD + "." + COLUMN_RECORD_CATEGORY_ID + "=" + TABLE_CATEGORY + "." + COLUMN_ID
                + " WHERE " + TABLE_RECORD + "." + COLUMN_RECORD_TIME_START + " BETWEEN \'" + dateOne + "\' AND \'" + dateTwo + "\'"
                + " GROUP BY " + TABLE_CATEGORY + "." + COLUMN_ID
                + " ORDER BY 1 DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                hashMap.put(cursor.getInt(1), cursor.getLong(0));
            } while (cursor.moveToNext());
        }
        return hashMap;
    }

    public void addRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        String timeStart=Record.getStringDateTime(record.get_timeStart());
        String timeEnd=Record.getStringDateTime(record.get_timeEnd());
        values.put(COLUMN_RECORD_TIME_START, timeStart);
        values.put(COLUMN_RECORD_TIME_END, timeEnd);
        values.put(COLUMN_RECORD_DESCRIPTION, record.get_description());
        values.put(COLUMN_RECORD_TIME_MINUTES, record.get_timeMinutes());
        values.put(COLUMN_RECORD_CATEGORY_ID, record.get_category().get_id());

        db.insert(TABLE_RECORD, null, values);

    }

    public Record getRecord(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECORD, new String[]{
                        COLUMN_ID,
                        COLUMN_RECORD_TIME_START,
                        COLUMN_RECORD_TIME_END,
                        COLUMN_RECORD_TIME_MINUTES,
                        COLUMN_RECORD_DESCRIPTION,
                        COLUMN_RECORD_CATEGORY_ID},
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            return new Record(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3),
                    cursor.getString(4),
                    getCategory(Integer.parseInt(cursor.getString(5))));
        } else {
            return null;
        }
    }

    public void deleteRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORD, COLUMN_ID + " = ?",
                new String[]{String.valueOf(record.get_id())});

    }

    public List<Record> getAllRecords() {
        List<Record> recordList = new ArrayList<Record>();
        String selectQuery = "SELECT  * FROM " + TABLE_RECORD + " ORDER BY "+COLUMN_RECORD_TIME_START +" DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Record record = new Record(
                        Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getLong(4),
                        cursor.getString(5),
                        getCategory(Integer.parseInt(cursor.getString(3))));

                recordList.add(record);
            } while (cursor.moveToNext());
        }
        return recordList;
    }

    public void addPhoto(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PHOTO_IMAGE, photo.get_image());
        values.put(COLUMN_PHOTO_RECORD_ID, photo.get_record().get_id());

        db.insert(TABLE_PHOTO, null, values);
    }

    public Photo getPhoto(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PHOTO, new String[] { COLUMN_ID,
                        COLUMN_PHOTO_IMAGE, COLUMN_PHOTO_RECORD_ID}, COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            return new Photo(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1).getBytes(), getRecord(Integer.parseInt(cursor.getString(2))));
        } else {
            return null;
        }
    }

    public void deletePhoto(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PHOTO, COLUMN_ID + " = ?",
                new String[]{String.valueOf(photo.get_id())});

    }

    public List<Photo> getAllPhotos() {
        List<Photo> photoList = new ArrayList<Photo>();
        String selectQuery = "SELECT * FROM " + TABLE_PHOTO;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Photo photo = new Photo();
                photo.set_id(Integer.parseInt(cursor.getString(0)));
                photo.set_record(getRecord(Integer.parseInt(cursor.getString(1))));
                photo.set_image(cursor.getBlob(2));

                photoList.add(photo);
            } while (cursor.moveToNext());
        }

        return photoList;
    }

    public List<Photo> getRecordPhotos(int record_id) {
        List<Photo> photoList = new ArrayList<Photo>();
        String selectQuery = "SELECT  * FROM " + TABLE_PHOTO + " WHERE "+ COLUMN_PHOTO_RECORD_ID + "="+record_id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Photo photo = new Photo();
                photo.set_id(cursor.getInt(0));
                photo.set_record(getRecord(Integer.parseInt(cursor.getString(1))));
                photo.set_image(cursor.getBlob(2));

                photoList.add(photo);
            } while (cursor.moveToNext());
        }

        return photoList;
    }
}
