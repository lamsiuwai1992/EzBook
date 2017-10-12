package com.ezbook.lamsiuwai.ezbook;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lamsiuwai on 23/9/2017.
 */

public class BookObject {

    private String bookName ;
    private List<AddPostImage> images;
    private String state ;
    private String bookOwner ;
    private int price ;
    private String createTime ;
    private String bookType ;
    private String bookDescrpition;
    private String category;
    private String bookId ;
    private String searchBookName;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookDescrpition() {
        return bookDescrpition;
    }

    public void setBookDescrpition(String bookDescrpition) {
        this.bookDescrpition = bookDescrpition;
    }


    public String getSearchBookName() {
        return searchBookName;
    }

    public void setSearchBookName(String searchBookName) {
        this.searchBookName = searchBookName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getBookOwner() {
        return bookOwner;
    }

    public void setBookOwner(String bookOwner) {
        this.bookOwner = bookOwner;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }


    public List<AddPostImage> getImages() {
        return images;
    }

    public void setImages(List<AddPostImage> images) {
        this.images = images;
    }

    public BookObject(String bookId ,String bookName, List<AddPostImage> images, String bookOwner, int price, String category, String bookType,String bookDescrpition ,String searchBookName) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        this.bookId = bookId;
        this.bookName = bookName;
        this.images = images;
        this.state = "Available";
        this.bookOwner = bookOwner;
        this.price = price;
        this.category = category;
        this.bookType = bookType;
        this.createTime = dateFormat.format(date);
        this.bookDescrpition = bookDescrpition;
        this.searchBookName = searchBookName.toLowerCase();
    }
    public BookObject(){

    }

}
