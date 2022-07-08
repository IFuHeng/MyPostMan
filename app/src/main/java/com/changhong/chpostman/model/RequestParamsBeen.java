package com.changhong.chpostman.model;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RequestParamsBeen {

    public static final long ROOT_REQUEST_PARAMS_ID = 1;

    Integer id;
    BodyBeen bodyBeen;
    String url;
    List<ParamBeen> params;
    List<ParamBeen> headers;
    String name;
    byte requestType;
    byte indexOfPHB;

    public BodyBeen getBodyBeen() {
        return bodyBeen;
    }

    public String getBodyBeenString() {
        if (bodyBeen == null)
            return null;
        return new Gson().toJson(bodyBeen);
    }

    public void setBodyBeen(BodyBeen bodyBeen) {
        this.bodyBeen = bodyBeen;
    }

    public void setBodyBeen(String bodyBeenString) {
        if (bodyBeenString == null || bodyBeenString.length() == 0)
            this.bodyBeen = null;
        else
            this.bodyBeen = new Gson().fromJson(bodyBeenString, BodyBeen.class);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ParamBeen> getParams() {
        return params;
    }

    public String getParamsString() {
        if (params == null || params.isEmpty())
            return null;
        return new Gson().toJson(params);
    }

    public void setParams(List<ParamBeen> params) {
        this.params = params;
    }

    public void setParams(String paramsString) {
        if (paramsString == null || paramsString.length() == 0) {
            this.params = null;
        } else {
            this.params = turnString2Params(paramsString);
        }
    }

    public List<ParamBeen> getHeaders() {
        return headers;
    }

    public String getHeadersString() {
        if (headers == null || headers.isEmpty())
            return null;
        return new Gson().toJson(headers);
    }

    public void setHeaders(List<ParamBeen> headers) {
        this.headers = headers;
    }

    public void setHeaders(String headersString) {
        if (headersString == null || headersString.length() == 0) {
            this.headers = null;
        } else {
            this.headers = turnString2Params(headersString);
        }
    }

    private List<ParamBeen> turnString2Params(String str) {
        List<ParamBeen> result = new ArrayList<>();
        Gson gson = new Gson();
        try {
            JSONArray jsonObject = new JSONArray(str);
            for (int i = 0; i < jsonObject.length(); i++) {
                result.add(gson.fromJson(jsonObject.getString(i), ParamBeen.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getRequestType() {
        return requestType;
    }

    public void setRequestType(byte requestType) {
        this.requestType = requestType;
    }

    public byte getIndexOfPHB() {
        return indexOfPHB;
    }

    public void setIndexOfPHB(byte indexOfPHB) {
        this.indexOfPHB = indexOfPHB;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RequestParamsBeen{" +
                "id=" + id +
                ", bodyBeen=" + bodyBeen +
                ", url='" + url + '\'' +
                ", params=" + params +
                ", headers=" + headers +
                ", name='" + name + '\'' +
                ", requestType=" + requestType +
                ", indexOfPHB=" + indexOfPHB +
                '}';
    }
}
