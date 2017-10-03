package com.ezbook.lamsiuwai.ezbook;

/**
 * Created by lamsiuwai on 2/10/2017.
 */

public class UserObject {
    private String email ;
    private String profileIcon;
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileIcon() {
        return profileIcon;
    }

    public void setProfileIcon(String profileIcon) {
        this.profileIcon = profileIcon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserObject(String email, String profileIcon, String name) {
        this.email = email;
        this.profileIcon = profileIcon;
        this.name = name;
    }

    UserObject(){

    }
}
