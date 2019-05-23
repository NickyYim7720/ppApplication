package com.example.pp_application;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private String TAG = "AccountFragment ===> ";
    private View view;
    private TextView username, userid, userdepartment;
    private ImageView userImg;
    private String uname, department, uid;


    public AccountFragment() {
        // Required empty public constructor
    }

    @SuppressLint({"VaildFragment", "ValidFragment", "ValidFragment"})
    public AccountFragment(String username, String uid, String department){
        this.uname = username;
        this.uid = uid;
        this.department = department;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false);

        userImg = (ImageView) view.findViewById(R.id.userImg);
        username = (TextView) view.findViewById(R.id.username);
        userid = (TextView) view.findViewById(R.id.userid);
        userdepartment = (TextView) view.findViewById(R.id.userdepartment);
        Log.d(TAG, "uname = " + uname + " uid = " + uid + "department = " + department);

        userImg.setImageResource(R.mipmap.user_image);
        username.setText("User Name: " + uname);
        userid.setText("User ID: " + uid);
        userdepartment.setText("Department: " + department);

        return view;
    }

}
