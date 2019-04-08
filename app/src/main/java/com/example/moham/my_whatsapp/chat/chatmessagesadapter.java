package com.example.moham.my_whatsapp.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moham.my_whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;



public class chatmessagesadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<messege> list;
    private Context context;
    private final int imageandtext=111,image=222,imageandtextright=333,imageright=444,textright=555;

    public chatmessagesadapter(ArrayList<messege> list,Context context) {
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v;
        LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
        if(i==imageandtext || i==imageandtextright) {
            if(i==imageandtextright)
                v = inflater.inflate(R.layout.messageimageandtextviewright, viewGroup, false);
            else
                v = inflater.inflate(R.layout.messageimageandtextview, viewGroup, false);
            return new imageandtextviewholder (v);
        }
        else if(i==image || i==imageright) {
            if (i==imageright)
                v = inflater.inflate(R.layout.messageimageitemviewright, viewGroup, false);
            else
                v = inflater.inflate(R.layout.messageimageitemview, viewGroup, false);
            return new imageviewholder(v);
        }
        else {
            if(i==textright)
                v = inflater.inflate(R.layout.messageitemviewright, null, false);
            else
                v = inflater.inflate(R.layout.messageitemview, null, false);
            return new textviewholder (v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int type=getItemViewType(i);
        if(type==imageandtext || type == imageandtextright) {
            imageandtextviewholder imageandtextviewholder= (imageandtextviewholder) viewHolder;

            if(list.get(i).getMediaurllist().get(0).equals("default"))
                Glide.with(context).load(R.drawable.default_image).into(imageandtextviewholder.images);
            else
                Glide.with(context).load(list.get(i).getMediaurllist().get(0)).into(imageandtextviewholder.images);

            imageandtextviewholder.numberofimages.setText(""+list.get(i).getMediaurllist().size());
            imageandtextviewholder.message.setText(list.get(i).getMessage());

            if(list.get(i).getUpload_state()){
                imageandtextviewholder.progress.setVisibility(View.VISIBLE);
            }else{
                imageandtextviewholder.progress.setVisibility(View.GONE);
            }

            imageandtextviewholder.progress.setText(""+list.get(i).getProgres()+"%");

            if (list.get(i).isIsseen()==2){
                Glide.with(context).load(R.drawable.ic_seen).into(imageandtextviewholder.isseen);
            }else if(list.get(i).isIsseen()==1){
                Glide.with(context).load(R.drawable.ic_deleverd).into(imageandtextviewholder.isseen);
            }else{
                Glide.with(context).load(R.drawable.upload).into(imageandtextviewholder.isseen);
            }
        }
        else if(type==image || type == imageright) {
            imageviewholder imageviewholder=(imageviewholder)viewHolder;

            if(list.get(i).getMediaurllist().get(0).equals("default"))
                Glide.with(context).load(R.drawable.default_image).into(imageviewholder.images);
            else
                Glide.with(context).load(list.get(i).getMediaurllist().get(0)).into(imageviewholder.images);

            imageviewholder.numberofimages.setText(""+list.get(i).getMediaurllist().size());

            if(list.get(i).getUpload_state()){
                imageviewholder.progress.setVisibility(View.VISIBLE);
            }else{
                imageviewholder.progress.setVisibility(View.GONE);
            }

            imageviewholder.progress.setText(""+list.get(i).getProgres()+"%");

            if (list.get(i).isIsseen()==2){
                Glide.with(context).load(R.drawable.ic_seen).into(imageviewholder.isseen);
            }else if(list.get(i).isIsseen()==1){
                Glide.with(context).load(R.drawable.ic_deleverd).into(imageviewholder.isseen);
            }else{
                Glide.with(context).load(R.drawable.upload).into(imageviewholder.isseen);
            }
        }
        else {
            textviewholder  textviewholder =(textviewholder ) viewHolder;
            textviewholder.message.setText(list.get(i).getMessage());

            if (list.get(i).isIsseen()==2){
                Glide.with(context).load(R.drawable.ic_seen).into(textviewholder.isseen);
            }else if(list.get(i).isIsseen()==1){
                Glide.with(context).load(R.drawable.ic_deleverd).into(textviewholder.isseen);
            }else{
                Glide.with(context).load(R.drawable.upload).into(textviewholder.isseen);
            }

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getMediaurllist().size()>0 && !list.get(position).getMessage().equals("")) {

            if (list.get(position).getSenderid().equals(FirebaseAuth.getInstance().getUid())){
                return imageandtextright;
            }else
                return imageandtext;

        }
        else if(list.get(position).getMediaurllist().size()>0) {

            if (list.get(position).getSenderid().equals(FirebaseAuth.getInstance().getUid())){
                return imageright;
            }else
                return image;

        }
        else {

            if (list.get(position).getSenderid().equals(FirebaseAuth.getInstance().getUid())){
                return textright;
            }else
                return super.getItemViewType(position);

        }
    }

    public class textviewholder  extends RecyclerView.ViewHolder{
        public TextView message;
        private ImageView isseen;
        public textviewholder (@NonNull View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.message);
            isseen=itemView.findViewById(R.id.seen);
        }
    }

    public class imageviewholder  extends RecyclerView.ViewHolder{
        private ImageView images,isseen;
        private  TextView numberofimages,progress;

        public imageviewholder (@NonNull View itemView) {
            super(itemView);
            images=itemView.findViewById(R.id.messageimage);
            isseen=itemView.findViewById(R.id.seen);
            progress=itemView.findViewById(R.id.progress);
            numberofimages=itemView.findViewById(R.id.numberofimages);

            images.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(list.get(getAdapterPosition()).getMediaurllist().size()>0) {
                        new ImageViewer.Builder(v.getContext(), list.get(getAdapterPosition()).getMediaurllist())
                                .setStartPosition(0)
                                .show();
                    }
                }
            });
        }

    }

    public class imageandtextviewholder  extends RecyclerView.ViewHolder{
        private TextView message,numberofimages,progress;
        private ImageView images,isseen;

        public imageandtextviewholder (@NonNull View itemView) {
            super(itemView);
            message=itemView.findViewById(R.id.mess);
            images=itemView.findViewById(R.id.messageimage);
            isseen=itemView.findViewById(R.id.seen);
            progress=itemView.findViewById(R.id.progress);
            numberofimages=itemView.findViewById(R.id.numberofimages);

            images.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(list.get(getAdapterPosition()).getMediaurllist().size()>0) {
                        new ImageViewer.Builder(v.getContext(), list.get(getAdapterPosition()).getMediaurllist())
                                .setStartPosition(0)
                                .show();
                    }
                }
            });
        }
    }

}
