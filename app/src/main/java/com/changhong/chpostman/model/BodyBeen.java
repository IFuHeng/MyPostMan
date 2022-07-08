package com.changhong.chpostman.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BodyBeen implements Parcelable {
    String formData;
    String x_www_form_urlencoded;
    String raw;
    byte raw_type;
    String binary;
    byte type;

    public BodyBeen() {
    }

    protected BodyBeen(Parcel in) {
        formData = in.readString();
        x_www_form_urlencoded = in.readString();
        raw = in.readString();
        raw_type = in.readByte();
        binary = in.readString();
        type = in.readByte();
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public String getX_www_form_urlencoded() {
        return x_www_form_urlencoded;
    }

    public void setX_www_form_urlencoded(String x_www_form_urlencoded) {
        this.x_www_form_urlencoded = x_www_form_urlencoded;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public byte getRawType() {
        return raw_type;
    }

    String[] TYPES_MEDIA = {"text/plain", "application/x-www-form-urlencoded", "application/x-msdownload", "application/javascript", "application/json", "text/html", "application/xml"};

    public String getMediaType() {
        switch (type) {
            case 2:
                return TYPES_MEDIA[1];
            case 4:
                return TYPES_MEDIA[2];
            case 3:
                switch (raw_type) {
                    case 1:
                        return TYPES_MEDIA[3];
                    case 2:
                        return TYPES_MEDIA[4];
                    case 3:
                        return TYPES_MEDIA[5];
                    case 4:
                        return TYPES_MEDIA[6];
                }
            default:// 0 、 1
                return TYPES_MEDIA[0];
        }
    }

    public void setRawType(byte raw_type) {
        this.raw_type = raw_type;
    }

    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    private String getFileContent(String filePath) {

        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(filePath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[128];

            int size;
            while ((size = is.read(buf)) != -1) {
                baos.write(buf, 0, size);
            }

            return new String(baos.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static final Creator<BodyBeen> CREATOR = new Creator<BodyBeen>() {
        @Override
        public BodyBeen createFromParcel(Parcel in) {
            return new BodyBeen(in);
        }

        @Override
        public BodyBeen[] newArray(int size) {
            return new BodyBeen[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(formData);
        dest.writeString(x_www_form_urlencoded);
        dest.writeString(raw);
        dest.writeByte(raw_type);
        dest.writeString(binary);
        dest.writeByte(type);
    }

    @Override
    public String toString() {
        return "BodyBeen{" +
                "formData='" + formData + '\'' +
                ", x_www_form_urlencoded='" + x_www_form_urlencoded + '\'' +
                ", raw='" + raw + '\'' +
                ", raw_type=" + raw_type +
                ", binary='" + binary + '\'' +
                ", type=" + type +
                '}';
    }
}
