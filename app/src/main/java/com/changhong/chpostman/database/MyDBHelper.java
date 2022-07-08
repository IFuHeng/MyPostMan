package com.changhong.chpostman.database;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.changhong.chpostman.model.RequestParamsBeen;

import java.util.ArrayList;

public class MyDBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "my_postman.db";
    private final static int VERSION = 1;
    public final static String TABLE_REQUEST_PARAMS = "table_request_params";

    public MyDBHelper(Context context, String name,
                      CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDBHelper(Context context) {
        this(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_REQUEST_PARAMS + "(" +
                MyStore.RequestBeen._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MyStore.RequestBeen.INDEX_OF_PHB + " INTEGER DEFAULT 0," +
                MyStore.RequestBeen.REQUEST_TYPE + " INTEGER DEFAULT 0," +
                MyStore.RequestBeen.NAME + " TEXT," +
                MyStore.RequestBeen.PARAMS + " TEXT," +
                MyStore.RequestBeen.HEADERS + " TEXT," +
                MyStore.RequestBeen.BODY_BEEN + " TEXT," +
                MyStore.RequestBeen.URL + " TEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Kills the table and existing data
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUEST_PARAMS);
            // Recreates the database with a new version
            onCreate(db);
        }
    }

    public void startQueryMenu(AsyncQueryHandler queryHandler, int msgWhat, String name) {
        String where = MyStore.RequestBeen.NAME + "==" + name;
        queryHandler.startQuery(msgWhat, where, null, MyStore.sRequestBeenProjection, where, null, null);
    }

    public synchronized void deleteRow(RequestParamsBeen requestParamBeen) {
        getWritableDatabase().delete(TABLE_REQUEST_PARAMS, MyStore.RequestBeen._ID + "=?", new String[]{requestParamBeen.getId().toString()});
    }

    public synchronized void deleteAllRow() {
        getWritableDatabase().delete(TABLE_REQUEST_PARAMS, null, null);
    }

    public int getRowLength() {
        int count = 0;
        Cursor cursor = getReadableDatabase().query(TABLE_REQUEST_PARAMS, MyStore.sRequestBeenProjection, null, null, null, null, null);
        if (cursor != null)
            count = cursor.getCount();
        return count;
    }

    public ArrayList<RequestParamsBeen> queryAll() {
        ArrayList<RequestParamsBeen> businessMenus = new ArrayList();
        Cursor cursor = getReadableDatabase().query(TABLE_REQUEST_PARAMS, MyStore.sRequestBeenProjection, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            RequestParamsBeen requestParamBeen = cursorToRequestParamsBeen(cursor);
            businessMenus.add(requestParamBeen);
        }
        return businessMenus;
    }

    public RequestParamsBeen cursorToRequestParamsBeen(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(MyStore.RequestBeen._ID));
        String name = cursor.getString(cursor.getColumnIndex(MyStore.RequestBeen.NAME));
        String url = cursor.getString(cursor.getColumnIndex(MyStore.RequestBeen.URL));
        byte indexOfPHB = (byte) cursor.getInt(cursor.getColumnIndex(MyStore.RequestBeen.INDEX_OF_PHB));
        String bodyBeenString = cursor.getString(cursor.getColumnIndex(MyStore.RequestBeen.BODY_BEEN));
        String headersString = cursor.getString(cursor.getColumnIndex(MyStore.RequestBeen.HEADERS));
        String paramsString = cursor.getString(cursor.getColumnIndex(MyStore.RequestBeen.PARAMS));
        byte requestType = (byte) cursor.getInt(cursor.getColumnIndex(MyStore.RequestBeen.REQUEST_TYPE));

        RequestParamsBeen been = new RequestParamsBeen();
        been.setId(id);
        been.setName(name);
        been.setUrl(url);
        been.setIndexOfPHB(indexOfPHB);
        been.setRequestType(requestType);
        been.setHeaders(headersString);
        been.setParams(paramsString);
        been.setBodyBeen(bodyBeenString);
        return been;
    }

    private ContentValues turnRequestParamsBeenToContentValue(RequestParamsBeen been) {
        ContentValues value = new ContentValues();
        if (been.getId() != null)
            value.put(MyStore.RequestBeen._ID, been.getId());
        value.put(MyStore.RequestBeen.INDEX_OF_PHB, been.getIndexOfPHB());
        value.put(MyStore.RequestBeen.REQUEST_TYPE, been.getRequestType());
        if (been.getName() != null)
            value.put(MyStore.RequestBeen.NAME, been.getName());
        String headersString = been.getHeadersString();
        if (headersString != null)
            value.put(MyStore.RequestBeen.HEADERS, headersString);
        String paramsString = been.getParamsString();
        if (paramsString != null)
            value.put(MyStore.RequestBeen.PARAMS, paramsString);
        String bodyBeen = been.getBodyBeenString();
        if (bodyBeen != null)
            value.put(MyStore.RequestBeen.BODY_BEEN, bodyBeen);
        if (been.getUrl() != null)
            value.put(MyStore.RequestBeen.URL, been.getUrl());

        return value;
    }

    public RequestParamsBeen queryRowByName(String name) {
        Cursor cursor = getReadableDatabase().query(TABLE_REQUEST_PARAMS, MyStore.sRequestBeenProjection, MyStore.RequestBeen.NAME + "=?", new String[]{name}, null, null, null);
        RequestParamsBeen requestParamBeen = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            requestParamBeen = cursorToRequestParamsBeen(cursor);
        }
        cursor.close();
        return requestParamBeen;
    }

    public synchronized void recordRow(RequestParamsBeen requestParamBeen) {
        ContentValues values = turnRequestParamsBeenToContentValue(requestParamBeen);
        if (requestParamBeen.getId() == null) {
            long newId = getWritableDatabase().insert(TABLE_REQUEST_PARAMS, null, values);
            requestParamBeen.setId((int) newId);
        } else {
            getWritableDatabase().update(TABLE_REQUEST_PARAMS, values, MyStore.RequestBeen._ID + "=?", new String[]{requestParamBeen.getId().toString()});
        }
    }
}
