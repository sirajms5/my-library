package com.sirajsaleem.my_library;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BooksArray {

    private static BooksArray instance;
    private final SharedPreferences sharedPreferences;
    private final static String ALL_BOOKS_KEY = "allBooks";
    private final static String CURRENTLY_READING_BOOKS_KEY = "currentlyReadingBooks";
    private final static String ALREADY_READ_BOOKS_KEY = "alreadyReadBooks";
    private final static String MY_WISH_LIST_BOOKS_KEY = "myWishlistBooks";
    private final static String MY_FAVORITE_BOOKS_KEY = "myFavoriteBooks";
    private final static String GALLERY_PERMISSION_KEY = "gallery";
    private final static String CAMERA_PERMISSION_KEY = "camera";
    private final static String SEARCH_RESULTS_KEY = "searchResults";
    private final static String SORT_BY_KEY = "sortBy";
    private final static String FIREBASE_ANALYTICS_PERMISSION_KEY = "firebaseAnalytics";

    private BooksArray(Context context) {

        sharedPreferences = context.getSharedPreferences("db", Context.MODE_PRIVATE);

        if(!getPermission("firebaseAnalytics")){
            saveNewPermissions(FIREBASE_ANALYTICS_PERMISSION_KEY);
        }

        if(!getPermission("gallery")){
            saveNewPermissions(GALLERY_PERMISSION_KEY);
        }

        if(!getPermission("camera")){
            saveNewPermissions(CAMERA_PERMISSION_KEY);
        }

        if(getSortBy() == null){
            newSortBy();
        }

        if(getAllBooks() == null){
            saveNewData(ALL_BOOKS_KEY);
        }
        if(getCurrentlyReadingBooks() == null){
            saveNewData(CURRENTLY_READING_BOOKS_KEY);
        }
        if(getAlreadyReadBooks() == null){
            saveNewData(ALREADY_READ_BOOKS_KEY);
        }
        if(getMyWishlistBooks() == null){
            saveNewData(MY_WISH_LIST_BOOKS_KEY);
        }
        if(getMyFavoriteBooks() == null){
            saveNewData(MY_FAVORITE_BOOKS_KEY);
        }

        if(getSearchResults() == null){
            saveNewData(SEARCH_RESULTS_KEY);
        }
    }

    private void newSortBy() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SORT_BY_KEY, "dateAdded");
        editor.apply();
    }

    public void updateSortBy(String sortBy){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SORT_BY_KEY);
        switch(sortBy){
            case "a-z":
                editor.putString(SORT_BY_KEY, "a-z");
                break;
            case "z-a":
                editor.putString(SORT_BY_KEY, "z-a");
                break;
            default:
                editor.putString(SORT_BY_KEY, "dateAdded");
                break;
        }
        editor.apply();
    }

    public String getSortBy(){
        return sharedPreferences.getString(SORT_BY_KEY, null);
    }

    public boolean getPermission(String identifier) {
        switch(identifier){
            case "gallery":
                return sharedPreferences.getBoolean(GALLERY_PERMISSION_KEY, false);
            case "camera":
                return sharedPreferences.getBoolean(CAMERA_PERMISSION_KEY, false);
            case "firebaseAnalytics":
                return sharedPreferences.getBoolean(FIREBASE_ANALYTICS_PERMISSION_KEY, false);
            default:
                return false;
        }
    }

    public void grantPermissions(String identifier, boolean permission){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch(identifier){
            case "gallery":
                editor.remove(GALLERY_PERMISSION_KEY);
                editor.putBoolean(GALLERY_PERMISSION_KEY, permission);
                break;
            case "camera":
                editor.remove(CAMERA_PERMISSION_KEY);
                editor.putBoolean(CAMERA_PERMISSION_KEY, permission);
                break;
            case "firebaseAnalytics":
                editor.remove(FIREBASE_ANALYTICS_PERMISSION_KEY);
                editor.putBoolean(FIREBASE_ANALYTICS_PERMISSION_KEY, permission);
        }
        editor.apply();
    }



    public static BooksArray getInstance(Context context){
        if(instance == null){
            return instance = new BooksArray(context);
        } else {
            return instance;
        }
    }

    public ArrayList<NewBook> getAllBooks() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<NewBook>>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(ALL_BOOKS_KEY, null), type);
    }

    public ArrayList<NewBook> getCurrentlyReadingBooks() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<NewBook>>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(CURRENTLY_READING_BOOKS_KEY, null), type);
    }

    public ArrayList<NewBook> getAlreadyReadBooks() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<NewBook>>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(ALREADY_READ_BOOKS_KEY, null), type);
    }

    public ArrayList<NewBook> getMyWishlistBooks() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<NewBook>>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(MY_WISH_LIST_BOOKS_KEY, null), type);
    }

    public ArrayList<NewBook> getMyFavoriteBooks() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<NewBook>>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(MY_FAVORITE_BOOKS_KEY, null), type);
    }

    public ArrayList<NewBook> getSearchResults(){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<NewBook>>(){}.getType();
        return gson.fromJson(sharedPreferences.getString(SEARCH_RESULTS_KEY, null), type);
    }

    public void addToAllBooks(NewBook incomingBook){
        ArrayList<NewBook> books = getAllBooks();
        if(books != null){
            if(books.add(incomingBook)) {
                updateData(ALL_BOOKS_KEY, books);
            }
        }
    }

    public void addToCurrentlyReadingBooks(NewBook incomingBook){
        ArrayList<NewBook> books = getCurrentlyReadingBooks();
        if(books != null){
            if(books.add(incomingBook)) {
                updateData(CURRENTLY_READING_BOOKS_KEY, books);
            }
        }
    }

    public void addToAlreadyReadBooks(NewBook incomingBook){
        ArrayList<NewBook> books = getAlreadyReadBooks();
        if(books != null){
            if(books.add(incomingBook)) {
                updateData(ALREADY_READ_BOOKS_KEY, books);
            }
        }
    }

    public void addToMyWishlistBooks(NewBook incomingBook){
        ArrayList<NewBook> books = getMyWishlistBooks();
        if(books != null){
            if(books.add(incomingBook)) {
                updateData(MY_WISH_LIST_BOOKS_KEY, books);
            }
        }
    }

    public void addToMyFavoriteBooks(NewBook incomingBook){
        ArrayList<NewBook> books = getMyFavoriteBooks();
        if(books != null){
            if(books.add(incomingBook)) {
                updateData(MY_FAVORITE_BOOKS_KEY, books);
            }
        }
    }

    public void addToSearchResults(NewBook incomingBook){
        ArrayList<NewBook> books = getSearchResults();
        if(books != null){
            if(books.add(incomingBook)){
                updateData(SEARCH_RESULTS_KEY, books);
            }
        }
    }

    public NewBook getBookById(String id){
        if(getAllBooks() != null){
            for (NewBook e : getAllBooks()) {
                if (e.getMainId().equals(id)) {
                    return e;
                }
            }
        }
        return null;
    }

    public void removeBook(NewBook book, String bookList) {
        ArrayList<NewBook> books;
        switch(bookList){
            case "allBooks":
                books = getAllBooks();
                for(NewBook e: books){
                    if(e.getMainId().equals(book.getMainId())){
                        books.remove(e);
                        break;
                    }
                }
                updateData(ALL_BOOKS_KEY, books);
                break;
            case "currentlyReadingBooks":
                books = getCurrentlyReadingBooks();
                for(NewBook e: books){
                    if(e.getMainId().equals(book.getMainId())){
                        books.remove(e);
                        break;
                    }
                }
                updateData(CURRENTLY_READING_BOOKS_KEY, books);
                break;
            case "alreadyReadBooks":
                books = getAlreadyReadBooks();
                for(NewBook e: books){
                    if(e.getMainId().equals(book.getMainId())){
                        books.remove(e);
                        break;
                    }
                }
                updateData(ALREADY_READ_BOOKS_KEY, books);
                break;
            case "myWishlistBooks":
                books = getMyWishlistBooks();
                for(NewBook e: books){
                    if(e.getMainId().equals(book.getMainId())){
                        books.remove(e);
                        break;
                    }
                }
                updateData(MY_WISH_LIST_BOOKS_KEY, books);
                break;
            case "myFavoriteBooks":
                books = getMyFavoriteBooks();
                for(NewBook e: books){
                    if(e.getMainId().equals(book.getMainId())){
                        books.remove(e);
                        break;
                    }
                }
                updateData(MY_FAVORITE_BOOKS_KEY, books);
                break;
            case "searchList":
                books = getSearchResults();
                for(NewBook e: books){
                    if(e.getMainId().equals(book.getMainId())){
                        books.remove(e);
                        break;
                    }
                }
                updateData(SEARCH_RESULTS_KEY, books);
                break;
        }
    }

    public void refreshIsChecked(String bookList) {
        ArrayList<NewBook> books;
        switch(bookList){
            case "allBooks":
                books = getAllBooks();
                for(NewBook e: books){
                    e.setIsChecked("default");
                    e.setAdd(false);
                }
                updateData(ALL_BOOKS_KEY, books);
                break;
            case "currentlyReadingBooks":
                books = getCurrentlyReadingBooks();
                for(NewBook e: books){
                    e.setIsChecked("default");
                    e.setAdd(false);
                }
                updateData(CURRENTLY_READING_BOOKS_KEY, books);
                break;
            case "alreadyReadBooks":
                books = getAlreadyReadBooks();
                for(NewBook e: books){
                    e.setIsChecked("default");
                    e.setAdd(false);
                }
                updateData(ALREADY_READ_BOOKS_KEY, books);
                break;
            case "myWishlistBooks":
                books = getMyWishlistBooks();
                for(NewBook e: books){
                    e.setIsChecked("default");
                    e.setAdd(false);
                }
                updateData(MY_WISH_LIST_BOOKS_KEY, books);
                break;
            case "myFavoriteBooks":
                books = getMyFavoriteBooks();
                for(NewBook e: books){
                    e.setIsChecked("default");
                    e.setAdd(false);
                }
                updateData(MY_FAVORITE_BOOKS_KEY, books);
                break;
            case "searchList":
                books = getSearchResults();
                for(NewBook e: books){
                    e.setIsChecked("default");
                    e.setAdd(false);
                }
                updateData(SEARCH_RESULTS_KEY, books);
                break;
        }
    }

    public void updateBookList(String change, String key, NewBook book){
        ArrayList<NewBook> allBooks = getAllBooks();
        switch (key){
            case "fav":
                for (NewBook e : allBooks) {
                    if (e.getMainId().equals(book.getMainId())) {
                        e.setFav(book.isFav());
                    }
                }
                break;
            case "Reading Status":
                for(NewBook e: allBooks){
                    if(e.getMainId().equals(book.getMainId())){
                        e.setStatus(change);
                    }
                }
                break;
            case "notes":
                for(NewBook e: allBooks){
                    if(e.getMainId().equals(book.getMainId())){
                        e.setNotes(change);
                    }
                }
        }
        updateData(ALL_BOOKS_KEY, allBooks);
    }

    private void saveNewData(String bookListName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putString(bookListName, gson.toJson(new ArrayList<NewBook>()));
        editor.apply(); // use editor.apply()
    }

    public void updateData(String key, ArrayList<NewBook> books) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.putString(key, gson.toJson(books));
        editor.apply();
    }

    public void saveNewPermissions(String key){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, false);
        editor.apply();
    }

    public void cancelPermissions(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(GALLERY_PERMISSION_KEY);
        editor.remove(CAMERA_PERMISSION_KEY);
        editor.remove(FIREBASE_ANALYTICS_PERMISSION_KEY);
        editor.apply();
    }

    public void refreshSearch(){
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(SEARCH_RESULTS_KEY);
        editor.putString(SEARCH_RESULTS_KEY, gson.toJson(new ArrayList<NewBook>()));
        editor.apply();
    }

    public void updateExistingBook(NewBook incomingBook, String format, String imgFrom){
        ArrayList<NewBook> allBooks = getAllBooks();
        for(NewBook e: allBooks){
            if(e.getMainId().equals(incomingBook.getMainId())){
                e.setBookImg(format);
                e.setImgSource(imgFrom);
                updateData(ALL_BOOKS_KEY, allBooks);
            }
        }
        switch(incomingBook.getStatus()){
            case "Currently Reading":
                ArrayList<NewBook> currentlyReadingBooks = getCurrentlyReadingBooks();
                for(NewBook e: currentlyReadingBooks){
                    if(e.getMainId().equals(incomingBook.getMainId())){
                        e.setBookImg(format);
                        e.setImgSource(imgFrom);
                    }
                }
                updateData(CURRENTLY_READING_BOOKS_KEY, currentlyReadingBooks);
                break;
            case "Already Read":
                ArrayList<NewBook> alreadyReadBooks = getAlreadyReadBooks();
                for(NewBook e: alreadyReadBooks){
                    if(e.getMainId().equals(incomingBook.getMainId())){
                        e.setBookImg(format);
                        e.setImgSource(imgFrom);
                    }
                }
                updateData(ALREADY_READ_BOOKS_KEY, alreadyReadBooks);
                break;
            case "On My Wishlist":
                ArrayList<NewBook> onMyWishlist = getMyWishlistBooks();
                for(NewBook e: onMyWishlist){
                    if(e.getMainId().equals(incomingBook.getMainId())){
                        e.setBookImg(format);
                        e.setImgSource(imgFrom);
                    }
                }
                updateData(MY_WISH_LIST_BOOKS_KEY, onMyWishlist);
                break;
        }
        if(incomingBook.isFav()){
            ArrayList<NewBook> favBooks = getMyFavoriteBooks();
            for(NewBook e: favBooks){
                if(e.getMainId().equals(incomingBook.getMainId())){
                    e.setBookImg(format);
                    e.setImgSource(imgFrom);
                }
            }
            updateData(MY_FAVORITE_BOOKS_KEY, favBooks);
        }
    }
}
