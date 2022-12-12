package com.sirajsaleem.my_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

public class SearchResultsActivity extends AppCompatActivity implements MethodsFactory, RecyclerViewMethods{

    private ConstraintLayout parentLayout;
    private ConstraintLayout editToolBar;
    private ConstraintLayout searchFloaterLayout;
    private RecyclerView searchResultsRec;
    private String fromHandler;
    private String identifierParent;
    public static boolean editClicked = false;
    public static int clickedNum = 0;
    private ArrayList<NewBook> currentList = new ArrayList<>();
    private TextView deleteTxt;
    private TextView unSelectAll;
    private TextView selectAllTxt;
    private TextView cancelTxt;
    public static boolean allSelected = false;
    public static ArrayList<NewBook> booksToDelete = new ArrayList<>();
    public static boolean allSelectedAdapterChecker = false;
    private String sortBy = "a-z";
    public static boolean isCanceled = false;
    private NewBookRecViewAdapter adapter;
    private FloatingActionButton floater;
    private boolean isActivated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        if(getIntent().getBooleanExtra("back", false)){
            overridePendingTransition(R.anim.navigation_back_slide_in, R.anim.navigation_back_slide_out);
        }

        findViews();
        refreshActivityControls();
        handlerManager();
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Search Results");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setActivityRec(null);

        if(getIntent().getStringExtra("deletedBookName") != null){
            String deletedBook = getIntent().getStringExtra("deletedBookName") + " has been deleted.";
            Snackbar.make(parentLayout, deletedBook, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dismiss", view -> {})
                    .show();
        }

        selectAllTxt.setOnClickListener(v -> {
            allSelected = true;
            allSelectedAdapterChecker = true;
            checkedManager(null);
        });

        unSelectAll.setOnClickListener(v -> {
            allSelectedAdapterChecker = false;
            allSelected = false;
            booksToDelete.clear();
            checkedManager(null);
        });

        deleteTxt.setOnClickListener(V -> {
            if(allSelected) {
                booksToDelete = currentList;
            }
            if(booksToDelete.size() == 0){
                Toast.makeText(this, "There are no books selected to delete", Toast.LENGTH_SHORT).show();
            } else {
                deleteAlertDialog();
            }
        });

        cancelTxt.setOnClickListener(v -> {
            allSelectedAdapterChecker = true; //to bypass adapter conditions
            allSelected = true;  //to bypass adapter conditions
            isCanceled = true;
            removeEditToolBar();
            checkedManager(null);
        });

        searchResultsRec.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int scrollY = searchResultsRec.computeVerticalScrollOffset();
                if(scrollY > 50){
                    floater.setVisibility(View.VISIBLE);
                } else {
                    floater.setVisibility(View.INVISIBLE);
                }
            }
        });

        floater.setOnClickListener(v -> searchResultsRec.smoothScrollToPosition(0));
    }

    @Override
    public void goBack() {
        Intent intent;
        if(fromHandler.equals("MainActivity")){
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            intent = new Intent(this, RecListActivity.class);
            intent.putExtra("from", fromHandler);
            intent.putExtra("identifierParent", identifierParent);
        }
        intent.putExtra("back", true);
        startActivity(intent);
    }

    @Override
    public void refreshActivityControls() {
        BooksArray.getInstance(this).refreshIsChecked("searchList");
        editClicked = false; //refreshing checkbox view state to gone
        allSelectedAdapterChecker = false; //for adapter convenience
        isCanceled = false; //refreshing checkbox view state to gone
        RecListActivity.editClicked = false; //refreshing checkbox view state to gone
        RecListActivity.allSelected = true; //for adapter convenience
        RecListActivity.allSelectedAdapterChecker = true; //for adapter convenience
    }

    @Override
    public void removeEditToolBar() {
        hideEditBar(editToolBar);
        //----
        ConstraintLayout.LayoutParams recParams = (ConstraintLayout.LayoutParams) searchResultsRec.getLayoutParams();
        recParams.bottomToTop = -1;
        searchResultsRec.setPadding(0,0,0,0);
        //----
        searchFloaterLayout.animate().translationY(0);
        editClicked = false;
    }

    @Override
    public void deleteAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(clickedNum == 1){
            builder.setMessage("Are you sure you want to delete " + booksToDelete.get(0).getBookName() + " from your library?");
        } else {
            builder.setMessage("Are you sure you want to delete " + clickedNum + " selected books from your library?");
        }
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            while (booksToDelete.size() != 0) {
                for (NewBook e : booksToDelete) {
                    if (e.getIsChecked().equals("checked")) {
                        BooksArray.getInstance(SearchResultsActivity.this).removeBook(e, "allBooks");
                        switch (e.getStatus()) {
                            case "Currently Reading":
                                BooksArray.getInstance(SearchResultsActivity.this).removeBook(e, "currentlyReadingBooks");
                                break;
                            case "Already Read":
                                BooksArray.getInstance(SearchResultsActivity.this).removeBook(e, "alreadyReadBooks");
                                break;
                            case "On My Wishlist":
                                BooksArray.getInstance(SearchResultsActivity.this).removeBook(e, "myWishlistBooks");
                                break;
                        }
                        BooksArray.getInstance(SearchResultsActivity.this).removeBook(e, "myFavoriteBooks");
                        BooksArray.getInstance(SearchResultsActivity.this).removeBook(e, "searchList");
                        currentList.remove(e);
                        booksToDelete.remove(e);
                    }
                    break;
                }
            }
            removeEditToolBar();
            notifyAdapter();
        });
        builder.setNegativeButton("No", (dialogInterface, i) -> {

        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void showEditBar(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.edit_tool_bar_slide_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                editToolBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(animation);
    }

    @Override
    public void hideEditBar(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.edit_tool_bar_slide_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                editToolBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    @Override
    public void checkedManager(String handler) {
        ArrayList<NewBook> checkedList = new ArrayList<>();
        for(NewBook e: BooksArray.getInstance(this).getSearchResults()){
            if(!isCanceled) {
                if (selectAllTxt.getVisibility() == View.VISIBLE) {
                    e.setIsChecked("checked");
                    e.setAdd(true);
                } else {
                    e.setIsChecked("unChecked");
                    e.setAdd(false);
                }
            } else {
                e.setIsChecked("default");
                e.setAdd(false);
            }
            checkedList.add(e);
        }
        BooksArray.getInstance(this).updateData("searchResults", checkedList);
        currentList = BooksArray.getInstance(this).getSearchResults();
        isCanceled = false;
        allSelectedAdapterChecker = false;
        allSelected = false;
        notifyAdapter();
    }

    @Override
    public void notifyAdapter() {
        clickedNum = 0;
        NewBookRecViewAdapter.listNum = 0;
        adapter.setBooksList(currentList);
    }

    @Override
    public void handlerManager() {
        fromHandler = getIntent().getStringExtra("from");
        identifierParent = getIntent().getStringExtra("identifierParent");
    }

    @Override
    public void findViews() {
        parentLayout = findViewById(R.id.searchRecListLayout);
        searchResultsRec = findViewById(R.id.searchRecList);
        editToolBar = findViewById(R.id.editToolBar);
        selectAllTxt = findViewById(R.id.selectAllTxt);
        deleteTxt = findViewById(R.id.deleteTxt);
        unSelectAll = findViewById(R.id.unSelectAllTxt);
        cancelTxt = findViewById(R.id.cancelTxt);
        floater = findViewById(R.id.goUpBtn);
        searchFloaterLayout = findViewById(R.id.searchFloaterLayout);
    }

    @Override
    public void setActivityRec(String handler) {
        clickedNum = 0; //refreshing clickedNum
        currentList = BooksArray.getInstance(this).getSearchResults();
        sortList(sortBy);
        NewBookRecViewAdapter.listNum = 0;
        adapter = new NewBookRecViewAdapter(this, "search", identifierParent);
        adapter.setBooksList(currentList);
        searchResultsRec.setAdapter(adapter);
        searchResultsRec.setLayoutManager(new LinearLayoutManager(this));
        isActivated = true;
    }

    @Override
    public void sortList(String sortBy) {
        switch(sortBy){
            case "a-z":
                Collections.sort(currentList, (t1, t2) -> t1.getBookName().compareToIgnoreCase(t2.getBookName()));
                break;
            case "z-a":
                Collections.sort(currentList, (t1, t2) -> t2.getBookName().compareToIgnoreCase(t1.getBookName()));
                break;
            case "dateAdded":
                Collections.sort(currentList, (t1, t2) -> {
                    int num1 = Integer.parseInt(t1.getMainId());
                    int num2 = Integer.parseInt(t2.getMainId());
                    return Integer.compare(num1, num2);
                });
                break;
        }
        if(isActivated){
            notifyAdapter();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.edit) {
            if (!editClicked) {
                booksToDelete.clear();
            }
            editClicked = true;
            notifyAdapter();
            showEditBar(editToolBar);
            //changing recyclerView constraints
            //changing layout params based on screen orientation
            int orientation = getResources().getConfiguration().orientation;
            if(orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (currentList.size() > 2) {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) searchResultsRec.getLayoutParams();
                    params.bottomToTop = R.id.editToolBar;
                    searchResultsRec.setPadding(0, 50, 0, 50);
                }
            } else {
                if (currentList.size() > 1) {
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) searchResultsRec.getLayoutParams();
                    params.bottomToTop = R.id.editToolBar;
                    searchResultsRec.setPadding(0, 50, 0, 50);
                }
            }
            int recyclerViewScrollPosition = searchResultsRec.computeVerticalScrollOffset();
            if (recyclerViewScrollPosition <= 50) {
                searchResultsRec.smoothScrollToPosition(0);
            }

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int height = size.y;
            searchFloaterLayout.animate().translationY(-height/29f);

            deleteCounter.run();
        }else if(item.getItemId() == R.id.az) {
            sortBy = "a-z";
            sortList(sortBy);
        }else if(item.getItemId() == R.id.za) {
            sortBy = "z-a";
            sortList(sortBy);
        } else if(item.getItemId() == R.id.dateAdded) {
            sortBy = "dateAdded";
            sortList(sortBy);
        } else if(item.getItemId() == android.R.id.home) {
            goBack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        fromHandler = "MainActivity";
        goBack();
    }

    private final Runnable deleteCounter = new Runnable() {
        @Override
        public void run() {
            if(editClicked) {
                String updatedCheckedNum = "(" + clickedNum + ")";
                deleteTxt.setText(updatedCheckedNum);
                if (clickedNum == currentList.size()) {
                    selectAllTxt.setVisibility(View.GONE);
                    unSelectAll.setVisibility(View.VISIBLE);
                    allSelected = true;
                } else {
                    selectAllTxt.setVisibility(View.VISIBLE);
                    unSelectAll.setVisibility(View.GONE);
                    allSelected = false;
                }
                Handler handler = new Handler();
                handler.postDelayed(this, 100);
            }
        }
    };
}