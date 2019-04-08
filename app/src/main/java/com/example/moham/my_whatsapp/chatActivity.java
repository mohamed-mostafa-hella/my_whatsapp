package com.example.moham.my_whatsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.moham.my_whatsapp.chat.chatmessagesadapter;
import com.example.moham.my_whatsapp.chat.medialistadapter;
import com.example.moham.my_whatsapp.chat.messege;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class chatActivity extends AppCompatActivity {

    private RecyclerView MuserList,imglist;
    private RecyclerView.LayoutManager mlayout,imglayout;
    private chatmessagesadapter madapter;
    private medialistadapter mediaadapter;
    private ArrayList<messege> list;
    private ArrayList<String> imgurilist;

    private String chatid,notificationKey,name;

    private DatabaseReference db,seenrefranse;

    private String lastmesscreator;
    private int lastmesscount;
    private Boolean lastmessisseen;

    ValueEventListener seenlistener,seenlistener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        setContentView(R.layout.activity_chat);


        chatid=getIntent().getExtras().getString("chatid");
        notificationKey=getIntent().getExtras().getString("notificationkey");
        name=getIntent().getExtras().getString("name");

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(chatActivity.this,MainHomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        list=new ArrayList<>();
        imgurilist=new ArrayList<>();
        intializethelistview();
        intializethelistview1();


        lastmessisseen=true;

        db=FirebaseDatabase.getInstance().getReference().child("chat").child(chatid);
        seenrefranse=FirebaseDatabase.getInstance().getReference().child("chat").child(chatid);

        ImageButton send=findViewById(R.id.send);
        ImageButton media=findViewById(R.id.media);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage();
            }
        });
        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getimgs();
            }
        });

        getmasseges();
    }

    private void seenmessage(final String userid){
        seenlistener=seenrefranse.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    String creator="";
                    if(child.child("creator").getValue() != null)
                        creator=child.child("creator").getValue().toString();
                    if(!creator.equals(userid)){
                        child.getRef().child("isseen").setValue(2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenlistener2=seenrefranse.child("lastmess").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String creator="";
                    int count=0;
                    boolean isseen=false;
                    if(dataSnapshot.child("creator").getValue() !=null){
                        creator=dataSnapshot.child("creator").getValue(String.class);
                    }
                    if(dataSnapshot.child("count").getValue() !=null){
                        count=dataSnapshot.child("count").getValue(Integer.class);
                    }
                    if(dataSnapshot.child("isseen").getValue() !=null){
                        isseen= dataSnapshot.child("isseen").getValue(Boolean.class);
                    }

                    setlastmessdata(creator,count,isseen);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setlastmessdata(String creator, int count,Boolean isseen) {
        lastmesscount=count;
        lastmesscreator=creator;
        lastmessisseen=isseen;
        if (!lastmesscreator.equals(FirebaseAuth.getInstance().getUid())){
            seenrefranse.child("lastmess").child("isseen").setValue(true);
            seenrefranse.child("lastmess").child("count").setValue(0);
        }
    }

    private void getimgs() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"select Picture(s)"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            if(data.getClipData() == null){
                imgurilist.add(data.getData().toString());
            }else{
                for(int i=0;i<data.getClipData().getItemCount();i++){
                    imgurilist.add(data.getClipData().getItemAt(i).getUri().toString());
                }
            }
            Toast.makeText(this,""+imgurilist.size(),Toast.LENGTH_SHORT).show();
            mediaadapter.notifyDataSetChanged();
        }
    }

    private void getmasseges(){
        DatabaseReference messageref=db.child("messages");
        messageref.keepSynced(true);
        messageref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String text = "",creator="";
                int isseen=1;
                Boolean upload_state=false;
                ArrayList<String> mediaurllist = new ArrayList<>();

                if(dataSnapshot.child("text").getValue() != null)
                    text=dataSnapshot.child("text").getValue(String.class);
                if(dataSnapshot.child("isseen").getValue() != null)
                    isseen= dataSnapshot.child("isseen").getValue(Integer.class);
                if(dataSnapshot.child("creator").getValue() != null)
                    creator=dataSnapshot.child("creator").getValue(String.class);
                if(dataSnapshot.child("upload_state").getValue() != null){
                    upload_state=dataSnapshot.child("upload_state").getValue(Boolean.class);
                }
                if(dataSnapshot.child("media").getChildrenCount() > 0)
                    for(DataSnapshot mediasnap : dataSnapshot.child("media").getChildren())
                        mediaurllist.add(mediasnap.getValue(String.class));

                messege newmessege = new messege(dataSnapshot.getKey(),text,creator,mediaurllist,isseen,upload_state);
                list.add(newmessege);
                mlayout.scrollToPosition(list.size()-1);
                madapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String key=dataSnapshot.getKey();
                messege child;
                for(int i=list.size()-1 ; i>=0 ; i--){
                    child=list.get(i);
                    if(child.getMessageid().equals(key)){
                        if(dataSnapshot.child("isseen").getValue() != null)
                            child.setIsseen(dataSnapshot.child("isseen").getValue(Integer.class));

                        if(dataSnapshot.child("upload_state").getValue()!=null && dataSnapshot.child("media").getChildrenCount() > 0)
                        {
                            child.cleararr();
                            for(DataSnapshot mediasnap : dataSnapshot.child("media").getChildren())
                            {
                                child.addelemtolist(mediasnap.getValue(String.class));
                            }
                        }

                        if(dataSnapshot.child("progress").getValue() != null){
                            child.setProgres(dataSnapshot.child("progress").getValue(Integer.class));
                        }

                        if(dataSnapshot.child("upload_state").getValue() != null){
                            child.setUpload_state(dataSnapshot.child("upload_state").getValue(Boolean.class));
                        }

                        madapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    EditText editText;
    ArrayList<String> mediaidlist = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    private void sendmessage(){
        editText=findViewById(R.id.messege);
        String mesID=db.push().getKey();
        final DatabaseReference databaseReference = db.child("messages").child(mesID);
        databaseReference.keepSynced(true);

        final Map newmassege=new HashMap<>();

        newmassege.put("creator",FirebaseAuth.getInstance().getUid());
        newmassege.put("isseen",0);

        if(!editText.getText().toString().isEmpty()) {
            newmassege.put("text", editText.getText().toString());
        }

        if(!imgurilist.isEmpty()){

            newmassege.put("/media/"+1+"/","default");
            newmassege.put("upload_state",true);
            newmassege.put("progress",0);
            updatedatawithnewmessage(databaseReference,newmassege , imgurilist , chatid , mesID);

        }else if (!editText.getText().toString().isEmpty()){
            newmassege.put("upload_state",false);
            updatedatawithnewmessage(databaseReference,newmassege  , imgurilist , chatid , mesID );
        }

    }

    private void updatedatawithnewmessage(DatabaseReference databaseReference, Map newmassege , List<String> imguri , String chatid  , String messID) {
//        if(!editText.getText().toString().isEmpty()) {
//            new SendNotification(editText.getText().toString(),name,notificationKey,this);
//        }else{
//            new SendNotification("media message",name,notificationKey,this);
//        }


        final DatabaseReference ref=databaseReference;
        ref.keepSynced(true);
        ref.updateChildren(newmassege).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(getApplicationContext() , "done ....." , Toast.LENGTH_SHORT).show();
                ref.child("isseen").setValue(1);
            }
        });

        if(newmassege.get("upload_state") != null && (Boolean) newmassege.get("upload_state") )
        {
            upload(imguri , chatid , messID);
            Toast.makeText(getApplicationContext() , "uploading..." , Toast.LENGTH_SHORT).show();
        }


        db.child("lastmess").child("creator").setValue(FirebaseAuth.getInstance().getUid());
        db.child("lastmess").child("count").setValue(lastmesscount+1);
        db.child("lastmess").child("isseen").setValue(false);
        db.child("lastmess").child("text").setValue(newmassege.get("text"));

        imgurilist.clear();
        mediaidlist.clear();
        editText.setText(null);
        mediaadapter.notifyDataSetChanged();
    }

    void upload (List<String> imguri , final String chatid  , final String messID){

        int cunter=1;
        final int imgurilistsize=imguri.size();

        for(String mediURI : imguri){

            final String mediaID=""+cunter;
            final int flagh=cunter;

            final StorageReference filepath= FirebaseStorage.getInstance().getReference().child("chat").child(chatid).child(messID).child(mediaID);

            UploadTask uploadTask=filepath.putFile(Uri.parse(mediURI));

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String , Object> update_map=new HashMap<>();
                            update_map.put("/media/"+mediaID+"/",""+uri.toString());

                            if(imgurilistsize == flagh){
                                update_map.put("upload_state",false);
                            }
                            double progress = (100.0 * (flagh ) ) / (imgurilistsize ) ;
                            update_map.put("progress",(int)progress);


                            FirebaseDatabase.getInstance().getReference("chat").child(chatid).child("messages").child(messID).updateChildren(update_map);

                        }
                    });
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(getApplicationContext(),"error the upload task is canceled try in anther time please ...." , Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    if(imgurilistsize == 1){

                        double progress = (100.0 * taskSnapshot.getBytesTransferred() ) / taskSnapshot.getBytesTransferred() ;

                        FirebaseDatabase.getInstance().getReference("chat").child(chatid).child("messages").child(messID).child("progress").setValue( (int) progress );

                    }

                }
            });
            cunter++;


        }
    }


    private void intializethelistview() {
        MuserList=findViewById(R.id.messagelist);
        MuserList.setNestedScrollingEnabled(false);
        MuserList.setHasFixedSize(false);

        mlayout=new LinearLayoutManager(getApplicationContext(),LinearLayout.VERTICAL,false);
        MuserList.setLayoutManager(mlayout);

        madapter=new chatmessagesadapter(list,getApplicationContext());

        MuserList.setAdapter(madapter);
    }

    private void intializethelistview1() {
        imglist=findViewById(R.id.imglist);
        imglist.setNestedScrollingEnabled(false);
        imglist.setHasFixedSize(false);

        imglayout=new LinearLayoutManager(getApplicationContext(),LinearLayout.HORIZONTAL,false);
        imglist.setLayoutManager(imglayout);

        mediaadapter=new medialistadapter(this,imgurilist);

        imglist.setAdapter(mediaadapter);
    }

    private void stats(String stat){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String,Object> map=new HashMap<>();
        map.put("onoff",stat);
        reference.updateChildren(map);
    }


    @Override
    protected void onStart() {
        super.onStart();
        seenmessage(FirebaseAuth.getInstance().getUid());
    }

    @Override
    protected void onResume() {
        super.onResume();
        stats("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stats("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        seenrefranse.child("messages").removeEventListener(seenlistener);
        seenrefranse.child("lastmess").removeEventListener(seenlistener2);
    }
}
