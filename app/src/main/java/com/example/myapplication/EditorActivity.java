package com.example.myapplication;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditorActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_EDITOR_PLAN = 905;

    private Toolbar editorToolBar;
    private ListView listViewSettingMenu;
    private TextView editorTitle;
    private TextView editorDate;
    private TextView editorCountDown;
    private FrameLayout bkgFrameLayout;
    private List<Map<String, Object>> menuList = new ArrayList<Map<String, Object>>();
    private Plan plan;
    private ChangeColor changeColor;
    private int planPosition;
    private CountDownTimer timer;   // 倒计时
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editorToolBar = findViewById(R.id.editor_menu_toolBar);             // 修改界面工具栏
        bkgFrameLayout = findViewById(R.id.editor_bkg_frame);               // 修改界面plan背景框架
        editorTitle = findViewById(R.id.editor_bkg_title_textView);         // 修改界面plan标题
        editorDate = findViewById(R.id.editor_bkg_date_textView);           // 修改界面plan日期
        editorCountDown = findViewById(R.id.editor_count_down_textView);    // 修改界面plan倒计时

        // 初始化主题颜色
        InitTheme();

        // toolBar工具栏
        editorToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 将修改的数据传回主界面
                Intent editorIntent = new Intent(EditorActivity.this, MainActivity.class);
                editorIntent.putExtra("editor_plan", plan);
                editorIntent.putExtra("editor_plan_position", planPosition);
                setResult(RESULT_OK, editorIntent);
                EditorActivity.this.finish();
            }
        });
        editorToolBar.setOnMenuItemClickListener(new MyMenuClickListener());


        // 获取数据
        plan = (Plan) getIntent().getSerializableExtra("editor_plan");
        planPosition = getIntent().getIntExtra("editor_plan_position",-1);

        // 界面显示
        editorPlanView();

        editorCountDown.setOnClickListener(new ChangeFormatOnClickListener());

    }

    private void InitTheme() {
        changeColor = (ChangeColor) getIntent().getSerializableExtra("my_theme_color");
        if (changeColor.getMyColorPrimaryDark() != -1)
            editorToolBar.setBackgroundColor(changeColor.getMyColorPrimaryDark());
    }

    // 编辑界面plan的显示
    private void editorPlanView() {
        // 设置显示的数据
        bkgFrameLayout.setBackgroundResource(plan.getBackgroundImg());
        editorTitle.setText(plan.getTitle());
        // 时间格式
        final ShowTime getDate = new ShowTime(plan);
        editorDate.setText(getDate.getDateStr());

        // 获取秒数
        long ms = 0;
        try {
            ms = getDate.CalculationMillisecondWhitNow();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 倒计时
        timer = new CountDownTimer(ms, 1000) {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTick(long countDownTimeMs) {
                ArrayList<String> countDownTime = getDate.getCountDownDateArrayList(countDownTimeMs);
                // 获取倒计时格式的字符串
                StringBuilder countDownStr = new StringBuilder();
                for (int i=0; i<countDownTime.size(); i++)
                    countDownStr.append(countDownTime.get(i));
                // 设置倒计时格式的字符串
                editorCountDown.setText(countDownStr);
            }

            @Override
            public void onFinish() {
                editorCountDown.setText("已结束");
            }
        };
        timer.start();
    }

    // 处理修改返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQUEST_CODE_EDITOR_PLAN:{
                if (resultCode == RESULT_OK)
                {
                    if (data != null) {
                        this.plan = (Plan)data.getSerializableExtra("newPlan");
                    }
                    timer.cancel();     // 取消旧的倒计时
                    editorPlanView();   // 刷新界面
                }
                break;
            }
        }
    }



    // toolbar菜单栏点击响应事件
    private class MyMenuClickListener implements Toolbar.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int menuItemId = item.getItemId();
            switch (menuItemId)
            {
                case R.id.delete_icon:{
                    new android.app.AlertDialog.Builder(EditorActivity.this)
                            .setMessage("是否删除该计时？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent deleteIntent = new Intent(EditorActivity.this, MainActivity.class);
                                    deleteIntent.putExtra("delete_plan_position", planPosition);
                                    setResult(RESULT_OK, deleteIntent);
                                    EditorActivity.this.finish();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create().show();
                    //Toast.makeText(EditorActivity.this, "点击删除", Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.editor_icon:{
                    Intent editorIntent = new Intent(EditorActivity.this, AddActivity.class);
                    editorIntent.putExtra("editor_plan", plan);
                    startActivityForResult(editorIntent, REQUEST_CODE_EDITOR_PLAN);
                    //Toast.makeText(EditorActivity.this, "点击修改", Toast.LENGTH_SHORT).show();
                    break;
                }
            }


            return false;
        }
    }

    // 设置倒计时点击响应事件
    private class ChangeFormatOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

        }
    }
}
