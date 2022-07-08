package com.changhong.chpostman.model;

import java.util.List;

public class ResponseBeen {
    long length;
    long headersLength;
    long costTime;
    String msg;
    int code;
    byte[] body;
    boolean isSuccessful;
    List<ParamBeen> headers;
    private List<ParamBeen> cookies;

    public long getHeadersLength() {
        return headersLength;
    }

    public void setHeadersLength(long headersLength) {
        this.headersLength = headersLength;
    }

    public List<ParamBeen> getHeaders() {
        return headers;
    }

    public void setHeaders(List<ParamBeen> headers) {
        this.headers = headers;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBodyString() {
        if (body == null)
            return null;
        else
            return new String(body);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public List<ParamBeen> getCookies() {
        return cookies;
    }

    public void setCookies(List<ParamBeen> cookies) {
        this.cookies = cookies;
    }
}
