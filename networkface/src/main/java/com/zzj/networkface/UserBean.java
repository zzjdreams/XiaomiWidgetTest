package com.zzj.networkface;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Description:
 *
 * @author zzj
 * @date 2023/2/23
 * @since 1.0.0
 */
public class UserBean implements Parcelable {
    private String name;
    private int age;
//    @ParcelProperty("time")
    private long registerTime;

    public UserBean(String name, int age, long registerTime) {
        this.name = name;
        this.age = age;
        this.registerTime = registerTime;
    }

    protected UserBean(Parcel in) {
        name = in.readString();
        age = in.readInt();
        registerTime = in.readLong();
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeLong(registerTime);
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", registerTime=" + registerTime +
                '}';
    }
}
