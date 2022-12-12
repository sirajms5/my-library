package com.sirajsaleem.my_library;

import android.view.View;

public interface RecyclerViewMethods {
    void setActivityRec(String handler);
    void sortList(String sortBy);
    void refreshActivityControls();
    void removeEditToolBar();
    void showEditBar(final View view);
    void hideEditBar(final View view);
    void checkedManager(String handler);
    void notifyAdapter();
    void deleteAlertDialog();
}
