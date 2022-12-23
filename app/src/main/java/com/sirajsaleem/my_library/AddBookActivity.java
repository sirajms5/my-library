package com.sirajsaleem.my_library;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AddBookActivity extends AppCompatActivity implements MethodsFactory {

    private EditText nameEditTxt;
    private EditText authorEditTxt;
    private EditText pagesNum;
    private EditText addNotesEditTxt;
    private String nameInput;
    private String authorInput;
    private String pagesNumInput;
    private String spinnerInput;
    private String handler;
    private String status;
    private String imgCode = "";
    private String imgSource = "";
    private String notes;
    private TextView nameError;
    private TextView authorError;
    private TextView publicationError;
    private TextView pagesError;
    private TextView isReadingError;
    private Button addBtn;
    private Button imgBtn;
    private Button fromGallery;
    private Button takePhoto;
    private Spinner yearSpinner;
    private ArrayList<Integer> yearsList;
    private RadioGroup isReadingRadioGroup;
    private ConstraintLayout parentLayout;
    public int mainId;
    private CheckBox myFavCheckBox;
    private NewBook incomingBook;
    private Dialog dialog;
    private ImageView bookImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        if (getIntent().getBooleanExtra("back", false)) {
            overridePendingTransition(R.anim.navigation_back_slide_in, R.anim.navigation_back_slide_out);
        }

        findViews();
        handlerManager();
        yearGenerator();
        autoSelector();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Book A New Book");
        }

        imgBtn.setOnClickListener(v -> {
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.select_image_dialog);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.setCancelable(true);
            dialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimation;
            dialog.show();
            fromGallery = dialog.findViewById(R.id.fromGallery);
            takePhoto = dialog.findViewById(R.id.takePhoto);
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
        });

        mainId = BooksArray.getInstance(this).getAllBooks().size();

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, yearsList);
        yearSpinner.setAdapter(adapter);

        addBtn.setOnClickListener(v -> {
            nameInput = nameEditTxt.getText().toString();
            authorInput = authorEditTxt.getText().toString();
            pagesNumInput = pagesNum.getText().toString();
            spinnerInput = yearSpinner.getSelectedItem().toString();
            notes = addNotesEditTxt.getText().toString();

            if (validityCheck()) {
                cancelErrors();
                checkImageSource(imgCode, imgSource);
                mainId++;
                incomingBook = new NewBook(nameInput, authorInput, spinnerInput, pagesNumInput, Integer.toString(mainId), myFavCheckBox.isChecked(), status, imgCode, imgSource, notes);

// Resource IDs will be non-final by default in Android Gradle Plugin version 8.0, avoid using them in switch case statements
                int radioBtn = isReadingRadioGroup.getCheckedRadioButtonId();
                if (radioBtn == R.id.currentlyReadingRadioBtn) {
                    BooksArray.getInstance(this).addToCurrentlyReadingBooks(incomingBook);
                    BooksArray.getInstance(this).addToAllBooks(incomingBook);
                } else if (radioBtn == R.id.finishedReadingRadioBtn) {
                    BooksArray.getInstance(this).addToAllBooks(incomingBook);
                    BooksArray.getInstance(this).addToAlreadyReadBooks(incomingBook);
                } else if (radioBtn == R.id.wishlistRadioBtn) {
                    BooksArray.getInstance(this).addToAllBooks(incomingBook);
                    BooksArray.getInstance(this).addToMyWishlistBooks(incomingBook);
                }
                if (myFavCheckBox.isChecked()) {
                    BooksArray.getInstance(this).addToMyFavoriteBooks(incomingBook);
                }
                goBack(nameInput);
            } else {
                Snackbar.make(parentLayout, "Fill fields with red text", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss", view -> {
                        })
                        .show();
            }
        });
    }

    private void checkPermissions(String key, String request) {
        boolean isGranted = BooksArray.getInstance(this).getPermission(key);
        if (key.equals("gallery")) {
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

    private void dialogBuilder(String key, String request) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission required");
        builder.setMessage("Allow My Library to access " + request + " on your device?");
        builder.setPositiveButton("ALLOW", (dialogInterface, i) -> {
            if (key.equals("gallery")) {
                BooksArray.getInstance(AddBookActivity.this).grantPermissions(key, true);
            } else {
                BooksArray.getInstance(AddBookActivity.this).grantPermissions(key, true);
            }
            checkPermissions(key, request);
        });
        builder.setNegativeButton("DENY", (dialogInterface, i) -> {
            if (key.equals("gallery")) {
                BooksArray.getInstance(AddBookActivity.this).grantPermissions(key, false);
            } else {
                BooksArray.getInstance(AddBookActivity.this).grantPermissions(key, false);
            }
            Snackbar.make(parentLayout, "Access Denied", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dismiss", view -> {

                    })
                    .show();
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void autoSelector() {
        switch (handler) {
            case "MyFavorites":
                myFavCheckBox.setChecked(true);
                break;
            case "CurrentlyReadingBooks":
                isReadingRadioGroup.check(R.id.currentlyReadingRadioBtn);
                break;
            case "AlreadyReadBooks":
                isReadingRadioGroup.check(R.id.finishedReadingRadioBtn);
                break;
            case "MyWishlist":
                isReadingRadioGroup.check(R.id.wishlistRadioBtn);
                break;
        }
    }

    private boolean isReadingRadioGroupChecker() {
        int radioBtn = isReadingRadioGroup.getCheckedRadioButtonId();
        if (radioBtn == R.id.currentlyReadingRadioBtn) {
            status = "Currently Reading";
        } else if (radioBtn == R.id.finishedReadingRadioBtn) {
            status = "Already Read";
        } else if (radioBtn == R.id.wishlistRadioBtn) {
            status = "On My Wishlist";
        }

        return radioBtn == R.id.currentlyReadingRadioBtn || radioBtn == R.id.finishedReadingRadioBtn || radioBtn == R.id.wishlistRadioBtn;
    }

    private void cancelErrors() {
        nameError.setText("");
        authorError.setText("");
        pagesError.setText("");
        publicationError.setText("");
        isReadingError.setText("");
    }

    private boolean validityCheck() {
        if (
                !nameInput.equals("")
                        && !authorInput.equals("")
                        && !pagesNumInput.equals("")
                        && !spinnerInput.equals("")
                        && isReadingRadioGroupChecker()
        ) {
            return true;
        } else {
            if (nameInput.equals("")) {
                nameError.setText(R.string.bookNameError);
                nameError.setTextColor(Color.RED);
            }
            if (authorInput.equals("")) {
                authorError.setText(R.string.authorNameError);
                authorError.setTextColor(Color.RED);
            }
            if (pagesNumInput.equals("")) {
                pagesError.setText(R.string.pagesNumError);
                pagesError.setTextColor(Color.RED);
            }
            if (spinnerInput.equals("")) {
                publicationError.setText(R.string.publicationYearError);
                publicationError.setTextColor(Color.RED);
            }
            if (!isReadingRadioGroupChecker()) {
                isReadingError.setText(R.string.isReadingError);
                isReadingError.setTextColor(Color.RED);
            }
            return false;
        }
    }

    private void yearGenerator() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        yearsList = new ArrayList<>();
        for (int i = year; i >= 1900; i--) {
            yearsList.add(i);
        }
        Collections.sort(yearsList, Collections.reverseOrder());
    }

    @Override
    public void goBack(String bookName) {
        Intent intent;
        if (handler.equals("MainActivity")) {
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            intent = new Intent(this, RecListActivity.class);
            intent.putExtra("from", handler);
        }
        intent.putExtra("bookName", bookName);
        if (bookName != null) {
            intent.putExtra("bookName", bookName);
        }
        intent.putExtra("back", true);
        startActivity(intent);
    }

    @Override
    public void handlerManager() {
        handler = getIntent().getStringExtra("from");
    }

    @Override
    public void findViews() {
        nameEditTxt = findViewById(R.id.nameEditTxt);
        authorEditTxt = findViewById(R.id.authorEditTxt);
        pagesNum = findViewById(R.id.pagesNum);
        nameError = findViewById(R.id.nameError);
        authorError = findViewById(R.id.authorError);
        publicationError = findViewById(R.id.publicationError);
        pagesError = findViewById(R.id.pagesError);
        addBtn = findViewById(R.id.addBtn);
        yearSpinner = findViewById(R.id.yearSpinner);
        parentLayout = findViewById(R.id.parentLayout);
        isReadingRadioGroup = findViewById(R.id.isReadingRadioGroup);
        isReadingError = findViewById(R.id.isReadingError);
        myFavCheckBox = findViewById(R.id.myFavCheckBox);
        imgBtn = findViewById(R.id.selectImgBtn);
        bookImg = findViewById(R.id.bookImg);
        addNotesEditTxt = findViewById(R.id.addNotesEditTxt);
    }

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

    public final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() != null && result.getResultCode() == RESULT_OK) {
                if (result.getData().getClipData() != null) { //clip data is the image we got
                    Uri selectedImage = result.getData().getData();
                    bookImg.setImageURI(selectedImage);
                    checkImageSource(selectedImage.toString(), "gallery");
                }
            }
        }
    });

    public final ActivityResultLauncher<Intent> cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getData() != null) {
                Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                bookImg.setImageBitmap(bitmap);
                // Creating a String for the Bitmap
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                String bitmapString = Base64.encodeToString(b, Base64.DEFAULT);
                checkImageSource(bitmapString, "camera");
            }
        }
    });

    private void checkImageSource(String format, String imgFrom) {
        switch (imgFrom) {
            case "gallery":
            case "camera":
                imgCode = format;
                imgSource = imgFrom;
                break;
            default:
                imgCode = "";
                imgSource = "no photo";
                break;
        }
    }
}