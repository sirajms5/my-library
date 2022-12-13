package com.sirajsaleem.my_library;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity implements MethodsFactory{

    private Button btnAllBooks;
    private Button btnAlreadyRead;
    private Button btnWishlist;
    private Button btnCurrentlyReadying;
    private Button btnFavorite;
    private Button statisticBtn;
    private Button btnAbout;
    private FloatingActionButton floater;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getIntent().getBooleanExtra("back", false)){
            overridePendingTransition(R.anim.navigation_back_slide_in, R.anim.navigation_back_slide_out);
        }

        findViews();
        //setting up sharedPreferences
        BooksArray.getInstance(this);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if(BooksArray.getInstance(this).getPermission("firebaseAnalytics")) {
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        } else {
            if(!BooksArray.getInstance(this).getPermission("noDialog")) {
                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setContentView(R.layout.firebase_permission_dialog);
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90); //making width of dialog 90% of screen width
                dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCancelable(true);
                dialog.show();

                TextView allow = dialog.findViewById(R.id.allowTxt);
                TextView deny = dialog.findViewById(R.id.denyTxt);
                CheckBox neverShowCheckBox = dialog.findViewById(R.id.permissionCheckBox);

                allow.setOnClickListener(v -> {
                    dialog.dismiss();
                    mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
                    BooksArray.getInstance(MainActivity.this).grantPermissions("firebaseAnalytics", true);
                    if (neverShowCheckBox.isChecked()) {
                        BooksArray.getInstance(this).grantPermissions("noDialog", true);
                    }
                });

                deny.setOnClickListener(v -> {
                    dialog.dismiss();
                    mFirebaseAnalytics.setAnalyticsCollectionEnabled(false);
                    BooksArray.getInstance(MainActivity.this).grantPermissions("firebaseAnalytics", false);
                    if (neverShowCheckBox.isChecked()) {
                        BooksArray.getInstance(this).grantPermissions("noDialog", true);
                    }
                });
            }
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        btnAllBooks.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecListActivity.class);
            intent.putExtra("from", "AllBooks");
            startActivity(intent);
        });

        btnCurrentlyReadying.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecListActivity.class);
            intent.putExtra("from", "CurrentlyReadingBooks");
            startActivity(intent);
        });

        btnAlreadyRead.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecListActivity.class);
            intent.putExtra("from", "AlreadyReadBooks");
            startActivity(intent);
        });

        btnWishlist.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecListActivity.class);
            intent.putExtra("from", "MyWishlist");
            startActivity(intent);
        });

        btnFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecListActivity.class);
            intent.putExtra("from", "MyFavorites");
            startActivity(intent);
        });

        statisticBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, StatisticActivity.class);
            startActivity(intent);
        });

        floater.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddBookActivity.class);
            intent.putExtra("from", "MainActivity");
            startActivity(intent);
        });

        btnAbout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("My Library");
            builder.setMessage("Designed and Developed with Love by Siraj at sirajsaleem.com\n" +
                    "Check my website for more applications");
            builder.setPositiveButton("Visit", (dialogInterface, i) -> {
                Intent intent = new Intent(this, WebsiteActivity.class);
                startActivity(intent);
            });
            builder.setNegativeButton("Dismiss", (dialogInterface, i) -> {

            });
            builder.setCancelable(true);
            builder.show();
        });
    }

    @Override
    public void findViews() {
        btnAllBooks = findViewById(R.id.btnAllBooks);
        btnAlreadyRead = findViewById(R.id.btnAlreadyRead);
        btnWishlist = findViewById(R.id.btnWishlist);
        btnCurrentlyReadying = findViewById(R.id.btnCurrentlyReading);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnAbout = findViewById(R.id.btnAbout);
        floater = findViewById(R.id.floatingBtn);
        statisticBtn = findViewById(R.id.statistics);
    }

    @Override
    public void goBack() {
        //not required in this activity
    }

    @Override
    public void handlerManager() {
        //not required in this activity
    }
}