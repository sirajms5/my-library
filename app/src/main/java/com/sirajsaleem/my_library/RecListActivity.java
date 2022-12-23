package com.sirajsaleem.my_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecListActivity extends AppCompatActivity implements MethodsFactory, RecyclerViewMethods {
    private String fromHandler;
    private String sortedBy;
    private NewBookRecViewAdapter adapter;
    private RecyclerView booksRec;
    private ConstraintLayout parentLayout;
    private ConstraintLayout editToolBar;
    private ConstraintLayout floaterLayout;
    private Dialog dialog;
    private EditText searchEditTxt;
    private TextView deleteTxt;
    private TextView unSelectAll;
    private TextView selectAllTxt;
    private TextView cancelTxt;
    public static int clickedNum = 0;
    public static boolean editClicked = false;
    public static boolean allSelected = false;
    public static boolean allSelectedAdapterChecker = false;
    public static boolean isCanceled = false;
    private boolean isActivated = false;
    public static ArrayList<NewBook> booksToDelete = new ArrayList<>();
    private ArrayList<NewBook> currentList = new ArrayList<>();
    private FloatingActionButton floater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_list);

        if(getIntent().getBooleanExtra("back", false)){
            overridePendingTransition(R.anim.navigation_back_slide_in, R.anim.navigation_back_slide_out);
        }

        findViews();
        refreshActivityControls();
        handlerManager();
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        createTitle();

        if(getIntent().getStringExtra("bookName") != null){
            Snackbar.make(parentLayout, getIntent().getStringExtra("bookName") + " has been added to your library.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dismiss", view -> {})
                    .show();
        }

        if(getIntent().getStringExtra("deletedBookName") != null){
            String deletedBook = getIntent().getStringExtra("deletedBookName") + " has been deleted.";
            Snackbar.make(parentLayout, deletedBook, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dismiss", view -> {})
                    .show();
        }

        setActivityRec(fromHandler);
        selectAllTxt.setOnClickListener(v -> {
            allSelectedAdapterChecker = true;
            allSelected = true;
            checkedManager(fromHandler);
        });

        unSelectAll.setOnClickListener(v -> {
            allSelectedAdapterChecker = false;
            allSelected = false;
            booksToDelete.clear();
            checkedManager(fromHandler);
        });

        deleteTxt.setOnClickListener(V -> {
            if(allSelected) {
                booksToDelete = currentList;
            }
            if(booksToDelete.size() == 0){
                Snackbar.make(parentLayout, "There are no books selected to delete", Snackbar.LENGTH_LONG)
                        .show();
            } else {
                deleteAlertDialog();
                }
        });

        cancelTxt.setOnClickListener(v -> {
            allSelectedAdapterChecker = true;
            allSelected = true;
            isCanceled = true;
            booksToDelete.clear();
            removeEditToolBar();
            checkedManager(fromHandler);
        });

        booksRec.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int scrollY = booksRec.computeVerticalScrollOffset();
                if(scrollY > 50){
                    floater.setVisibility(View.VISIBLE);
                } else {
                    floater.setVisibility(View.INVISIBLE);
                }
            }
        });

        floater.setOnClickListener(v -> booksRec.smoothScrollToPosition(0));
    }

    private ArrayList<NewBook> currentListMaker(String handler){
        switch(handler){
            case "AllBooks":
                currentList = BooksArray.getInstance(this).getAllBooks(); // call BooksArray.getInstance() in main.activity to avoid null exception error
                break;
            case "CurrentlyReadingBooks":
                currentList = BooksArray.getInstance(this).getCurrentlyReadingBooks();
                break;
            case "AlreadyReadBooks":
                currentList = BooksArray.getInstance(this).getAlreadyReadBooks();
                break;
            case "MyWishlist":
                currentList = BooksArray.getInstance(this).getMyWishlistBooks();
                break;
            case "MyFavorites":
                currentList = BooksArray.getInstance(this).getMyFavoriteBooks();
                break;
        }
        return currentList;
    }

    private void createTitle() {

        String title = "";
        title = title + fromHandler.charAt(0);
        for(int i = 1; i < fromHandler.length(); i++){
            String tester = fromHandler.substring(i, i + 1);
            if(tester.toUpperCase().equals(tester)){
                title = title.concat(" ");
            }
            title = title.concat(tester);
        }
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    public void onBackPressed() {
        goBack(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.books_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            if(item.getItemId() == R.id.add) {
                Intent intentAdd = new Intent(this, AddBookActivity.class);
                intentAdd.putExtra("from", fromHandler);
                startActivity(intentAdd);
            } else if(item.getItemId() == R.id.search) {
                dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setContentView(R.layout.search_prompt_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int width = (int)(getResources().getDisplayMetrics().widthPixels*0.90); // to make width of dialog 90% of screen width
                dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                dialog.show();
                Button searchBtn = dialog.findViewById(R.id.searchBtn);
                Button cancelBtn = dialog.findViewById(R.id.cancelBtn);
                searchEditTxt = dialog.findViewById(R.id.searchEditTxt);
                searchBtn.setOnClickListener(v -> {
                    String editTxtInput = searchEditTxt.getText().toString();
                    Pattern pattern = Pattern.compile(".*" + editTxtInput + ".*"); //user input as regex
                    Matcher matcher;
                    if(editTxtInput.equals("")){
                        dialog.dismiss();
                        Snackbar.make(parentLayout, "Search bar is empty", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Dismiss", view -> {

                                })
                                .show();
                    } else {
                        switch (fromHandler) {
                            case "AllBooks":
                                for (NewBook e : BooksArray.getInstance(this).getAllBooks()) {
                                    matcher = pattern.matcher(e.getBookName());
                                    if (matcher.matches()) {
                                        BooksArray.getInstance(this).addToSearchResults(e);
                                    }
                                }
                                break;
                            case "CurrentlyReadingBooks":
                                for (NewBook e : BooksArray.getInstance(this).getCurrentlyReadingBooks()) {
                                    matcher = pattern.matcher(e.getBookName());
                                    if (matcher.matches()) {
                                        BooksArray.getInstance(this).addToSearchResults(e);
                                    }
                                }
                                break;
                            case "AlreadyReadBooks":
                                for (NewBook e : BooksArray.getInstance(this).getAlreadyReadBooks()) {
                                    matcher = pattern.matcher(e.getBookName());
                                    if (matcher.matches()) {
                                        BooksArray.getInstance(this).addToSearchResults(e);
                                    }
                                }
                                break;
                            case "MyWishlist":
                                for (NewBook e : BooksArray.getInstance(this).getMyWishlistBooks()) {
                                    matcher = pattern.matcher(e.getBookName());
                                    if (matcher.matches()) {
                                        BooksArray.getInstance(this).addToSearchResults(e);
                                    }
                                }
                                break;
                            case "MyFavorites":
                                for (NewBook e : BooksArray.getInstance(this).getMyFavoriteBooks()) {
                                    matcher = pattern.matcher(e.getBookName());
                                    if (matcher.matches()) {
                                        BooksArray.getInstance(this).addToSearchResults(e);
                                    }
                                }
                                break;
                        }
                        if (BooksArray.getInstance(this).getSearchResults().size() >= 1) {
                            ArrayList<NewBook> refreshingSearchArrayList = new ArrayList<>();
                            for (NewBook e : BooksArray.getInstance(this).getSearchResults()) { //to refresh holder in search intent
                                e.setIsChecked("default");
                                e.setAdd(false);
                                refreshingSearchArrayList.add(e);
                            }
                            BooksArray.getInstance(this).updateData("searchResults", refreshingSearchArrayList);
                            Intent intentSearch = new Intent(this, SearchResultsActivity.class);
                            intentSearch.putExtra("from", fromHandler);
                            intentSearch.putExtra("identifierParent", fromHandler);
                            startActivity(intentSearch);
                        } else {
                            dialog.dismiss();
                            Snackbar.make(parentLayout, "Book not in your Library", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Dismiss", j -> {

                                    })
                                    .show();
                        }
                    }
                });
                cancelBtn.setOnClickListener(v -> dialog.dismiss());
            } else if(item.getItemId() == R.id.edit) {
                if (!editClicked) {
                    booksToDelete.clear();
                }
                editClicked = true;
                notifyAdapter();
                showEditBar(editToolBar);
                //changing layout params based on screen orientation
                int orientation = getResources().getConfiguration().orientation;
                if(orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (currentList.size() > 2) {
                        ConstraintLayout.LayoutParams recParams = (ConstraintLayout.LayoutParams) booksRec.getLayoutParams();
                        recParams.bottomToTop = R.id.editToolBar;
                        booksRec.setPadding(0, 50, 0, 50);
                    }
                } else {
                    if (currentList.size() > 1) {
                        ConstraintLayout.LayoutParams recParams = (ConstraintLayout.LayoutParams) booksRec.getLayoutParams();
                        recParams.bottomToTop = R.id.editToolBar;
                        booksRec.setPadding(0, 50, 0, 50);
                    }
                }
                int recyclerViewScrollPosition = booksRec.computeVerticalScrollOffset();
                if (recyclerViewScrollPosition <= 50) {
                    booksRec.smoothScrollToPosition(0);
                }

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int height = size.y;
                floaterLayout.animate().translationY(-height/29f);

                deleteCounter.run();

            } else if(item.getItemId() == R.id.az) {
                sortedBy = "a-z";
                sortList(sortedBy);
            } else if(item.getItemId() == R.id.za) {
                sortedBy = "z-a";
                sortList(sortedBy);
            } else if(item.getItemId() == R.id.dateAdded) {
                sortedBy = "dateAdded";
                sortList(sortedBy);
            } else if(item.getItemId() == R.id.cancelPermissions) {
                BooksArray.getInstance(this).cancelPermissions();
                Snackbar.make(parentLayout, "All permissions has been canceled", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss", v -> {

                        })
                        .show();
            } else if(item.getItemId() == android.R.id.home) {
                goBack(null);
            }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void goBack(String string) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("back", true);
        startActivity(intent);
    }

    @Override
    public void refreshActivityControls() {
        BooksArray.getInstance(this).refreshIsChecked("allBooks");
        BooksArray.getInstance(this).refreshIsChecked("currentlyReadingBooks");
        BooksArray.getInstance(this).refreshIsChecked("alreadyReadBooks");
        BooksArray.getInstance(this).refreshIsChecked("myWishlistBooks");
        BooksArray.getInstance(this).refreshIsChecked("myFavoriteBooks");
        BooksArray.getInstance(this).refreshSearch();
        editClicked = false; //refreshing checkbox view state to gone
        allSelectedAdapterChecker = false; //for adapter convenience
        isCanceled = false; //refreshing checkbox view state to gone
        SearchResultsActivity.editClicked = false; //refreshing checkbox view state to gone
        SearchResultsActivity.allSelected = true; //for adapter convenience
        SearchResultsActivity.allSelectedAdapterChecker = true; //for adapter convenience
    }

    @Override
    public void removeEditToolBar() {
        hideEditBar(editToolBar);
        //----
        ConstraintLayout.LayoutParams recParams = (ConstraintLayout.LayoutParams) booksRec.getLayoutParams();
        recParams.bottomToTop = -1;
        booksRec.setPadding(0,0,0,0);

        floaterLayout.animate().translationY(0);
        //----
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
                        BooksArray.getInstance(RecListActivity.this).removeBook(e, "allBooks");
                        switch (e.getStatus()) {
                            case "Currently Reading":
                                BooksArray.getInstance(RecListActivity.this).removeBook(e, "currentlyReadingBooks");
                                break;
                            case "Already Read":
                                BooksArray.getInstance(RecListActivity.this).removeBook(e, "alreadyReadBooks");
                                break;
                            case "On My Wishlist":
                                BooksArray.getInstance(RecListActivity.this).removeBook(e, "myWishlistBooks");
                                break;
                        }
                        BooksArray.getInstance(RecListActivity.this).removeBook(e, "myFavoriteBooks");
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
        switch (handler){
            case "AllBooks":
                for(NewBook e: BooksArray.getInstance(this).getAllBooks()){
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
                BooksArray.getInstance(this).updateData("allBooks", checkedList);
                currentList = BooksArray.getInstance(this).getAllBooks();
                break;
            case "CurrentlyReadingBooks":
                for(NewBook e: BooksArray.getInstance(this).getCurrentlyReadingBooks()){
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
                BooksArray.getInstance(this).updateData("currentlyReadingBooks", checkedList);
                currentList = BooksArray.getInstance(this).getCurrentlyReadingBooks();
                break;
            case "AlreadyReadBooks":
                for(NewBook e: BooksArray.getInstance(this).getAlreadyReadBooks()){
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
                BooksArray.getInstance(this).updateData("alreadyReadBooks", checkedList);
                currentList = BooksArray.getInstance(this).getAlreadyReadBooks();
                break;
            case "MyWishlist":
                for(NewBook e: BooksArray.getInstance(this).getMyWishlistBooks()){
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
                BooksArray.getInstance(this).updateData("myWishlistBooks", checkedList);
                currentList = BooksArray.getInstance(this).getMyWishlistBooks();
                break;
            case "MyFavorites":
                for(NewBook e: BooksArray.getInstance(this).getMyFavoriteBooks()){
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
                BooksArray.getInstance(this).updateData("myFavoriteBooks", checkedList);
                currentList = BooksArray.getInstance(this).getMyFavoriteBooks();
                break;
        }
        isCanceled = false;
        allSelectedAdapterChecker = false;
        allSelected = false;
        notifyAdapter();
    }

    @Override
    public void notifyAdapter() {
        clickedNum = 0; //refreshing clickedNum
        NewBookRecViewAdapter.listNum = 0;
        adapter.setBooksList(currentList);
    }

    @Override
    public void handlerManager() {
        String identifierParentHandler = getIntent().getStringExtra("identifierParent");
        fromHandler = getIntent().getStringExtra("from");
        if(identifierParentHandler != null){
            fromHandler = identifierParentHandler;
        }
    }

    @Override
    public void findViews() {
        booksRec = findViewById(R.id.recBookList);
        parentLayout = findViewById(R.id.parentLayout);
        editToolBar = findViewById(R.id.editToolBar);
        selectAllTxt = findViewById(R.id.selectAllTxt);
        deleteTxt = findViewById(R.id.deleteTxt);
        unSelectAll = findViewById(R.id.unSelectAllTxt);
        cancelTxt = findViewById(R.id.cancelTxt);
        floater = findViewById(R.id.goUpBtn);
        floaterLayout = findViewById(R.id.floaterLayout);
    }

    @Override
    public void setActivityRec(String handler) {
        clickedNum = 0; //refreshing clickedNum
        NewBookRecViewAdapter.listNum = 0;
        currentList = currentListMaker(handler);
        sortedBy = BooksArray.getInstance(this).getSortBy();
        sortList(sortedBy);
        adapter = new NewBookRecViewAdapter(this, handler, null);
        adapter.setBooksList(currentList);
        booksRec.setAdapter(adapter);
        booksRec.setLayoutManager(new LinearLayoutManager(this));
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
            default:
                Collections.sort(currentList, (t1, t2) -> {
                    int num1 = Integer.parseInt(t1.getMainId());
                    int num2 = Integer.parseInt(t2.getMainId());
                    return Integer.compare(num1, num2);
                });
                break;
        }
        BooksArray.getInstance(this).updateSortBy(sortBy);
        if(isActivated){
            notifyAdapter();
        }
    }
}