package com.ljh2017.footprint;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentTabHost tabHost;

    ViewPager pager;
    FragmentAdapter adapter;

    TextView userId, userName;
    ArrayList<Member> members = new ArrayList<>();

    String Id, Name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        userId = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_id);
        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);

        Intent intent = getIntent();
        Id = intent.getStringExtra("Id");
        Name = intent.getStringExtra("Name");



        members.add(new Member(Id,Name));


        userId.setText(Id);
        userName.setText(Name);

        navigationView.setNavigationItemSelectedListener(this);


        //프레그먼트
        tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);

        //탭버튼(TabSpec)을 추가작업을 위한 셋업메소드
        tabHost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent);

        tabHost.addTab( tabHost.newTabSpec( "tab1" ).setIndicator("Map"), DummyFragment.class, null );
        tabHost.addTab( tabHost.newTabSpec( "tab2" ).setIndicator("Memo"), DummyFragment.class, null );
        tabHost.addTab( tabHost.newTabSpec( "tab3" ).setIndicator("Pic"), DummyFragment.class, null );
        tabHost.addTab( tabHost.newTabSpec( "tab4" ).setIndicator("Search"), DummyFragment.class, null );


        pager = (ViewPager)findViewById(R.id.viewpager);
        adapter = new FragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        Bundle bundle = new Bundle();
        bundle.putString("Name", Name);
        adapter.getItem(1).setArguments(bundle);
        adapter.getItem(2).setArguments(bundle);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if(tabId.equals("tab1")){
                    pager.setCurrentItem(0,true);
                }else if(tabId.equals("tab2")){
                    pager.setCurrentItem(1,true);
                }else if(tabId.equals("tab3")){
                    pager.setCurrentItem(2,true);
                }else if(tabId.equals("tab4")){
                    pager.setCurrentItem(3,true);
                }
            }
        });

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabHost.setCurrentTab(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });   ///Tab////////////

    }/////onCreate()///////

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            //추가버튼 클릭시 컨텐츠 올리기
            Intent intent = new Intent(MainActivity.this,AddActivity.class);
            intent.putExtra("Name",Name);
            startActivity(intent);
            //Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
