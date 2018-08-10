package com.vfs.pg12alfredo.lostitems;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements ItemsListFragment.OnItemActionsListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainFragmentPagerAdapter mainFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        // Add the fragments to the adapter
        mainFragmentPagerAdapter.addFragment(ItemsListFragment.newInstance(ItemsListFragment.TYPE_OWN));
        mainFragmentPagerAdapter.addFragment(new ItemsMapFragment());
        mainFragmentPagerAdapter.addFragment(new SettingsFragment());

        viewPager = findViewById(R.id.main_view_pager);
        viewPager.setAdapter(mainFragmentPagerAdapter);

        tabLayout = findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        setTabLayoutIcons();
    }

    private void setTabLayoutIcons() {
        TabLayout.Tab tabOne = tabLayout.getTabAt(0);
        if (tabOne != null) {
            tabOne.setIcon(R.drawable.ic_list_item);
        }
        TabLayout.Tab tabTwo = tabLayout.getTabAt(1);
        if (tabTwo != null) {
            tabTwo.setIcon(R.drawable.ic_map_items);
        }
        TabLayout.Tab tabThree = tabLayout.getTabAt(2);
        if (tabThree != null) {
            tabThree.setIcon(R.drawable.ic_settings);
        }
    }

    @Override
    public void onAddItemRequest() {
        // Go to the add item activity
        Intent intent = new Intent(MainActivity.this, SetItemActivity.class);
        startActivity(intent);
    }
}
