package com.zzj.networkface;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description:
 *  自定义的http回传请求格式
 *
 * @author zzj
 * @date 2023/2/25
 * @since 1.0.0
 */
public class MyRequest<T extends Parcelable> implements Parcelable {
    /**
     * 返回的编码
     */
    private int code;
    /**
     * 调用的路劲
     */
    private String pathName;
    /**
     * 消息解析格式
     */
    private String msg;
    /**
     * 回传的内容
     */
    private T data;

    protected MyRequest(Parcel in) {
        code = in.readInt();
        pathName = in.readString();
        msg = in.readString();
        String dataStr = in.readString();
        try {
            data = in.readParcelable(Class.forName(dataStr).getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static final Creator<MyRequest> CREATOR = new Creator<MyRequest>() {
        @Override
        public MyRequest createFromParcel(Parcel in) {
            return new MyRequest(in);
        }

        @Override
        public MyRequest[] newArray(int size) {
            return new MyRequest[size];
        }
    };

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(pathName);
        dest.writeString(msg);
        dest.writeParcelable(this.data, flags);
    }

    @Override
    public String toString() {
        return "MyRequest{" +
                "code=" + code +
                ", pathName='" + pathName + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data.toString() +
                '}';
    }
}
