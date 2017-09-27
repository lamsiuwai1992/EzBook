package com.ezbook.lamsiuwai.ezbook;

/**
 * Created by lamsiuwai on 28/9/2017.
 */

public class ShowChatListObject {
    private String Email;
    private String ProfileIcon;
    private String Name;

    public ShowChatListObject(String email, String profileIcon, String name) {
        Email = email;
        ProfileIcon = profileIcon;
        Name = name;
    }
    ShowChatListObject(){

    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getProfileIcon() {
        return ProfileIcon;
    }

    public void setProfileIcon(String profileIcon) {
        ProfileIcon = profileIcon;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
