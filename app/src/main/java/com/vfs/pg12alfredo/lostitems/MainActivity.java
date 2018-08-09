package com.vfs.pg12alfredo.lostitems;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.main_view_pager);
        viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager()));

        tabLayout = findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setTabLayoutIcons();
    }

    private void setTabLayoutIcons() {
        TabLayout.Tab tabOne = tabLayout.getTabAt(0);
        if (tabOne != null) {
            tabOne.setIcon(android.R.drawable.ic_dialog_map);
        }
        TabLayout.Tab tabTwo = tabLayout.getTabAt(1);
        if (tabTwo != null) {
            tabTwo.setIcon(android.R.drawable.ic_dialog_info);
        }
        TabLayout.Tab tabThree = tabLayout.getTabAt(2);
        if (tabThree != null) {
            tabThree.setIcon(android.R.drawable.ic_dialog_email);
        }
    }
}
