package com.vfs.pg12alfredo.lostitems;

public class User extends Model {

    private String name;

    // For firebase
    public User() {

    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
