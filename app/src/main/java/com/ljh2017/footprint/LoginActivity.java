package com.ljh2017.footprint;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {

    ImageView ivMember;
    EditText etMemberId;
    EditText etMemberPw;

    EditText aMemberId, aMemberPw, aMemberName;

    //SQLite
    String databaseName;
    SQLiteDatabase database;
    String tableName;
    CustomerDatabaseHelper databaseHelper;
    String CId;
    String CPw;
    String CName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ivMember = (ImageView) findViewById(R.id.iv_member);
        etMemberId = (EditText) findViewById(R.id.et_member_id);
        etMemberPw = (EditText)findViewById(R.id.et_member_pw);
        databaseName = "member";

        //SQLite 생성//8.8

        try{
            if(database==null){
                //database = openOrCreateDatabase(databaseName, Context.MODE_PRIVATE,null);

                databaseHelper = new CustomerDatabaseHelper(getApplicationContext(),databaseName,null,1);
                database = databaseHelper.getWritableDatabase();
                //Toast.makeText(LoginActivity.this, "DB :"+databaseName+"생성", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        tableName = "JOIN";
        try{
            if (database != null) {
                database.execSQL("CREATE TABLE if not exists " + "tableName" + "(" +
                        "_id integer PRIMARY KEY autoincrement," +
                        "id text," +
                        "pw integer," +
                        "name text" +
                        ")");
                //Toast.makeText(LoginActivity.this,"table :" + tableName + "생성",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        ////////////////////////////**8.8
    }

    //회원가입 버튼
    public void clickAdd(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Log in");

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.alert_member,null);

        aMemberId = (EditText) layout.findViewById(R.id.et_member_id);
        aMemberPw = (EditText) layout.findViewById(R.id.et_member_pw);
        aMemberName = (EditText) layout.findViewById(R.id.et_member_name);

        builder.setView(layout);

        builder.setPositiveButton("login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String id;
                String pw;
                String name;

                //SQLite 회원저장하기기**8.8
                try {
                    if(database!=null){
                        id = aMemberId.getText().toString();
                        pw = aMemberPw.getText().toString();
                        name = aMemberName.getText().toString();

                        database.execSQL("INSERT INTO " + "tableName" + "(id, pw, name) VALUES" +
                                "("+"'"+id+"'"+","+pw+","+"'"+name+"'"+");");

                        Toast.makeText(LoginActivity.this, "데이터를 추가했습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "데이터베이스를 먼저 열어야합니다.", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                ////////////////////////////**8.8
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
    }

    //로그인 버튼
    public void clickLogin(View v) {

        //SQLite 로그인 확인//**8.8
        if (database != null) {
            String etId = etMemberId.getText().toString();
            String etPw = etMemberPw.getText().toString();

            String sql = "SELECT id, pw, name FROM tableName where id = '"+etId+"' and pw = '"+etPw+"'";

            Cursor cursor = database.rawQuery(sql, null);
            Log.e("sqll",cursor.toString());



            while (cursor.moveToNext()){
                CId = cursor.getString(0);
                CPw = cursor.getString(1);
                CName = cursor.getString(2);

                Log.e("로그인",CId+","+CPw+","+CName);
            }

            if (etMemberId.getText().toString().equals(CId) && etMemberPw.getText().toString().equals(CPw)) {//내장디비인 SQLite에서 가져온 Id와 Pw 비교하여 같으면 로그인
                Intent intent = new Intent(getApplication(),MainActivity.class);
                intent.putExtra("Id",CId);
                intent.putExtra("Name",CName);
                startActivity(intent);

                Toast.makeText(getApplication(),etMemberId.getText().toString()+"님 환영합니다.",Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(this, "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }/////////////////**8.8
    }


    //데이터베이스///**8.8 --찾아보기--
    class CustomerDatabaseHelper extends SQLiteOpenHelper {

        CustomerDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onOpen(SQLiteDatabase database) {
            super.onOpen(database);

            Toast.makeText(getApplicationContext(),"Helper의 onOpen()호출됨",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            Toast.makeText(getApplicationContext(),"Helper의 onCreate()호출됨",Toast.LENGTH_LONG).show();

            //createTable(database);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            Toast.makeText(getApplicationContext(),"Helper의 onUpgrade()호출됨 :"+oldVersion+" => "+newVersion,Toast.LENGTH_LONG).show();

            // changeTable(database);
        }
    }
    ///////////**8.8
}