package com.example.moham.my_whatsapp.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moham.my_whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class edittextFragment extends Fragment {

    private static final String ARG_PARAM1 = "type";
    private static final String ARG_PARAM2 = "data";

    private String type,data;

    private DatabaseReference reference;

    private EditText editnewtext;
    private Button ok,cancel;


    public static edittextFragment newInstance(String param1,String param2) {
        edittextFragment fragment = new edittextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference= FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getUid());
        if (getArguments() != null) {
            type = getArguments().getString(ARG_PARAM1);
            data = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_edittext, container, false);

        editnewtext=v.findViewById(R.id.new_text);
        ok=v.findViewById(R.id.button_ok);
        cancel=v.findViewById(R.id.button_cancel);

        editnewtext.setText(data);
        editnewtext.selectAll();

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newtext=editnewtext.getText().toString().trim();
                if (!newtext.equals("")){
                    if (type.equals("name")){
                        reference.child("name").setValue(newtext);
                        reference.child("search").setValue(newtext.toLowerCase());
                        Toast.makeText(getContext(),newtext,Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }else if (type.equals("stats")){
                        reference.child("stats").setValue(newtext);
                        getActivity().onBackPressed();
                    }

                }else{
                    Toast.makeText(getContext(),"the text is empty and it is not posipol",Toast.LENGTH_LONG).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
}
