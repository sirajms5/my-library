package com.sirajsaleem.my_library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

public class EditNotesActivity extends AppCompatActivity implements MethodsFactory{

    private Button addNoteBtn;
    private EditText editTxt;
    private String fromHandler;
    private String recHandler;
    private String idHandler;
    private String identifierParent;
    private ConstraintLayout addNoteParentLayout;
    private NewBook myBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes);

        if(getIntent().getBooleanExtra("back", false)){
            overridePendingTransition(R.anim.navigation_back_slide_in, R.anim.navigation_back_slide_out);
        }

        findViews();
        handlerManager();
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Notes");
        }

        //getting window measurements
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if(height > 1700) {
                editTxt.setLineHeight(height / 30);
            } else {
                editTxt.setLineHeight(82);
            }
        } else {
            editTxt.setLineSpacing(30, 0.6f);
        }

        myBook = BooksArray.getInstance(this).getBookById(idHandler);
        if(myBook.getNotes().isEmpty()){
            editTxt.setHint(R.string.notes_hint);
        } else {
            editTxt.setText(myBook.getNotes());
        }

        addNoteBtn.setOnClickListener(v -> {
            String noteInput = editTxt.getText().toString();
            if(idHandler.equals(myBook.getMainId())){
                myBook.setNotes(noteInput);
                BooksArray.getInstance(this).updateBookList(noteInput, "notes", myBook);
            }
            myBook.setNotes(noteInput);
            Snackbar.make(addNoteParentLayout, "Note have been updated", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Dismiss", view -> goBack(null))
                    .show();
        });
    }

    @Override
    public void goBack(String string) {
        Intent intent;
        switch(fromHandler){
            case "MoreDetailsActivity":
                intent = new Intent(this, MoreDetailsActivity.class);
                intent.putExtra("from", recHandler);
                intent.putExtra("id", idHandler);
                intent.putExtra("identifierParent", identifierParent);
                break;
            case "search":
                intent = new Intent(this, SearchResultsActivity.class);
                intent.putExtra("from", fromHandler);
                intent.putExtra("identifierParent", identifierParent);
                break;
            case "MainActivity":
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                break;
            default:
                intent = new Intent(this, RecListActivity.class);
                intent.putExtra("from", fromHandler);
                break;
        }
        intent.putExtra("back", true);
        startActivity(intent);
    }

    @Override
    public void handlerManager() {
        fromHandler = getIntent().getStringExtra("from");
        recHandler = getIntent().getStringExtra("recList");
        idHandler = getIntent().getStringExtra("id");
        identifierParent = getIntent().getStringExtra("identifierParent");
    }

    @Override
    public void findViews() {
        //toolbar = findViewById(R.id.toolBar);
        addNoteBtn = findViewById(R.id.addNoteBtn);
        editTxt = findViewById(R.id.editNotes);
        addNoteParentLayout = findViewById(R.id.addNoteParentLayout);
    }

    @Override
    public void onBackPressed() {
        fromHandler = "MainActivity";
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
}