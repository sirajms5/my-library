package com.sirajsaleem.my_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;


public class StatisticActivity extends AppCompatActivity implements MethodsFactory {

    private TextView allBooksNum;
    private TextView currentlyReadingNum;
    private TextView alreadyReadNum;
    private TextView wishlistNum;
    private TextView favNum;
    private TextView pagesNum;
    private int readPages;
    private int allPagesMax;
    private int pagesCounter;
    private int allBooksMax;
    private int currentBooksMax;
    private int alreadyReadBooksMax;
    private int myWishlistMax;
    private int favMax;
    private int allBooksCounter;
    private int currentBooksCounter;
    private int alreadyReadBooksCounter;
    private int myWishlistCounter;
    private int favCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        if(getIntent().getBooleanExtra("back", false)){
            overridePendingTransition(R.anim.navigation_back_slide_in, R.anim.navigation_back_slide_out);
        }

        findViews();

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Library Statistics");
        }

        for(NewBook e: BooksArray.getInstance(this).getAllBooks()){
            allPagesMax = allPagesMax + Integer.parseInt(e.getPagesNum());
        }
        for(NewBook e: BooksArray.getInstance(this).getAlreadyReadBooks()){
            readPages = readPages + Integer.parseInt(e.getPagesNum());
        }

        allBooksMax = BooksArray.getInstance(this).getAllBooks().size();
        currentBooksMax = BooksArray.getInstance(this).getCurrentlyReadingBooks().size();
        alreadyReadBooksMax = BooksArray.getInstance(this).getAlreadyReadBooks().size();
        myWishlistMax = BooksArray.getInstance(this).getMyWishlistBooks().size();
        favMax = BooksArray.getInstance(this).getMyFavoriteBooks().size();



        counting.run();
    }

    private final Runnable counting = new Runnable() {
        @Override
        public void run() {
            if(allBooksCounter <= allBooksMax){
                String counterAsString = "" + allBooksCounter;
                allBooksNum.setText(counterAsString);
                allBooksCounter++;
            }

            if(currentBooksCounter <= currentBooksMax){
                String counterAsString = "" + currentBooksCounter;
                currentlyReadingNum.setText(counterAsString);
                currentBooksCounter++;
            }

            if(alreadyReadBooksCounter <= alreadyReadBooksMax){
                String counterAsString = "" + alreadyReadBooksCounter;
                alreadyReadNum.setText(counterAsString);
                alreadyReadBooksCounter++;
            }

            if(myWishlistCounter <= myWishlistMax){
                String counterAsString = "" + myWishlistCounter;
                wishlistNum.setText(counterAsString);
                myWishlistCounter++;
            }

            if(favCounter <= favMax){
                String counterAsString = "" + favCounter;
                favNum.setText(counterAsString);
                favCounter++;
            }

            if(pagesCounter <= readPages) {
                String counterAsString = pagesCounter + "/" + allPagesMax;
                pagesNum.setText(counterAsString);
                pagesCounter = pagesCounter + 7;
            } else {
                String finalCount = readPages + "/" + allPagesMax;
                pagesNum.setText(finalCount);
            }

            Handler handler = new Handler();
            handler.postDelayed(this, 1);
        }
    };

    @Override
    public void onBackPressed() {
        goBack(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.short_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            goBack(null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void goBack(String string) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("back", true);
        startActivity(intent);
    }

    @Override
    public void findViews() {
        allBooksNum = findViewById(R.id.allBooksNum);
        currentlyReadingNum = findViewById(R.id.currentlyReadingNum);
        alreadyReadNum = findViewById(R.id.alreadyReadNum);
        wishlistNum = findViewById(R.id.myWishListNum);
        favNum = findViewById(R.id.myFavNum);
        pagesNum = findViewById(R.id.pagesReadNum);
    }

    @Override
    public void handlerManager() {
        //not required in this activity
    }
}