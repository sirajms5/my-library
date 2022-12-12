package com.sirajsaleem.my_library;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class MoreDetailsActivity extends AppCompatActivity implements MethodsFactory{

    private TextView bookName;
    private TextView authorName;
    private TextView publicationYear;
    private TextView pagesNum;
    private TextView notes;
    private TextView readingStatus;
    private TextView editNotes;
    private String fromHandler;
    private String idHandler;
    private String statusTxt;
    private String btnTxt;
    private String identifierParent;
    private String deletedBookName;
    private Button favBtn;
    private Button deleteBtn;
    private Button wishlistBtn;
    private Button alreadyReadBtn;
    private Button currentlyReadingBtn;
    private NewBook myBook;
    private ImageView bookImg;
    private ScrollView parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details);

        if(getIntent().getBooleanExtra("back", false)){
            overridePendingTransition(R.anim.navigation_back_slide_in, R.anim.navigation_back_slide_out);
        }

        findViews();
        handlerManager();

        myBook = BooksArray.getInstance(this).getBookById(idHandler);
        setPage();
        manageBtnVisibilityBeforeClick(statusTxt);

        favBtn.setOnClickListener(v -> {
            btnTxt = favBtn.getText().toString();
            if(myBook.isFav()) {
                for (NewBook e : BooksArray.getInstance(this).getMyFavoriteBooks()) {
                    if (idHandler.equals(e.getMainId())) {
                        myBook.setFav(false);
                        favBtnText(myBook);
                        BooksArray.getInstance(this).updateBookList(null, "fav", myBook);
                        BooksArray.getInstance(this).removeBook(e, "myFavoriteBooks");
                        break;
                    }
                }
            } else {
                        manageAddBook(btnTxt);
                    }
        });

        deleteBtn.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete " + myBook.getBookName() + " from your library?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                deletedBookName = myBook.getBookName();
                BooksArray.getInstance(MoreDetailsActivity.this).removeBook(myBook, "allBooks");
                BooksArray.getInstance(MoreDetailsActivity.this).removeBook(myBook, "currentlyReadingBooks");
                BooksArray.getInstance(MoreDetailsActivity.this).removeBook(myBook, "alreadyReadBooks");
                BooksArray.getInstance(MoreDetailsActivity.this).removeBook(myBook, "myWishlistBooks");
                BooksArray.getInstance(MoreDetailsActivity.this).removeBook(myBook, "myFavoriteBooks");
                BooksArray.getInstance(MoreDetailsActivity.this).removeBook(myBook, "searchList");
                goBack();
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> {

            });
            builder.create().show();
        });

        currentlyReadingBtn.setOnClickListener(v -> {
            currentlyReadingBtn.setVisibility(View.GONE);
            alreadyReadBtn.setVisibility(View.VISIBLE);
            wishlistBtn.setVisibility(View.VISIBLE);
            readingStatus.setText(R.string.currently_reading);
            btnTxt = currentlyReadingBtn.getText().toString();
            manageAddBook(btnTxt);

        });

        alreadyReadBtn.setOnClickListener(v -> {
            alreadyReadBtn.setVisibility(View.GONE);
            currentlyReadingBtn.setVisibility(View.VISIBLE);
            wishlistBtn.setVisibility(View.VISIBLE);
            readingStatus.setText(R.string.already_read);
            btnTxt = alreadyReadBtn.getText().toString();
            manageAddBook(btnTxt);
        });

        wishlistBtn.setOnClickListener(v -> {
            wishlistBtn.setVisibility(View.GONE);
            currentlyReadingBtn.setVisibility(View.VISIBLE);
            alreadyReadBtn.setVisibility(View.VISIBLE);
            readingStatus.setText(R.string.on_my_wishlist);
            btnTxt = wishlistBtn.getText().toString();
            manageAddBook(btnTxt);
        });

        editNotes.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditNotesActivity.class);
            intent.putExtra("from", "MoreDetailsActivity");
            intent.putExtra("recList", fromHandler);
            intent.putExtra("id", myBook.getMainId());
            intent.putExtra("identifierParent", identifierParent);
            startActivity(intent);
        });
    }

    private void setPage() {
        String title = myBook.getBookName();
        statusTxt = myBook.getStatus();
        String note = getString(R.string.tab) + myBook.getNotes();
        bookName.setText(myBook.getBookName());
        authorName.setText(myBook.getBookAuthor());
        publicationYear.setText(myBook.getPublicationYear());
        pagesNum.setText(myBook.getPagesNum());
        notes.setText(note);
        readingStatus.setText(myBook.getStatus());
        favBtnText(myBook);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }
        switch(myBook.getImgSource()){
            case "gallery":
                Glide.with(this)
                        .asBitmap()
                        .load(myBook.getBookUri())
                        .into(bookImg);
                break;
            case "camera":
                Glide.with(this)
                        .asBitmap()
                        .load(myBook.getBookBitmap())
                        .into(bookImg);
                break;
            default:
                Glide.with(this)
                        .asBitmap()
                        .load(R.mipmap.books)
                        .into(bookImg);
                break;
        }
    }

    private void manageAddBook(String btnTxt) {
        switch(btnTxt){
            case "Add to favorites":
                myBook.setFav(true);
                favBtnText(myBook);
                BooksArray.getInstance(this).addToMyFavoriteBooks(myBook);
                BooksArray.getInstance(this).updateBookList(null, "fav", myBook);
                break;
            case "Currently Reading":
                myBook.setStatus("Currently Reading");
                BooksArray.getInstance(this).addToCurrentlyReadingBooks(myBook);
                BooksArray.getInstance(this).updateBookList("Currently Reading", "Reading Status", myBook);
                BooksArray.getInstance(this).removeBook(myBook, "alreadyReadBooks");
                BooksArray.getInstance(this).removeBook(myBook, "myWishlistBooks");
                break;
            case "Finished Reading":
                myBook.setStatus("Already Read");
                BooksArray.getInstance(this).addToAlreadyReadBooks(myBook);
                BooksArray.getInstance(this).updateBookList("Finished Reading", "Reading Status", myBook);
                BooksArray.getInstance(this).removeBook(myBook, "currentlyReadingBooks");
                BooksArray.getInstance(this).removeBook(myBook, "myWishlistBooks");
                break;
            case "Add To Wishlist":
                myBook.setStatus("On My Wishlist");
                BooksArray.getInstance(this).addToMyWishlistBooks(myBook);
                BooksArray.getInstance(this).updateBookList("On My Wishlist", "Reading Status", myBook);
                BooksArray.getInstance(this).removeBook(myBook, "currentlyReadingBooks");
                BooksArray.getInstance(this).removeBook(myBook, "alreadyReadBooks");
                break;
        }
    }

    private void manageBtnVisibilityBeforeClick(String status) {
        switch(status){
            case "Currently Reading":
                currentlyReadingBtn.setVisibility(View.GONE);
                break;
            case "Already Read":
                alreadyReadBtn.setVisibility(View.GONE);
                break;
            case "On My Wishlist":
                wishlistBtn.setVisibility(View.GONE);
                break;
        }
    }


    private void favBtnText(NewBook e) {
                if (e.isFav()) {
                    favBtn.setText(R.string.remove_from_favorites);
                } else {
                    favBtn.setText(R.string.add_to_favorites);
                }
    }

    @Override
    public void onBackPressed() {
        fromHandler = "MainActivity";
        goBack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_details_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            goBack();
        } else {
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.select_image_dialog);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.setCancelable(true);
            dialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimation;
            dialog.show();

            Button fromGallery = dialog.findViewById(R.id.fromGallery);
            Button takePhoto = dialog.findViewById(R.id.takePhoto);
            fromGallery.setBackgroundColor(Color.WHITE);
            takePhoto.setBackgroundColor(Color.WHITE);

            fromGallery.setOnClickListener(j -> {
                dialog.dismiss();
                checkPermissions("gallery", "photos, media, and files");
            });

            takePhoto.setOnClickListener(j -> {
                dialog.dismiss();
                checkPermissions("camera", "camera");
            });
        }
        return super.onOptionsItemSelected(item);
    }



    private void checkPermissions(String key, String request) {
        boolean isGranted = BooksArray.getInstance(this).getPermission(key);
        if(key.equals("gallery")) {
            if (isGranted) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            } else {
                dialogBuilder(key, request);
            }
        } else {
            if (isGranted) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraResultLauncher.launch(intent);
            } else {
                dialogBuilder(key, request);
            }
        }
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() != null && result.getResultCode() == RESULT_OK) {
                if (result.getData().getClipData() != null) { //clip data is the image we got
                    Uri selectedImage = result.getData().getData();
                    bookImg.setImageURI(selectedImage);
                    BooksArray.getInstance(MoreDetailsActivity.this).updateExistingBook(myBook, selectedImage.toString(), "gallery");
                }
            }
        }
    });

    private final ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getData() != null) {
                Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                bookImg.setImageBitmap(bitmap);
                // Creating a String for the Bitmap
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                String bitmapString = Base64.encodeToString(b, Base64.DEFAULT);
                BooksArray.getInstance(MoreDetailsActivity.this).updateExistingBook(myBook, bitmapString, "camera");
            }
        }
    });

    private void dialogBuilder(String key, String request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission required");
        builder.setMessage("Allow My Library to access " + request + " on your device?");
        builder.setPositiveButton("ALLOW", (dialogInterface, i) -> {
            if(key.equals("gallery")){
                BooksArray.getInstance(MoreDetailsActivity.this).grantPermissions(key, true);
            } else {
                BooksArray.getInstance(MoreDetailsActivity.this).grantPermissions(key, true);
            }
            checkPermissions(key, request);
        });
        builder.setNegativeButton("DENY", (dialogInterface, i) -> {
            if(key.equals("gallery")){
                BooksArray.getInstance(MoreDetailsActivity.this).grantPermissions(key, false);
            } else {
                BooksArray.getInstance(MoreDetailsActivity.this).grantPermissions(key, false);
            }
            Snackbar.make(parentLayout, "Access Denied", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dismiss", view -> {

                    })
                    .show();
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    @Override
    public void goBack() {
        Intent intent;
        switch(fromHandler){
            case "search":
                intent = new Intent(this, SearchResultsActivity.class);
                break;
            case "MainActivity":
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                break;
            default:
                intent = new Intent(this, RecListActivity.class);
                break;
        }
        intent.putExtra("deletedBookName", deletedBookName);
        intent.putExtra("from", fromHandler);
        intent.putExtra("back", true);
        intent.putExtra("identifierParent", identifierParent);
        startActivity(intent);
    }

    @Override
    public void handlerManager() {
            idHandler = getIntent().getStringExtra("id");
            fromHandler = getIntent().getStringExtra("from");
            identifierParent = getIntent().getStringExtra("identifierParent");
        }

    @Override
    public void findViews() {
        bookName = findViewById(R.id.bookNameInDetails);
        authorName = findViewById(R.id.authorNameInDetails);
        publicationYear = findViewById(R.id.publicationYearInDetails);
        pagesNum = findViewById(R.id.pagesNumberInDetails);
        notes = findViewById(R.id.notesInDetails);
        favBtn = findViewById(R.id.favBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        wishlistBtn = findViewById(R.id.wishlistBtn);
        alreadyReadBtn = findViewById(R.id.alreadyReadBtn);
        currentlyReadingBtn = findViewById(R.id.currentlyReadingBtn);
        readingStatus = findViewById(R.id.readingStatus);
        editNotes = findViewById(R.id.editNotes);
        bookImg = findViewById(R.id.bkImg);
        parentLayout = findViewById(R.id.parentLayout);
    }
}