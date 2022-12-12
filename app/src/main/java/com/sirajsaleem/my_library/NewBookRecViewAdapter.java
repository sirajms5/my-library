package com.sirajsaleem.my_library;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class NewBookRecViewAdapter extends RecyclerView.Adapter<NewBookRecViewAdapter.ViewHolder> {

    private ArrayList<NewBook> booksList = new ArrayList<>();
    private final Context context;
    private final String identifier;
    private final String identifierParent;
    public static int listNum = 0;

    public NewBookRecViewAdapter(Context context, String identifier, String identifierParent) {
        this.context = context;
        this.identifier = identifier;
        this.identifierParent = identifierParent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        listNum++;
        if(booksList.get(position).getListId() == 0){
            String idText = context.getString(R.string.empty_space) + listNum;
            holder.idNum.setText(idText);
            booksList.get(position).setListId(listNum);
        } else {
            String idText = context.getString(R.string.empty_space) + booksList.get(position).getListId();
            holder.idNum.setText(idText);
        }
        String authorNameText = context.getString(R.string.empty_space) + booksList.get(position).getBookAuthor();
        String publicationYearText = context.getString(R.string.empty_space) + booksList.get(position).getPublicationYear();
        String pagesNumText = context.getString(R.string.empty_space) + booksList.get(position).getPagesNum();
        holder.bookName.setText(booksList.get(position).getBookName());
        holder.authorName.setText(authorNameText);
        holder.publicationYear.setText(publicationYearText);
        holder.pagesNumber.setText(pagesNumText);
        switch(booksList.get(position).getImgSource()){
            case "gallery":
                Glide.with(context)
                        .asBitmap()
                        .load(booksList.get(position).getBookUri())
                        .into(holder.bookImg);
                break;
            case "camera":
                Glide.with(context)
                        .asBitmap()
                        .load(booksList.get(position).getBookBitmap())
                        .into(holder.bookImg);
                break;
            default:
                Glide.with(context)
                        .asBitmap()
                        .load(R.mipmap.books)
                        .into(holder.bookImg);
                break;
        }

        //checkbox holder manager for each holder's checkbox to prevent stacking results on other holders
        switch(booksList.get(position).getIsChecked()){
            case "checked":
                holder.bookCheckbox.setChecked(true);
                holder.bookCheckbox.setVisibility(View.VISIBLE);
                break;
            case "unChecked":
                holder.bookCheckbox.setChecked(false);
                holder.bookCheckbox.setVisibility(View.VISIBLE);
                break;
            default:
                holder.bookCheckbox.setChecked(false);
                break;
        }


        if(RecListActivity.editClicked || SearchResultsActivity.editClicked){
            holder.bookCheckbox.setVisibility(View.VISIBLE);
        } else {
            holder.bookCheckbox.setVisibility(View.GONE);
        }

        //managing expanded card for each holder
        if(booksList.get(position).isExpanded()){
            holder.expandArrow.setVisibility(View.GONE);
            holder.collapseArrow.setVisibility(View.VISIBLE);
            holder.expandedCard.setVisibility(View.VISIBLE);
        } else {
            holder.expandArrow.setVisibility(View.VISIBLE);
            holder.collapseArrow.setVisibility(View.GONE);
            holder.expandedCard.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }

    public void setBooksList(ArrayList<NewBook> currentBooksList) {
        this.booksList = currentBooksList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView idNum;
        private final TextView bookName;
        private final TextView authorName;
        private final TextView publicationYear;
        private final TextView pagesNumber;
        private final TextView expandArrow;
        private final TextView collapseArrow;
        private final RelativeLayout expandedCard;
        private final ImageView bookImg;
        private final CheckBox bookCheckbox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CardView cardParent = itemView.findViewById(R.id.cardParent);
            idNum = itemView.findViewById(R.id.idNum);
            bookName = itemView.findViewById(R.id.bookName);
            authorName = itemView.findViewById(R.id.authorName);
            publicationYear = itemView.findViewById(R.id.publicationYear);
            pagesNumber = itemView.findViewById(R.id.pagesNumber);
            expandArrow = itemView.findViewById(R.id.expandArrow);
            expandedCard = itemView.findViewById(R.id.expandedCard);
            collapseArrow = itemView.findViewById(R.id.collapsedArrow);
            TextView addNotes = itemView.findViewById(R.id.addNotes);
            TextView moreDetails = itemView.findViewById(R.id.moreDetails);
            bookImg = itemView.findViewById(R.id.bookImage);
            bookCheckbox = itemView.findViewById(R.id.bookCheckbox);

            expandArrow.setOnClickListener(v -> {
                expandArrow.setVisibility(View.GONE);
                collapseArrow.setVisibility(View.VISIBLE);
                expandedCard.setVisibility(View.VISIBLE);
                booksList.get(getAdapterPosition()).setExpanded(true);
            });

            collapseArrow.setOnClickListener(v -> {
                expandArrow.setVisibility(View.VISIBLE);
                collapseArrow.setVisibility(View.GONE);
                expandedCard.setVisibility(View.GONE);
                booksList.get(getAdapterPosition()).setExpanded(false);
            });

            addNotes.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditNotesActivity.class);
                identifierManager(intent, identifier, identifierParent);
            });

            cardParent.setOnClickListener(v -> {
                Intent intent = new Intent(context, MoreDetailsActivity.class);
                identifierManager(intent, identifier, identifierParent);
            });

            moreDetails.setOnClickListener(v -> {
                Intent intent = new Intent(context, MoreDetailsActivity.class);
                identifierManager(intent, identifier, identifierParent);
            });

            bookCheckbox.setOnCheckedChangeListener((compoundButton, isClicked) -> {

                if(isClicked){
                    if(!RecListActivity.allSelectedAdapterChecker) {
                        booksList.get(getAdapterPosition()).setIsChecked("checked");
                        booksList.get(getAdapterPosition()).setAdd(true);
                        RecListActivity.booksToDelete.add(booksList.get(getAdapterPosition()));
                    } else if(!SearchResultsActivity.allSelectedAdapterChecker){
                        booksList.get(getAdapterPosition()).setIsChecked("checked");
                        booksList.get(getAdapterPosition()).setAdd(true);
                        SearchResultsActivity.booksToDelete.add(booksList.get(getAdapterPosition()));
                    }
                } else {
                    booksList.get(getAdapterPosition()).setIsChecked("unChecked");
                    booksList.get(getAdapterPosition()).setAdd(false);
                    RecListActivity.booksToDelete.remove(booksList.get(getAdapterPosition()));
                    SearchResultsActivity.booksToDelete.remove(booksList.get(getAdapterPosition()));
                }

                int add = 0;
                for(NewBook e: booksList){
                    if(e.isAdd()){
                        add++;
                    }
                }
                RecListActivity.clickedNum = add;
                SearchResultsActivity.clickedNum = add;
            });
        }

        private void identifierManager(Intent intent, String identifier, String identifierParent) {
            intent.putExtra("identifierParent", identifierParent);
            intent.putExtra("id", booksList.get(getAdapterPosition()).getMainId());
            intent.putExtra("from", identifier);
            context.startActivity(intent);
        }
    }
}
