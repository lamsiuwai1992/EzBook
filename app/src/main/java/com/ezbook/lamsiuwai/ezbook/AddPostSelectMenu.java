package com.ezbook.lamsiuwai.ezbook;

/**
 * Created by lamsiuwai on 22/9/2017.
 */

public class AddPostSelectMenu {
    private String menu;
    private String selection;

    public String getMenu() {
        return menu;
    }

    public String getSelection() {
        return selection;
    }

    public AddPostSelectMenu(String menu, String selection) {
        this.menu = menu;
        this.selection = selection;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }
}
