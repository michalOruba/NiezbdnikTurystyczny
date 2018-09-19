package com.oruba.niezbdnikturystyczny;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity{
    private static final String TAG = "NavigationActivity";

    final ArrayList<NavigationItem> hills = new ArrayList<NavigationItem>();
    ListView listView;
    ImageView hillInfoView;
    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSetOld = new ConstraintSet();
    private ConstraintSet constraintSetNew = new ConstraintSet();
    private boolean altLayout;

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.navigation_layout);
        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.navigation_container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.navigation_tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Navigation1Fragment(), getString(R.string.navigation1_fragment_name));
        adapter.addFragment(new Navigation2Fragment(), getString(R.string.navigation2_fragment_name));
        viewPager.setAdapter(adapter);
    }
}
