package com.example.pp_application;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private View view;
    private String app_version, app_name, app_author, app_publisher;
    private TextView app_n, app_v, app_a, app_p;
    public InfoFragment() {
        // Required empty public constructor
    }

    @SuppressLint({"ValidFragment", "ValidFragment", "ValidFragment", "ValidFragment"})
    public InfoFragment(String app_name, String app_version, String app_author, String app_publisher){
        this.app_name = app_name;
        this.app_version = app_version;
        this.app_author = app_author;
        this.app_publisher = app_publisher;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_info, container, false);

        app_n = (TextView) view.findViewById(R.id.app_n);
        app_v = (TextView) view.findViewById(R.id.app_v);
        app_a = (TextView) view.findViewById(R.id.app_a);
        app_p = (TextView) view.findViewById(R.id.app_p);

        app_n.setText(app_name);
        app_v.setText(app_version);
        app_a.setText(app_author);
        app_p.setText(app_publisher);

        return view;
    }

}
