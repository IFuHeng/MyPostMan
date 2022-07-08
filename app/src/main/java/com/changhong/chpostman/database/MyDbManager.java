package com.changhong.chpostman.database;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.changhong.chpostman.model.RequestParamsBeen;

import java.util.ArrayList;

public class MyDbManager {
    ContentResolver cr;
    // private static String TAG="MessageManager";
    private static MyDbManager instance = null;

    private MyDbManager(ContentResolver cr) {
        super();
        this.cr = cr;
    }

    public static MyDbManager getInstance(ContentResolver cr) {
        if (instance == null)
            instance = new MyDbManager(cr);
        return instance;
    }

    public void startQueryMenu(AsyncQueryHandler queryHandler, int msgWhat, String name) {
        String where = MyStore.RequestBeen.NAME + "==" + name;
        queryHandler.startQuery(msgWhat, where, null, MyStore.sRequestBeenProjection, where, null, null);
    }

    public int getRowLength() {
        int count = 0;
        Cursor cursor = cr.query(null, MyStore.sRequestBeenProjection, null, null, null);
        if (cursor != null)
            count = cursor.getCount();
        return count;
    }

    public ArrayList<RequestParamsBeen> queryAll() {
        ArrayList<RequestParamsBeen> businessMenus = new ArrayList();
        Cursor cursor = cr.query(null, MyStore.sRequestBeenProjection, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            RequestParamsBeen requestParamBeen = cursorToRequestParamsBeen(cursor);
            businessMenus.add(requestParamBeen);
        }
        return businessMenus;
    }

    public RequestParamsBeen queryRowById(long id) {
        StringBuilder where = new StringBuilder();
        where.append(MyStore.RequestBeen._ID + "==" + "'" + id + "'");
        Cursor cursor = cr.query(null, MyStore.sRequestBeenProjection, where.toString(), null, null);
        RequestParamsBeen requestParamBeen = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            requestParamBeen = cursorToRequestParamsBeen(cursor);
        }
        return requestParamBeen;
    }

    public RequestParamsBeen queryRowByName(String name) {
        StringBuilder where = new StringBuilder();
        where.append(MyStore.RequestBeen.NAME).append(" like \'%" + name + "%\'");

        Cursor cursor = cr.query(null, MyStore.sRequestBeenProjection, where.toString(), null, null);
        RequestParamsBeen requestParamBeen = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            requestParamBeen = cursorToRequestParamsBeen(cursor);
        }
        cursor.close();
        return requestParamBeen;
    }

    public ArrayList<RequestParamsBeen> cursorToMenu(Cursor cursor) {
        ArrayList<RequestParamsBeen> messages = new ArrayList<RequestParamsBeen>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            RequestParamsBeen requestParamsBeen = cursorToRequestParamsBeen(cursor);
            messages.add(requestParamsBeen);
        }
        return messages;
    }

    public synchronized void recordRow(RequestParamsBeen requestParamBeen) {
        ContentValues values = turnRequestParamsBeenToContentValue(requestParamBeen);
        cr.insert(null, values);
    }

    public void recordAll(ArrayList<RequestParamsBeen> datas) {
        if (datas == null)
            return;
        for (RequestParamsBeen requestParamsBeen : datas) {
            recordRow(requestParamsBeen);
        }
    }

    public synchronized void updateRow(RequestParamsBeen requestParamBeen) {
        StringBuilder where = new StringBuilder();
        where.append(MyStore.RequestBeen._ID + "==" + requestParamBeen.getId());
        ContentValues values = turnRequestParamsBeenToContentValue(requestParamBeen);
        cr.update(null, values, where.toString(), null);
    }

    public synchronized void deleteRow(RequestParamsBeen requestParamBeen) {
        StringBuilder where = new StringBuilder();
        where.append(MyStore.RequestBeen._ID + "==" + requestParamBeen.getId());
        cr.delete(null, where.toString(), null);
    }

    public synchronized void deleteAllRow() {
        cr.delete(null, null, null);
    }

    public synchronized void startDeleteRow(AsyncQueryHandler handler, int token, RequestParamsBeen requestParamBeen) {
        StringBuilder where = new StringBuilder();
        where.append(MyStore.RequestBeen._ID + "==" + requestParamBeen.getId());
        handler.startDelete(token, null, null, where.toString(), null);
    }

    public synchronized void startDeleteAll(AsyncQueryHandler handler, int token) {
        handler.startDelete(token, null, null, null, null);
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
        String name = been.getName();
        long id = been.getId();
        value.put(MyStore.RequestBeen._ID, id);
        int indexOfPHB = been.getIndexOfPHB();
        value.put(MyStore.RequestBeen.INDEX_OF_PHB, indexOfPHB);
        int requestType = been.getRequestType();
        value.put(MyStore.RequestBeen.REQUEST_TYPE, requestType);
        if (been.getName() != null)
            value.put(MyStore.RequestBeen.NAME, name);
        String headersString = been.getHeadersString();
        if (headersString != null)
            value.put(MyStore.RequestBeen.HEADERS, headersString);
        String paramsString = been.getParamsString();
        if (paramsString != null)
            value.put(MyStore.RequestBeen.PARAMS, paramsString);
        String bodyBeen = been.getBodyBeenString();
        if (bodyBeen != null)
            value.put(MyStore.RequestBeen.BODY_BEEN, bodyBeen);
        String url = been.getUrl();
        if (url != null)
            value.put(MyStore.RequestBeen.URL, url);

        return value;
    }

}
