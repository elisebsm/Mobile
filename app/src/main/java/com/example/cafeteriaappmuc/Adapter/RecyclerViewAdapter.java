package com.example.cafeteriaappmuc.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.example.cafeteriaappmuc.GlideApp;
import com.example.cafeteriaappmuc.R;


import androidx.recyclerview.widget.RecyclerView;

import com.example.cafeteriaappmuc.Objects.ImageUploadInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {


   // private String recyclerViewImage;

    private Context context;
    private List<ImageUploadInfo> MainImageUploadInfoList;
    private String dishName,foodService;
    private StorageReference imagesRef;

    // Creating RecyclerView.
    private RecyclerView recyclerView;


    public RecyclerViewAdapter(Context context, List<ImageUploadInfo> TempList,String foodService, String dishName) {

        this.MainImageUploadInfoList = TempList;
        this.context = context;
        this.foodService = foodService;
        this.dishName= dishName;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);


        return viewHolder;


    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);

        String imageName= UploadInfo.getImageName();

        //getting image from firebase or cache
        StorageReference storageReference =FirebaseStorage.getInstance().getReference();
        imagesRef = storageReference.child("images/"+foodService+"/"+dishName+"/"+imageName);

        GlideApp
                .with(context)
                .load(imagesRef)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
       // public TextView imageNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageViewRecycler);


        }
    }







}