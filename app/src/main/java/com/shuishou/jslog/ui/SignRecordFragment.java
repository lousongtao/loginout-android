package com.shuishou.jslog.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shuishou.jslog.R;

public class SignRecordFragment extends Fragment {

    private Button btnThisWeek;
    private Button btnPreWeek;
    private Button btnNextWeek;
    private TextView txtDatePeriod;
    private RecyclerView listSign;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signrecord, container, false);
        btnNextWeek = view.findViewById(R.id.btnNextWeek);
        btnThisWeek = view.findViewById(R.id.btnThisWeek);
        btnPreWeek = view.findViewById(R.id.btnPreWeek);
        txtDatePeriod = view.findViewById(R.id.txtDatePeriod);
        listSign = view.findViewById(R.id.listSignRecord);
        
        return view;
    }
}