package com.example.pp_application;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private View view;
    private String app_version, app_name, app_author, app_publisher, ser_path;
    private TextView app_n, app_v, app_a, app_p, ser_p;
    private ImageView app_l;
    public InfoFragment() {
        // Required empty public constructor
    }

    @SuppressLint({"ValidFragment", "ValidFragment", "ValidFragment", "ValidFragment"})
    public InfoFragment(String app_name, String app_version, String app_author, String app_publisher, String ser_path){
        this.app_name = app_name;
        this.app_version = app_version;
        this.app_author = app_author;
        this.app_publisher = app_publisher;
        this.ser_path = ser_path;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_info, container, false);

        app_l = (ImageView) view.findViewById(R.id.app_l);
        app_n = (TextView) view.findViewById(R.id.app_n);
        app_v = (TextView) view.findViewById(R.id.app_v);
        app_a = (TextView) view.findViewById(R.id.app_a);
        app_p = (TextView) view.findViewById(R.id.app_p);
        ser_p = (TextView) view.findViewById(R.id.ser_p);

        app_l.setImageResource(R.mipmap.champion);
        app_n.setText(getText(R.string.info_appname) + " " + app_name);
        app_v.setText(getText(R.string.info_appversion) + " " + app_version);
        app_a.setText(getText(R.string.info_appauthor) + " " + app_author);
        app_p.setText(getText(R.string.info_apppublisher) + " " + app_publisher);
        ser_p.setText(getText(R.string.info_serpath) + " " + ser_path);

        return view;
    }

}
