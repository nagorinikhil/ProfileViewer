package com.example.nikhil.profileviewer.POJO;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

    private int id;
    private String name;
    private String gender;
    private int age;
    private String hobbies;
    private String image;

    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
        gender = in.readString();
        age = in.readInt();
        hobbies = in.readString();
        image = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return name;
    }

    public User() {
    }

    public User(int id, String name, String gender, int age, String hobbies) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.hobbies = hobbies;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", hobbies='" + hobbies + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(gender);
        dest.writeInt(age);
        dest.writeString(hobbies);
        dest.writeString(image);
    }
}
