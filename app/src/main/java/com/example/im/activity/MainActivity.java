package com.example.im.activity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.im.R;
import com.example.im.fragment.ContactsFragment;
import com.example.im.fragment.SessionFragment;
import com.example.im.utils.ToastUtil;
import com.example.im.utils.ToolBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_title)
    TextView mainTitle;
    @BindView(R.id.main_vp)
    ViewPager mainVp;
    @BindView(R.id.main_bottom)
    LinearLayout mainBottom;

    private List<Fragment> fragments=new ArrayList<>();
    private ToolBarUtil toolBarUtil;
    private String[] toobartitleArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        intLisner();
    }

    private void intLisner() {
        mainVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toolBarUtil.change(position);
                mainTitle.setText(toobartitleArr[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        toolBarUtil.setOnToolBarLinstener(new ToolBarUtil.OnToolBarLinstener() {
            @Override
            public void onToolBarClick(int postion) {
                mainVp.setCurrentItem(postion);
            }
        });
    }

    private void initData() {
        //viewpage-->view-->pageAdapter
        //viewpage-->fragment-->fragmentpageAdapter
        //viewpage-->fragment-->fragmentStatepageAdapter

        //添加fragment到集合中
        fragments.add(new SessionFragment());
        fragments.add(new ContactsFragment());
        mainVp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        //底部添加按钮
        toolBarUtil = new ToolBarUtil();
        toobartitleArr = new String[]{"会话","联系人"};
        int[] iconArr={R.drawable.icon_meassage,R.drawable.icon_selfinfo};
        toolBarUtil.createToolBar(mainBottom, toobartitleArr,iconArr);
        toolBarUtil.change(0);
    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
