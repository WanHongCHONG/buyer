package com.harshit.androideatitserver.ViewHolder;

import android.icu.text.AlphabeticIndex;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.harshit.androideatitserver.Interface.ItemClickListener;
import com.harshit.androideatitserver.R;

public class OrderViewHolder extends RecyclerView.ViewHolder //implements View.OnClickListener,
        //View.OnLongClickListener,
       /* View.OnCreateContextMenuListener*/ {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;

    public Button btnEdit,btnRemove,btnDetail,btnDirection;

    // private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);

        btnDetail = (Button)itemView.findViewById(R.id.btnEdit);
        btnDirection = (Button)itemView.findViewById(R.id.btnDirection);
        btnRemove = (Button)itemView.findViewById(R.id.btnRemove);
        btnEdit = (Button)itemView.findViewById(R.id.btnEdit);

        //itemView.setOnClickListener(this);
        //itemView.setOnLongClickListener(this);
        //itemView.setOnCreateContextMenuListener(this);

    }

    /*

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select the action ");

        menu.add(0,0,getAdapterPosition(),"Update");
        menu.add(0,1,getAdapterPosition(),"Delete");

    }

    */
/*
    @Override
    public boolean onLongClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),true);
        return true;
    }
    */
}
