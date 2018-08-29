package com.oruba.niezbdnikturystyczny;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageButton menu_navigation, menu_help, menu_issue, menu_achievement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        menu_navigation = findViewById(R.id.menu_navigation);
        menu_help = findViewById(R.id.menu_help);
        menu_issue = findViewById(R.id.menu_issue);
        menu_achievement = findViewById(R.id.menu_achievement);

        menu_navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigationIntent = new Intent(MainActivity.this, NavigationActivity.class);
                startActivity(navigationIntent);
            }
        });

        menu_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigationIntent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(navigationIntent);
            }
        });

        menu_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigationIntent = new Intent(MainActivity.this, IssueActivity.class);
                startActivity(navigationIntent);
            }
        });

        menu_achievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigationIntent = new Intent(MainActivity.this, AchievementActivity.class);
                startActivity(navigationIntent);
            }
        });


    }


}
