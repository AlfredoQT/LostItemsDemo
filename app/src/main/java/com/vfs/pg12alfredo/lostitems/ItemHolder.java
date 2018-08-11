package com.vfs.pg12alfredo.lostitems;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemHolder extends RecyclerView.ViewHolder {

    private CircleImageView userCircleImageView;
    private TextView userNameTextView;
    private ImageView imageView;
    private TextView nameTextView;
    private TextView descTextView;
    private TextView latTextView;
    private TextView lngTextView;
    private Button updateButton;
    private Button foundButton;

    public ItemHolder(View itemView) {
        super(itemView);

        // Get everything
        userCircleImageView = itemView.findViewById(R.id.row_item_user_profile);
        userNameTextView = itemView.findViewById(R.id.row_item_user_name);
        imageView = itemView.findViewById(R.id.row_item_image_image_view);
        nameTextView = itemView.findViewById(R.id.row_item_name_text_view);
        descTextView = itemView.findViewById(R.id.row_item_desc_text_view);
        latTextView = itemView.findViewById(R.id.row_item_lat_text_view);
        lngTextView = itemView.findViewById(R.id.row_item_lng_text_view);

        foundButton = itemView.findViewById(R.id.row_item_found_button);
        updateButton = itemView.findViewById(R.id.row_item_update_button);
    }

    public void setUserName(String name) {
        userNameTextView.setText(name);
    }

    public void setItemImage(String url) {

    }
}
