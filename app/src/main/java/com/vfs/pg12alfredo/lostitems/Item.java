package com.vfs.pg12alfredo.lostitems;

import com.google.firebase.firestore.GeoPoint;

public class Item {

    private String uid;
    private String name;
    private GeoPoint location;
    private boolean found;
    private String description;
    private String owner;
    private String image;

    public Item(String uid, String name, GeoPoint location, boolean found, String description, String owner, String image) {
        this.uid = uid;
        this.name = name;
        this.location = location;
        this.found = found;
        this.description = description;
        this.owner = owner;
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
