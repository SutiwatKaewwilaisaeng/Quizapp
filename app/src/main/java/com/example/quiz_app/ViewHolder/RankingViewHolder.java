package com.example.quiz_app.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.quiz_app.Interface.ItemClickListener;
import com.example.quiz_app.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class RankingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txt_name,txt_score;
    private ItemClickListener itemClickListener;


    public RankingViewHolder(@NonNull View itemView)
    {
        super(itemView);
        txt_name = itemView.findViewById(R.id.txt_name);
        txt_score = itemView.findViewById(R.id.txt_score);

        itemView.setOnClickListener(this);
    }

    public void setTxt_name(TextView txt_name) {
        this.txt_name = txt_name;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view)
    {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
