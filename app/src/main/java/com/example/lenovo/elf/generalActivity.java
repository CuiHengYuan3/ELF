package com.example.lenovo.elf;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

public class generalActivity extends AppCompatActivity {
    private static String COLLECTION_FRAGMENT = "collectionFragment";//1
    private static String COMMENT_FRAGMENT = "commentFragment";//2
    private static String DAILYREC_FRAGMETN = "dailyRecFragment";//3


    public static void actionStartByTag(Context context, String tag) {
        Intent intent;
        if (tag.equals(COLLECTION_FRAGMENT)) {
            intent = new Intent(context, generalActivity.class);
            intent.putExtra("fragmentKind", 1);
        } else if (tag.equals(COMMENT_FRAGMENT)) {
            intent = new Intent(context, generalActivity.class);
            intent.putExtra("fragmentKind", 2);
        } else if (tag.equals(DAILYREC_FRAGMETN)) {
            intent = new Intent(context, generalActivity.class);
            intent.putExtra("fragmentKind", 3);
        } else {
            intent = new Intent(context, generalActivity.class);//默认

        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.beReplaced);
        Intent intent = getIntent();
        int Kind = intent.getIntExtra("fragmentKind", 1);
        if (fragment == null) {
            switch (Kind) {
                case 1:
                    fragment = new collectionFragment();
                    fm.beginTransaction().add(R.id.beReplaced, fragment).commit();
                    break;
                case 2:
                    fragment = new commentFragment();
                    toolbar.setVisibility(View.GONE);

                    fm.beginTransaction().add(R.id.beReplaced, fragment).commit();
                    break;
                case 3:
                    fragment = new DailyRecfragment();
                    fm.beginTransaction().add(R.id.beReplaced, fragment).commit();
                    break;
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.collection, menu);
        return true;
    }
}
