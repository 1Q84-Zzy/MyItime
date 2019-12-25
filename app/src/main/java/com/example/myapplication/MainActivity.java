package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.rtugeek.android.colorseekbar.ColorSeekBar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_ADD_NEW_PLAN = 901;
    public static final int REQUEST_CODE_EDITOR_PLAN = 902;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private View navigationHeadView;

    private ViewPager homePlanViewPager;        // 主页轮播图控件
    private LinearLayout homeViewPoints;    // 主页导航小圆点控件
    private ListView homePlanListView;          // 主页Plan列表控件
    private FloatingActionButton homeFABtn; // 主页悬浮按钮控件
    private PlansArrayAdapter thePlansListAdapter;
    private ArrayList<Fragment> planFragmentList;

    private ArrayList<Plan> myPlan;
    private ChangeColor myChangeColor;
    private int themeColor;
    private FileDataSource fileDataSource;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homePlanListView = findViewById(R.id.home_listView);        // 主页plan列表
        homeFABtn = findViewById(R.id.home_add_btn);                // 主页新增按钮
        toolbar = findViewById(R.id.main_toolBar);                  // 主页工具栏
        drawerLayout = findViewById(R.id.main_drawer_layout);       // 主页菜单侧滑抽屉
        navigationView = findViewById(R.id.main_navigationView);    // 主页菜单侧滑抽屉内容

        // 加载数据和主题
        initData();

        // 显示倒计时列表菜单
        thePlansListAdapter = new PlansArrayAdapter(this, R.layout.main_list_item_plan, myPlan);
        homePlanListView.setAdapter(thePlansListAdapter);
        homePlanListView.setOnItemClickListener(new PlanItemClickListener());

        // 点击新建的悬浮按钮
        homeFABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent NewPlanIntent = new Intent(MainActivity.this, AddActivity.class);
                NewPlanIntent.putExtra("create_new_plan",true);
                NewPlanIntent.putExtra("my_theme_color", myChangeColor);
                startActivityForResult(NewPlanIntent, REQUEST_CODE_ADD_NEW_PLAN);
            }
        });

        // 侧滑菜单
        navigationView.setItemIconTintList(null);   // 显示原本的图片
        navigationHeadView = navigationView.getHeaderView(0);   // 获取头布局

        setSupportActionBar(toolbar);// 取代原来的ActionBar
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);   // 设置返回键可以使用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // 设置显示返回箭头
        // 实现打开、关闭的监听
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        actionBarDrawerToggle.syncState();      // 显示三条杠
        drawerLayout.addDrawerListener(actionBarDrawerToggle);      // 菜单拖动监听事件
        // 侧滑菜单点击事件
        navigationHeadView.setOnClickListener(new loginOnClickListener());
        navigationView.setNavigationItemSelectedListener(new myOnNavigationItemSelected());

    }


    // 获得传回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQUEST_CODE_ADD_NEW_PLAN:{
                if (resultCode == RESULT_OK)
                {
                    Plan newPlan = (Plan) data.getSerializableExtra("newPlan");
                    // 更新列表
                    myPlan.add(newPlan);
                    thePlansListAdapter.notifyDataSetChanged();


                    Toast.makeText(MainActivity.this, "新建成功", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_CODE_EDITOR_PLAN:{
                if (resultCode == RESULT_OK){
                    int deletePlanPosition = data.getIntExtra("delete_plan_position", -1);
                    if (deletePlanPosition >= 0 ) {
                        myPlan.remove(deletePlanPosition);
                        // 更新列表
                        thePlansListAdapter.notifyDataSetChanged();

                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Plan editorPlan = (Plan)data.getSerializableExtra("editor_plan");
                        int editorPlanPosition = data.getIntExtra("editor_plan_position", -1);
                        myPlan.set(editorPlanPosition, editorPlan);

                        // 更新列表
                        thePlansListAdapter.notifyDataSetChanged();
                        //Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }


    // 初始化数据和主题颜色
    private void initData() {
        fileDataSource = new FileDataSource(this);
        myPlan = fileDataSource.load();
        myChangeColor = fileDataSource.loadTheme();
        if (myChangeColor.getMyColorPrimaryDark() != -1){
            toolbar.setBackgroundColor(myChangeColor.getMyColorPrimaryDark());
            homeFABtn.setBackgroundTintList(ColorStateList.valueOf(myChangeColor.getMyColorPrimaryDark()));
        }

/*        if (myPlan.size() == 0){
            ArrayList<String> label = new ArrayList<>();
            myPlan.add(new Plan("标题1","备注1",R.drawable.test4,label,"",2025,12,11,9,50,15, "周四"));
            myPlan.add(new Plan("标题2","备注2",R.drawable.test5,label,"无",2021,10,15,9,50,15,"周五"));
            myPlan.add(new Plan("标题3","备注3",R.drawable.test6,label,"每周",2019,12,25,0,0,15,"周六"));
            myPlan.add(new Plan("标题4","",R.drawable.test2,label,"每年",2015,11,23,9,50,15,"周日"));
        }*/
    }
    // 销毁时保存plan
    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileDataSource.save();
        fileDataSource.saveTheme();
    }

    // 主页显示item适配器
    private class PlansArrayAdapter extends ArrayAdapter<Plan> {
        private int resourceId;

        public PlansArrayAdapter(@NonNull Context context, int resource, @NonNull List<Plan> objects) {
            super(context, resource, objects);
            resourceId=resource;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
            @SuppressLint("ViewHolder") View item = layoutInflater.inflate(resourceId, null);

            ImageView bgImg = item.findViewById(R.id.list_item_imageView);          // 背景图
            TextView tip = item.findViewById(R.id.list_item_tip_textView);          // 显示剩余或者过去
            TextView time = item.findViewById(R.id.list_item_time_textView);        // 剩余或者过去的天数
            TextView title = item.findViewById(R.id.list_item_title_textView);      // 标题
            TextView date = item.findViewById(R.id.list_item_date_textView);        // 日期
            TextView remarks = item.findViewById(R.id.list_item_remarks_textView);  // 备注

            Plan plan_item = this.getItem(position);    // 获得一个plan

            if (plan_item != null) {
                ShowTime getDays = new ShowTime(plan_item);
                ArrayList<String> TipDate = null;
                try {
                    TipDate = getDays.getTipAndBetweenDate();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (TipDate != null) {
                    tip.setText(TipDate.get(0));
                    if (TipDate.get(1).equals(""))
                        time.setVisibility(View.GONE);
                    else
                        time.setText(TipDate.get(1));
                }

                bgImg.setImageResource(plan_item.getBackgroundImg());

                title.setText(plan_item.getTitle());
                String dateStr = plan_item.getYear() + "年" + plan_item.getMonth() + "月" + plan_item.getDay() + "日";
                date.setText(dateStr);
                remarks.setText(plan_item.getRemarks());

            }
            return item;
        }

    }

    // 监听滑动
    private class myOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        private int lastPosition = 0;
        @Override
        public void onPageSelected(int position) {
            homeViewPoints.getChildAt(lastPosition).setEnabled(false);
            homeViewPoints.getChildAt(position).setEnabled(true);
            lastPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    // Plan设置监听事件
    private class PlanItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            // 向编辑界面传输数据
            Intent editorIntent = new Intent(MainActivity.this, EditorActivity.class);
            editorIntent.putExtra("editor_plan", myPlan.get(position));
            editorIntent.putExtra("editor_plan_position", position);
            editorIntent.putExtra("my_theme_color", myChangeColor);
            startActivityForResult(editorIntent, REQUEST_CODE_EDITOR_PLAN);
        }
    }

    // 侧滑菜单NavigationView菜单项的点击事件
    private class myOnNavigationItemSelected implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_SHORT).show();
            switch (menuItem.getItemId()){
                case R.id.theme_color_menu:
                    LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.select_color, null);
                    ColorSeekBar colorSeekBar =  layout.findViewById(R.id.colorSlider);

                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("选择颜色").setView(layout);
                    // 滑动时动态改变颜色
                    if (myChangeColor.getMyColorPrimaryDark() != -1)
                        colorSeekBar.setColor(myChangeColor.getMyColorPrimaryDark());
                    else
                        colorSeekBar.setColor(getResources().getColor(R.color.colorPrimaryDark));
                    colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
                        @Override
                        public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                            toolbar.setBackgroundColor(color);
                            homeFABtn.setBackgroundTintList(ColorStateList.valueOf(color));
                            themeColor = color;
                        }
                    });
                    // 点击取消时，恢复原来颜色
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (myChangeColor.getMyColorPrimaryDark() != -1) {
                                toolbar.setBackgroundColor(myChangeColor.getMyColorPrimaryDark());
                                homeFABtn.setBackgroundTintList(ColorStateList.valueOf(myChangeColor.getMyColorPrimaryDark()));
                            }else {
                                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                homeFABtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                            }
                        }
                    });
                    // 点击确认，设置颜色并保存
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            myChangeColor.setMyColorPrimaryDark(themeColor);
                        }
                    });
                    dialog.create().show();
                    break;
            }

            drawerLayout.closeDrawers();
            return true;
        }
    }

    // 侧滑菜单头布局点击事件
    private class loginOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Toast.makeText(MainActivity.this, "点击了登录", Toast.LENGTH_LONG).show();
        }
    }


}
