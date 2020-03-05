package com.harshit.androideatit.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.harshit.androideatit.Interface.ItemClickListener;
import com.harshit.androideatit.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView food_name,food_price;
    public ImageView food_image,fav_image,share_image,quick_cart;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        food_name = (TextView)itemView.findViewById(R.id.food_name);
        food_image = (ImageView)itemView.findViewById(R.id.food_image);
        fav_image = (ImageView)itemView.findViewById(R.id.fav);
        share_image = (ImageView)itemView.findViewById(R.id.btnShare);
        food_price = (TextView)itemView.findViewById(R.id.food_price);
        quick_cart = (ImageView)itemView.findViewById(R.id.btn_quick_cart);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}