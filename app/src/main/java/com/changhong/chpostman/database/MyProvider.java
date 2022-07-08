package com.changhong.chpostman.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MyProvider extends ContentProvider {
    //private static String TAG="MessageProvider";
    private final static int REQUEST_PARAM_MENU = 1;
    private final static int REQUEST_PARAM_ID = 2;
    private final static int MESSAGES = 3;
    private final static int MESSAGE_ID = 4;
    public MyDBHelper openHelper = null;

    private final static UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(MyStore.AUTHORIY, MyStore.RequestBeen.ENTITY_NAME, REQUEST_PARAM_MENU);
        URI_MATCHER.addURI(MyStore.AUTHORIY, MyStore.RequestBeen.ENTITY_NAME + "/#", REQUEST_PARAM_ID);
        URI_MATCHER.addURI(MyStore.AUTHORIY, "messages", MESSAGES);
        URI_MATCHER.addURI(MyStore.AUTHORIY, "messages/#", MESSAGE_ID);
    }

    static final GetTableAndWhereOutParameter sGetTableAndWhereParam =
            new GetTableAndWhereOutParameter();


    @Override
    public int delete(Uri uri, String userWhere, String[] whereArgs) {
        // TODO Auto-generated method stub
        int count;
        int match = URI_MATCHER.match(uri);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        getTableAndWhere(uri, match, userWhere, sGetTableAndWhereParam);
        count = db.delete(sGetTableAndWhereParam.table, sGetTableAndWhereParam.where, whereArgs);
        return count;
    }

    @Override
    public String getType(Uri uri) {
//        switch (URI_MATCHER.match(uri)) {
//            case REQUEST_PARAM_MENU:
//                return MyStore.RequestBeen.CONTENT_TYPE;
//            case REQUEST_PARAM_ID:
//                return MyStore.RequestBeen.ENTRY_CONTENT_TYPE;
//        }
        throw new IllegalStateException("Unknown URL");
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        Uri newUri = insertInternal(uri, initialValues);
        return newUri;
    }

    private Uri insertInternal(Uri uri, ContentValues initialValues) {
        long rowId;
        Uri newUri = null;

        String table;
        String nullColumnHack;
        SQLiteDatabase db = openHelper.getWritableDatabase();
        // TODO Auto-generated method stub
        switch (URI_MATCHER.match(uri)) {
            case REQUEST_PARAM_MENU:
            case REQUEST_PARAM_ID:
                table = MyDBHelper.TABLE_REQUEST_PARAMS;
                nullColumnHack = MyStore.RequestBeen._ID;
                break;
            default:
                throw new UnsupportedOperationException("Invalid URI " + uri);
        }

        if (table != null) {
            rowId = db.insert(table, nullColumnHack, initialValues);
            if (rowId > 0) {
                newUri = ContentUris.withAppendedId(uri, rowId);
            }
        } else {
            throw new UnsupportedOperationException("Invalid URI " + uri);
        }

        return newUri;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        openHelper = new MyDBHelper(this.getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projectionIn, String selection, String[] selectionArgs,
                        String sort) {
        int table = URI_MATCHER.match(uri);
        SQLiteDatabase db = openHelper.getReadableDatabase();
        SQLiteQueryBuilder sq = new SQLiteQueryBuilder();
        String limit = uri.getQueryParameter("limit");
        String groupBy = null;
        String tables;
        switch (table) {
            case REQUEST_PARAM_MENU:
            case REQUEST_PARAM_ID:
                tables = MyDBHelper.TABLE_REQUEST_PARAMS;
                break;
            default:
                throw new UnsupportedOperationException("Invalid URI " + uri);
        }
        sq.setTables(tables);
        Cursor c = sq.query(db, projectionIn, selection,
                selectionArgs, groupBy, null, sort, limit);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues initialValues, String userWhere, String[] whereArgs) {
        // TODO Auto-generated method stub
        int count;
        int match = URI_MATCHER.match(uri);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        getTableAndWhere(uri, match, userWhere, sGetTableAndWhereParam);
        count = db.update(sGetTableAndWhereParam.table, initialValues, sGetTableAndWhereParam.where, whereArgs);
        return count;
    }

    private static final class GetTableAndWhereOutParameter {
        public String table;
        public String where;
    }

    private void getTableAndWhere(Uri uri, int match, String userWhere, GetTableAndWhereOutParameter out) {
        String where = null;
        switch (match) {
            case REQUEST_PARAM_MENU:
                out.table = MyDBHelper.TABLE_REQUEST_PARAMS;
                break;
            case REQUEST_PARAM_ID:
                out.table = MyDBHelper.TABLE_REQUEST_PARAMS;
                where = "_id=" + uri.getPathSegments().get(1);
                break;
            default:
                throw new UnsupportedOperationException(
                        "Unknown or unsupported URL: " + uri.toString());
        }
        // Add in the user requested WHERE clause, if needed
        if (!TextUtils.isEmpty(userWhere)) {
            if (!TextUtils.isEmpty(where)) {
                out.where = where + " AND (" + userWhere + ")";
            } else {
                out.where = userWhere;
            }
        } else {
            out.where = where;
        }
    }

}
