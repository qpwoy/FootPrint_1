package com.ljh2017.footprint;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

public class AddActivity extends AppCompatActivity {

    TextView tvAddr;
    ImageView ivPic;
    EditText etMemo;
    EditText etTag;
    TextView tvTag;
    CheckBox Lock;

    boolean isLock = false;
    TextView tt;

    String name; // 로그인할때 입력한 이름름

   Uri imgUri = null; // 갤러리에서 선택한 이미지의 경로 정보관리 객체참조변수
    String imgPath;

    // 지도에서 받아온 주소 데이터값
    String addrData;

    String insertUrl = "http://ljh2017.dothome.co.kr/FootPrint/InsertData.php";

    String tagData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        tvAddr = (TextView) findViewById(R.id.add_addr);
        ivPic = (ImageView) findViewById(R.id.add_img);
        etMemo = (EditText) findViewById(R.id.add_memo);
        etTag = (EditText) findViewById(R.id.add_ettag);
        tvTag = (TextView) findViewById(R.id.add_tvtag);
        Lock = (CheckBox) findViewById(R.id.add_check);
        tt = (TextView) findViewById(R.id.tt);

        Intent intent = getIntent();
        name = intent.getStringExtra("Name");

        Log.e("NNN1",name);


//        Lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked) {
//                    isLock  = true;
//                    tt.setText("공개");
//                }
//            }
//        });

    }

    public void clickTagAdd(View v) {
        if(etTag!=null) {
            String tag = etTag.getText().toString();
            etTag.setText("");
            tvTag.append("#" + tag);

            tagData = tvTag.getText().toString();
        } else {
            tagData = "  ";
        }
        Log.e("tvTagAdd",tvTag.getText().toString());
    }

    public void clickMap(View v) { // 지도 열어 위치 가져오기
        Intent intent = new Intent(AddActivity.this,MapActivity.class);
        startActivityForResult(intent,100);
    }

    public void clickPic(View v) { // 사진첩불러오기
        // 혹시 마시멜로우 버전 이상이면 런타임퍼미션을 획득해야 함.
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            int checkPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            // 혹시 퍼미션이 불허되어 있다면 퍼미션 요청..
            if (checkPermission == PackageManager.PERMISSION_DENIED) {
                // 퍼미션을 요청하는 새로운 다이얼로그처럼 보이는 화면이 띄어짐.
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            }
        }

        // 내 폰에 있는 이미지를 선택 ...
        // 사진목록을 보여주는 앱(갤러리 or 사진)의 화면(Activity)을 실행
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK); // 사진첩 or 전화번호부와 같은 선택할 데이터가 있는 경우
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,20);
    }

    // startActivityForResult()로 실행시켜서 보여진 화면이 종료되면 자동으로 실행되는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: // 위치
                if (resultCode!=RESULT_OK) return;

                addrData = data.getStringExtra("Addr");
                tvAddr.setText(addrData);
                Log.e("addr",addrData);
                break;

            case 20:  // 사진
                if(resultCode!=RESULT_OK) return;

                // 선택된 이미지의 경로정보를 가지고 있는 Uri객체 얻어오기
                imgUri = data.getData();
                Glide.with(this).load(imgUri).into(ivPic);

                ////.이미지 절대경로 찾기
                imgPath = imgUri.toString();
                Log.i("imgPath", imgPath);

                if(imgPath.contains("content://")){
                    // 이미지경로가 DB에 제공되어 있다는 것..
                    ContentResolver resolver = getContentResolver();
                    Cursor cursor = resolver.query(imgUri, null, null, null, null);
                    if(cursor!=null && cursor.getCount()!=0) {
                        cursor.moveToFirst();
                        imgPath = cursor.getString( cursor.getColumnIndex("_data"));
                    }
                }
                Log.i("imgPath", imgPath);
                ////.
                break;
        }
    }

    // requestPermissions()를 실행시켜서 보여진 화면의 선택을 완료하면 자동으로 실행되는 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 100:

                if(grantResults[0]==PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "사진목록보기 불가", Toast.LENGTH_SHORT).show();
                    finish(); // 앱 종료
                }
                break;
        }
    }

    public void clickMemoSave(View v) {
        // TODO : 데이터베이스에 데이터 저장하기(Memo : 위치(addrData), 사진, 메모(memoData), **공개여부(isLock), 태그(tag))

        // Volley를 통해 네트웍작업을 수행하는 큐가 필요
        RequestQueue requestQueue = Volley.newRequestQueue(AddActivity.this);

        SimpleMultiPartRequest smpr = new SimpleMultiPartRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("aaa","성공"+response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("bbb","bbb");
            }
        });

        smpr.addStringParam("name",name);
        smpr.addFile("upload", imgPath);
        smpr.addStringParam("imgpic","http://ljh2017.dothome.co.kr/FootPrint/image/"+Uri.parse(imgPath).getLastPathSegment()); // 이미지주소 string 값
        smpr.addStringParam("memo",etMemo.getText().toString());
        smpr.addStringParam("addr",addrData);
        smpr.addStringParam("tag",tagData);

        requestQueue.add(smpr);

        Intent intent = new Intent(AddActivity.this,MainActivity.class);
        startActivity(intent);
    }
}