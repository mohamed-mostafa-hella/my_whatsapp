package com.example.moham.my_whatsapp.Fragment;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.moham.my_whatsapp.R;
import com.example.moham.my_whatsapp.edit_activity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.transform.Result;

import static android.app.Activity.RESULT_OK;

public class brofileFragment extends Fragment {


    private ImageView profileImage;
    private TextView username,stats,number;
    private ImageButton editname,editphoto;
    private ProgressBar progressBar;

    private FirebaseUser cuser;
    private DatabaseReference reference;

    private Boolean uploadagain;

    private StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private StorageTask<UploadTask.TaskSnapshot> uploadtask;
    private Uri imageuri;
    private Context context;
    private String imageurinow;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_brofile, container, false);

        profileImage=v.findViewById(R.id.Profile_image);
        username=v.findViewById(R.id.user_name);
        stats=v.findViewById(R.id.Stats);
        number=v.findViewById(R.id.number);
        editname=v.findViewById(R.id.editname);
        editphoto=v.findViewById(R.id.editphoto);
        progressBar=v.findViewById(R.id.progress_bar);

        uploadagain=true;
        cuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("user").child(cuser.getUid());

        storageReference= FirebaseStorage.getInstance().getReference().child("usersimage").child(FirebaseAuth.getInstance().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.child("name").getValue()!=null){
                        username.setText(dataSnapshot.child("name").getValue().toString());
                    }

                    if(dataSnapshot.child("imageuri").getValue()!=null){
                        setimageurinow(dataSnapshot.child("imageuri").getValue().toString());
                        if(dataSnapshot.child("imageuri").getValue().toString().equals("default"))
                            Glide.with(context).load(R.drawable.person).into(profileImage);
                        else
                            Glide.with(context).load(dataSnapshot.child("imageuri").getValue().toString()).into(profileImage);
                    }

                    if(dataSnapshot.child("stats").getValue()!=null){
                        stats.setText(dataSnapshot.child("stats").getValue().toString());
                    }

                    if(dataSnapshot.child("phone").getValue()!=null){
                        number.setText(dataSnapshot.child("phone").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImag();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageurinow.equals("default")) {
                    Toast.makeText(context,"there is no photo yet",Toast.LENGTH_LONG).show();
                }
                else {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(imageurinow);
                    new ImageViewer.Builder(v.getContext(), list)
                            .setStartPosition(0)
                            .show();
                }
            }
        });

        editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, edit_activity.class);
                intent.putExtra("type","name");
                intent.putExtra("data",username.getText());
                startActivity(intent);
            }
        });

        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, edit_activity.class);
                intent.putExtra("type","stats");
                intent.putExtra("data",stats.getText());
                startActivity(intent);
            }
        });
        return  v;
    }

    private void setimageurinow(String imageuri) {
        this.imageurinow=imageuri;
    }

    public void setcontext(Context context){
        this.context=context;
    }


    private void openImag() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select Picture(s)"),IMAGE_REQUEST);
    }

    private String getFileextiontion(Uri uri){
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void uploadImage(){
        progressBar.setVisibility(View.VISIBLE);
        uploadagain=false;
        if(imageuri !=null) {
            final StorageReference filereference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileextiontion(imageuri));

            uploadtask = filereference.putFile(imageuri);
            uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            reference = FirebaseDatabase.getInstance().getReference("user").child(cuser.getUid());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageuri", uri.toString());
                            reference.updateChildren(map);
                            progressBar.setVisibility(View.GONE);
                            uploadagain=true;
                        }
                    });
                }
            });

        }else {
            Toast.makeText(getContext(),"No Image Selected" , Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            uploadagain=true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_REQUEST && resultCode == RESULT_OK
                && data!=null && data.getData() != null){

            imageuri=data.getData();
            if(!uploadagain){
                Toast.makeText(getContext(),"upload is in process" , Toast.LENGTH_LONG).show();
            }else {
                uploadImage();
            }
        }

    }
}
