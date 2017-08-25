package com.ljh2017.footprint;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

/**
 * Created by alfo06-15 on 2017-06-07.
 * TODO :  지도에 여행간 지역 표시하기
 */

public class Page1Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_page1,container,false);


        return view;
    }
}
