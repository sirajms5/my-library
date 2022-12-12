package com.sirajsaleem.my_library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

public class NewBook {
    private String bookName;
    private String bookAuthor;
    private String publicationYear;
    private String pagesNum;
    private String mainId;
    private String notes;
    private boolean isFav;
    private String status;
    private String imgCode;
    private String imgSource;
    private String isChecked; //for the recyclerview list edit check
    private boolean isExpanded; //to prevent viewHolder repetition
    private boolean add; //for menu edit button counter and refresh
    private int listId = 0; //for recycler view list id

    public NewBook(String bookName, String bookAuthor, String publicationYear, String pagesNum, String mainId, boolean isFav, String status, String imageCode, String imgSource, String notes) {
        this.setBookName(bookName);
        this.setBookAuthor(bookAuthor);
        this.setPublicationYear(publicationYear);
        this.setPagesNum(pagesNum);
        this.setMainId(mainId);
        this.notes = notes;
        this.setFav(isFav);
        this.setStatus(status);
        this.setBookImg(imageCode);
        this.setImgSource(imgSource);
        this.isChecked = "default";
        this.isExpanded = false;
        this.add = false;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String checked) {
        isChecked = checked;
    }

    public String getImgSource() {
        return imgSource;
    }

    public void setImgSource(String imgSource) {
        this.imgSource = imgSource;
    }

    public Uri getBookUri() {
        return Uri.parse(imgCode);
    }

    public Bitmap getBookBitmap(){
        byte[] encodeByte = android.util.Base64.decode(imgCode, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
    }

    public void setBookImg(String bookImg) {
        this.imgCode = bookImg;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getPagesNum() {
        return pagesNum;
    }

    public void setPagesNum(String pagesNum) {
        this.pagesNum = pagesNum;
    }

    public String getMainId(){
        return mainId;
    }

    public void setMainId(String id) {
        this.mainId = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        this.isFav = fav;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
