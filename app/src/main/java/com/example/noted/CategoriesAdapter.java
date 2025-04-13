package com.example.noted;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {
    List<NotesModel> categoryModelList;
    Context context;
    public CategoriesAdapter(List<NotesModel> categoryModelList, Context context){
        this.context = context;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public CategoriesAdapter.CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoriesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.CategoriesViewHolder holder, int position) {
        NotesModel categoryModel = categoryModelList.get(position);
        holder.button.setText(categoryModel.getCategoryTitle());
        holder.itemView.setOnClickListener(v -> {
            filterbycategory();
        });

    }

    private void filterbycategory() {

    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class CategoriesViewHolder extends RecyclerView.ViewHolder {
        Button button;
        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.btnCategory);
        }
    }
}
