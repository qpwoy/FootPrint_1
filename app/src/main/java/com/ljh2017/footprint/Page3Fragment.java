package com.ljh2017.footprint;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by alfo06-15 on 2017-06-07.
 * 데이터베이스에 저장된 사진 모아보기
 */
public class Page3Fragment extends Fragment {

    SwipeRefreshLayout refreshLayout;

    ArrayList<Memo> pics = new ArrayList<>();

    RecyclerView recyclerView;
    RecyclerPicAdapter adapter;

    String loadUrl = "http://ljh2017.dothome.co.kr/FootPrint/LoadPic.php";

    Thread pThread;

    String name,Name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_page3, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.view_recycle);
        adapter = new RecyclerPicAdapter(pics, getContext());

        recyclerView.setAdapter(adapter);

        Bundle bundle = getArguments();

        Log.e("qjsemf",bundle.toString());
        if(bundle != null) {
            name = bundle.getString("Name"); // getString(String key)
        }

        Name = name;

        //??


        //레이아웃 매니져...
        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);

        recyclerView.setLayoutManager(manager);

        // 데이터베이스의 사진 가져오기기
        picLoad();

        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.layout_swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //갱신작업을 실행
                pics.clear();
                picLoad();

                refreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    void picLoad(){
        pThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(loadUrl);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    String data = "name=" + URLEncoder.encode(Name, "UTF-8");

                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());

                    wr.write(data);
                    wr.flush();
                    wr.close();

                    InputStream is = connection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr);

                    StringBuffer buffer = new StringBuffer();
                    String line = reader.readLine();

                    while(line!=null) {
                        buffer.append(line);
                        line = reader.readLine();
                    }

                    String str = buffer.toString();
                    String[] rows = str.split(";");

                    pics.clear();

                    for(int i = 0;i<rows.length;i++) {
                        String imgpic = rows[i];

                        pics.add(new Memo(imgpic));

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(adapter.getItemCount()-1);
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
        pThread.start();
    }
}
