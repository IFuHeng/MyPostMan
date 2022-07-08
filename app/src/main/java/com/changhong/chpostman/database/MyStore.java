package com.changhong.chpostman.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class MyStore {
    public final static String AUTHORIY = "com.changhong.chpostman.database.MyProvider";
    public final static String CONTENT_URI_SLASH = "content://" + AUTHORIY + "/";
    public final static String CONTENT_TYPE_HEADER = "vnd.android.cursor.dir/";
    public final static String ENTRY_CONTENT_TYPE_HEADER = "vnd.android.cursor.item/";

    public final static class RequestBeen implements BaseColumns {
        public final static String ENTITY_NAME = "request_been";
//        public final static String ORDER = "order_no";
        public final static String NAME = "name";
        public final static String URL = "url";
        public final static String BODY_BEEN = "body_been";
        public final static String PARAMS = "params";
        public final static String HEADERS = "headers";
        public final static String REQUEST_TYPE = "request_type";
        public final static String INDEX_OF_PHB = "index_of_PHB";
//        public final static String DEFAULT_SORT_ORDER = ORDER;

//        public final static Uri CONTENT_URI = Uri.parse(CONTENT_URI_SLASH + ENTITY_NAME);
//        public final static String CONTENT_TYPE = CONTENT_TYPE_HEADER + ENTITY_NAME;
//        public final static String ENTRY_CONTENT_TYPE = ENTRY_CONTENT_TYPE_HEADER + ENTITY_NAME;

    }

    public static final String sRequestBeenProjection[] = {RequestBeen._ID, RequestBeen.NAME, RequestBeen.URL,
            RequestBeen.BODY_BEEN, RequestBeen.PARAMS, RequestBeen.HEADERS, RequestBeen.REQUEST_TYPE,
            RequestBeen.INDEX_OF_PHB};
}
