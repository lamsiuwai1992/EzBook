package com.ezbook.lamsiuwai.ezbook;

/**
 * Created by lamsiuwai on 18/9/2017.
 */

public class SubjectImage {

    public SubjectImage() {}
    private String subjectName;
    private String subjectIcon;

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectIcon() {
        return subjectIcon;
    }

    SubjectImage(String subjectName, String subjectIcon) {
        this.subjectName = subjectName;
        this.subjectIcon = subjectIcon;
    }


    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setSubjectIcon(String subjectIcon) {
        this.subjectIcon = subjectIcon;
    }
}
