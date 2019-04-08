package com.example.moham.my_whatsapp.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.moham.my_whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class choosestatsFragment extends Fragment {

    private static final String ARG_PARAM1 = "data";

    private choosestatsinterface mListener;
    private String data;

    private TextView stats;
    private ListView listoptions;
    private ImageButton editstats;

    private ArrayList<String> options;
    private ArrayList<Boolean> color;

    private DatabaseReference reference;


    public static choosestatsFragment newInstance(String param1) {
        choosestatsFragment fragment = new choosestatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference= FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getUid());
        if (getArguments() != null) {
            data = getArguments().getString(ARG_PARAM1);
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.child("stats").getValue()!=null){
                        data=dataSnapshot.child("stats").getValue().toString();
                        if(stats!=null){
                            stats.setText(data);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });




        options=new ArrayList<>();
        color=new ArrayList<>();

        options.add("Available");
        color.add(false);
        options.add("Busy");
        color.add(false);
        options.add("At School");
        color.add(false);
        options.add("At the movies");
        color.add(false);
        options.add("At work");
        color.add(false);
        options.add("Battery about to die");
        color.add(false);
        options.add("can not talk,WhatsApp only");
        color.add(false);
        options.add("In a meeting");
        color.add(false);
        options.add("At the gym");
        color.add(false);
        options.add("Sleeping");
        color.add(false);
        options.add("Urgent calls only");
        color.add(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_choosestats, container, false);

        stats=v.findViewById(R.id.stats);
        listoptions=v.findViewById(R.id.list_of_options);
        editstats=v.findViewById(R.id.edit_stats);


        stats.setText(data);


        return  v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editstats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.editstats(data);
            }
        });

        listadapter adapter=new listadapter();
        listoptions.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof choosestatsinterface) {
            mListener = (choosestatsinterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement choosestatsinterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface choosestatsinterface {
        void editstats(String data);
    }

    class  listadapter extends BaseAdapter{

        @Override
        public int getCount() {
            return options.size();
        }

        @Override
        public Object getItem(int position) {
            return options.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = getLayoutInflater().inflate(R.layout.listviewitemviewoption,null);
            final String opject=(String) getItem(position);
            final TextView option=v.findViewById(R.id.option);
            option.setText(opject);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference.child("stats").setValue(opject);
                    stats.setText(opject);
                    options.add(0,data);
                    color.add(0,false);
                    data=opject;
                    notifyDataSetChanged();
                }
            });

            return v;
        }
    }


}
