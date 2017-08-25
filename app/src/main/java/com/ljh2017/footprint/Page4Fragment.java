package com.ljh2017.footprint;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Page4Fragment extends Fragment {

    EditText etFind;
    Button btnFind;

    SwipeRefreshLayout refreshLayout;

    ArrayList<Memo> pics2 = new ArrayList<>();

    RecyclerView recyclerView;
    RecyclerPicAdapter adapter;

    // 해시태그 검색 php
    String loadTagUrl = "http://ljh2017.dothome.co.kr/FootPrint/LoadDataTag.php";

    Thread tThread;

    String find;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_page4, container, false);

        etFind = (EditText) view.findViewById(R.id.et_find);
        btnFind = (Button) view.findViewById(R.id.btn_find);

        //TODO 검색한 사진들 보여주기

        recyclerView = (RecyclerView) view.findViewById(R.id.view_recycle4);
        adapter = new RecyclerPicAdapter(pics2, getContext());

        recyclerView.setAdapter(adapter);

        //레이아웃 매니져...
        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);

        recyclerView.setLayoutManager(manager);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_swipe4);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //갱신작업을 실행
                pics2.clear();
                tagLoad();

                refreshLayout.setRefreshing(false);
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find = etFind.getText().toString();
                etFind.setText("");

                tagLoad();
            }
        });
        return view;
    }

    void tagLoad() {
        tThread = new Thread() {
            @Override
            public void run() {

                try {
                    URL url = new URL(loadTagUrl);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    String data = "tag=" + URLEncoder.encode(find, "UTF-8");

                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

                    wr.write(data);
                    wr.flush();
                    wr.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer buffer = new StringBuffer();
                    String line = reader.readLine();

                    while (line != null) {
                        buffer.append(line);
                        line = reader.readLine();
                    }
                    Log.e("qew", buffer.toString());

                    String str = buffer.toString();
                    String[] rows = str.split(";");

                    pics2.clear();

                    for (int i = 0; i < rows.length; i++) {
                        String imgpic = rows[i];

                        pics2.add(new Memo(imgpic));

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        tThread.start();
    }
}