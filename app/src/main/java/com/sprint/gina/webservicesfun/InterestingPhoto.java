package com.sprint.gina.webservicesfun;

public class InterestingPhoto {
    String id;
    String title;
    String dateTaken;
    String photoURL;

    public InterestingPhoto(String id, String title, String dateTaken, String photoURL) {
        this.id = id;
        this.title = title;
        this.dateTaken = dateTaken;
        this.photoURL = photoURL;
    }

    @Override
    public String toString() {
        return "InterestingPhoto{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", dateTaken='" + dateTaken + '\'' +
                ", photoURL='" + photoURL + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}
