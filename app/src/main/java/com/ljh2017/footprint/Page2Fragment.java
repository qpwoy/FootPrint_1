package com.ljh2017.footprint;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
 * TODO : 내가 올린 메모들 리스트로 보여주기
 * TODO : 올린 메모에 수정, 삭제 메뉴 달기
 */

public class Page2Fragment extends Fragment {

    SwipeRefreshLayout refreshLayout;

    ImageView imgChage;

    ArrayList<Memo> memos = new ArrayList<>();

    RecyclerView recyclerView;
    RecyclerAdapter adapter;

    String loadUrl = "http://ljh2017.dothome.co.kr/FootPrint/LoadData.php";

    Thread lThread;

    String name;
    String Name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_page2,container,false);
        imgChage = (ImageView) view.findViewById(R.id.change);

        //**
        recyclerView = (RecyclerView) view.findViewById(R.id.view_recycle);
        adapter = new RecyclerAdapter(memos,getContext());
        recyclerView.setAdapter(adapter);

        //??

        Bundle bundle = getArguments();

        Log.e("qjsemf",bundle.toString());
        if(bundle != null) {
            name = bundle.getString("Name"); // getString(String key)
        }
        //??

        Name = name;

        //레이아웃 매니져...
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        recyclerView.setLayoutManager(manager);



        memoLoad();

        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.layout_swipe);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //갱신작업을 실행
                memos.clear();
                memoLoad();

                refreshLayout.setRefreshing(false);
            }
        });
        return view;
    }

    void memoLoad() {
        lThread  = new Thread() {
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

                    //읽어온 DB의 데이터와 내가 화면에 이미 보여주고 있는 아이템의 개수가 같다면 이미 다 불러들인적이 있는 상황.
                    memos.clear();
                    for(String row : rows) {
                        String[] datas = row.split("&");

                        if(datas.length<5) continue;

                        String name = datas[0];
                        String imgpic = datas[1];
                        String memo = datas[2];
                        String addr = datas[3];
                        String tag = datas[4];

                        Log.e("aaa",imgpic);
                        Log.e("tag",tag);

                        memos.add(new Memo(name,imgpic, memo, addr, tag));

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
        lThread.start();
        //**
    }

}
