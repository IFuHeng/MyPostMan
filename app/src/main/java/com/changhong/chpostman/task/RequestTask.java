package com.changhong.chpostman.task;

import android.util.Log;

import com.changhong.chpostman.BuildConfig;
import com.changhong.chpostman.model.BodyBeen;
import com.changhong.chpostman.model.ParamBeen;
import com.changhong.chpostman.model.RequestParamsBeen;
import com.changhong.chpostman.model.ResponseBeen;
import com.changhong.chpostman.ui.fragment.ListContentFragment;
import com.changhong.chpostman.utils.FileUtils;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kotlin.Pair;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestTask extends GenericTask {

    public RequestTask execute(RequestParamsBeen been, String[] arr_method, TaskListener<ResponseBeen> responseTaskListener) {
        TaskParams taskParams = new TaskParams();
        taskParams.put("been", been);
        taskParams.put("arr_method", arr_method);
        setListener(responseTaskListener);
        executeOnExecutor(THREAD_POOL_EXECUTOR, taskParams);
        return this;
    }

    @Override
    protected TaskResult _doInBackground(TaskParams... params) {
        long startTime = System.currentTimeMillis();
        RequestParamsBeen been = params[0].get("been");
        String[] ARR_METHOD = params[0].get("arr_method");

        java.net.CookieManager manager = new java.net.CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        Log.d(getClass().getSimpleName(), "====~url = " + been.toString());
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        try {
            Request request = createRequest(been, ARR_METHOD);
            Response response = client.newCall(request).execute();

            ResponseBeen result = new ResponseBeen();
            result.setMsg(response.message());
            result.setCode(response.code());
            byte[] data = response.body().bytes();
            result.setBody(data);
            result.setCostTime(System.currentTimeMillis() - startTime);
            long length = response.body().contentLength();
            result.setLength(length <= 0 ? data.length : length);
            result.setSuccessful(response.isSuccessful());
            {
                Iterator<Pair<String, String>> iterator = response.headers().iterator();
                ArrayList<ParamBeen> headers = new ArrayList<>();
                ArrayList<ParamBeen> cookies = new ArrayList<>();
                while (iterator.hasNext()) {
                    Pair<String, String> next = iterator.next();
                    headers.add(new ParamBeen(true, next.getFirst(), next.getSecond()));
                    if ("Set-Cookie".equals(next.getFirst())) {
                        String value = next.getSecond();
                        int indexOfEqual = value.indexOf('=');
                        if (indexOfEqual == -1)
                            continue;
                        cookies.add(new ParamBeen(false, value.substring(0, indexOfEqual), value.substring(indexOfEqual + 1)));
                    }
                }
                result.setHeaders(headers);
                result.setCookies(cookies);
                result.setHeadersLength(response.headers().byteCount());
            }
            {
                List<String> cookies = response.headers().values("Set-Cookie");
                Log.d(getClass().getSimpleName(), "====~" + cookies.toString());
            }
            publishProgress(result);

        } catch (IOException e) {
            e.printStackTrace();
            setException(e);
            return TaskResult.FAILED;
        }

        return TaskResult.OK;
    }

    private Request createRequest(RequestParamsBeen been, String[] ARR_METHOD) throws IOException {
        String method = ARR_METHOD[been.getRequestType()];
        RequestBody body = null;
        if (!method.equals("GET")
                && !method.equals("COPY")
                && !method.equals("HEAD")
                && !method.equals("LINK")
                && !method.equals("UNLINK")
                && !method.equals("PURGE")
                && !method.equals("UNLOCK")
                && !method.equals("VIEW"))
            body = createRequestBodyByBodyType(been.getBodyBeen());

        Request.Builder builder = new Request.Builder()
                .url(been.getUrl())
                .method(method, body);
        if (been.getHeaders() != null && !been.getHeaders().isEmpty()) {
            boolean hasContnetType = false;
            for (ParamBeen header : been.getHeaders()) {
                if ("ContentType".equals(header.getKey())) {
                    hasContnetType = true;
                    builder.addHeader(header.getKey(), been.getBodyBeen().getMediaType());
                } else
                    builder.addHeader(header.getKey(), header.getValue());
            }
            if (!hasContnetType)
                builder.addHeader("ContentType", been.getBodyBeen().getMediaType());
        }
//        builder.addHeader("User-Agent", BuildConfig.APPLICATION_ID + " V" + BuildConfig.VERSION_NAME);
//        builder.addHeader("Accept", "*/*");
//        builder.addHeader("Connection", "keep-alive");
//        builder.addHeader("Accept-Encoding", "gzip, deflate, br");
        Request request = builder.build();
        return request;
    }

    private RequestBody createRequestBodyByBodyType(BodyBeen been) throws IOException {
        if (been == null)
            return null;

        RequestBody body;
        switch (been.getType()) {
            case 1: {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                List<ParamBeen> temp = ListContentFragment.turnString2ParamList(been.getFormData());
                if (temp != null && !temp.isEmpty())
                    for (ParamBeen paramBeen : temp) {
                        builder.addFormDataPart(paramBeen.getKey(), paramBeen.getValue());
                    }
                body = builder.build();
            }
            break;
            case 2:
                body = RequestBody.create(been.getX_www_form_urlencoded(), MediaType.parse(been.getMediaType()));
                break;
            case 3:
                body = RequestBody.create(been.getRaw(), MediaType.parse(been.getMediaType()));
                break;
            case 4:
                body = RequestBody.create(FileUtils.readFile(been.getBinary()), MediaType.parse(been.getMediaType()));
                break;
            default:
                body = RequestBody.create("", MediaType.parse(been.getMediaType()));
                break;
        }
        return body;
    }
}
