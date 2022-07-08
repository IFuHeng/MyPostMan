package com.changhong.chpostman.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ParamBeen implements Parcelable, Comparable {
    boolean isChecked;
    String value;
    String key;

    public ParamBeen() {
    }

    public ParamBeen(boolean isChecked, String key, String value) {
        this.isChecked = isChecked;
        this.key = key;
        this.value = value;
    }

    protected ParamBeen(Parcel in) {
        isChecked = in.readByte() != 0;
        value = in.readString();
        key = in.readString();
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static final Creator<ParamBeen> CREATOR = new Creator<ParamBeen>() {
        @Override
        public ParamBeen createFromParcel(Parcel in) {
            return new ParamBeen(in);
        }

        @Override
        public ParamBeen[] newArray(int size) {
            return new ParamBeen[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeString(value);
        dest.writeString(key);
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof ParamBeen)) {
            return -1;
        }
        ParamBeen oo = (ParamBeen) o;
        if (oo.isChecked != isChecked) {
            return oo.isChecked ? 1 : -1;
        }

        int result = oo.getKey().hashCode() - getKey().hashCode();
        if (result != 0)
            return result;

        result = oo.getValue().hashCode() - getValue().hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "ParamBeen{" +
                "isChecked=" + isChecked +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
