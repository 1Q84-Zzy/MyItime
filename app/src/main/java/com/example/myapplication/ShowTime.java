package com.example.myapplication;

/*
* 计算日期相隔天数
* */

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;

public class ShowTime {
    private Plan plan;

    public ShowTime(Plan plan) {
        this.plan = plan;
    }

    // 与当前时间比较算出毫秒数
    public long CalculationMillisecondWhitNow() throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDateStr = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        String planDateStr = plan.getYear() + "-" + plan.getMonth() + "-" + plan.getDay() + " " + plan.getHour() + ":" + plan.getMinute() + ":" + plan.getSecond();

        Date nowDate = simpleDateFormat.parse(nowDateStr);
        Date planDate = simpleDateFormat.parse(planDateStr);

        //Log.i("returnTime","" + (int) ((otherDate.getTime() - nowDate.getTime()) / 1000));
        return (planDate.getTime() - nowDate.getTime());
    }

    // 获取倒计时的数组
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<String> getCountDownDateArrayList(long ms){
        /*使用java 8的Period的对象计算两个LocalDate对象的时间差，严格按照年、月、日计算，如：2018-03-12 与 2014-05-23 相差 3 年 9 个月 17 天*/
        LocalDate nowDate = LocalDate.now();
        LocalDate planDate = LocalDate.of(plan.getYear(), plan.getMonth(), plan.getDay());
        Period period = Period.between(nowDate, planDate);

        long mSecond = ms/1000;
        long mMinute = 0;
        long mHour = 0;
        if (mSecond > 60) {
            mMinute = mSecond / 60;   // 取整
            mSecond = mSecond % 60;   // 取余
        }
        if (mMinute > 60){
            mHour = mMinute / 60;
            mMinute = mMinute % 60;
        }

        if (mHour > 24){
            // mDay = mHour / 24;
            mHour = mHour % 24;
        }

        ArrayList<String> dateList = new ArrayList<>();
        if (period.getYears() > 0)
            dateList.add(period.getYears() + "年");
        if (period.getMonths() > 0)
            dateList.add(period.getMonths() + "月");
        if (period.getDays() > 0)
            dateList.add(period.getDays() + "天");
        if (mHour > 0)
            dateList.add(mHour + "小时");
        if (mMinute > 0)
            dateList.add(mMinute + "分钟");
        dateList.add(mSecond + "秒");

        return dateList;
    }

    // 获取日期的字符串
    @SuppressLint("DefaultLocale")
    public String getDateStr() {
        String dateStr = plan.getYear() + "年" + plan.getMonth() + "月" + plan.getDay() + "日";
        // 如果设置了时间则显示时间
        if (plan.getHour() >= 0)
            dateStr += " " + String.format("%02d", plan.getHour()) + ":" + String.format("%02d", plan.getMinute());
        // 显示星期
        dateStr += " " + plan.getWeek();

        return dateStr;
    }

    // 获取相差的时间
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<String> getTipAndBetweenDate() throws ParseException {
        /*使用java 8的Period的对象计算两个LocalDate对象的时间差，严格按照年、月、日计算，如：2018-03-12 与 2014-05-23 相差 3 年 9 个月 17 天*/
        LocalDate nowDate = LocalDate.now();
        LocalDate planDate = LocalDate.of(plan.getYear(), plan.getMonth(), plan.getDay());
        Period period = Period.between(nowDate, planDate);

        String date = "";
        String tipStr = "";

        // 提示文字
        long ms = this.CalculationMillisecondWhitNow();
        int day = (int) (ms/24*60*60*1000);

        int mYear = period.getYears();
        int mMonth = period.getMonths();
        int mDay = period.getDays();
        if (mYear < 0 || mMonth < 0 || mDay < 0)
            tipStr = "已经";
        else if (mYear == 0 && mMonth == 0 && mDay == 0)
            tipStr = "今天";
        else if (mYear == 0 && mMonth == 0 && mDay < 30)
            tipStr = "还剩";
        else
            tipStr = "还有";

        // 相隔日期
        if (period.getYears() != 0)
            date += Math.abs(period.getYears()) + "年";
        if (period.getMonths() != 0)
            date += Math.abs(period.getMonths()) + "月";
        if (period.getDays() != 0)
            date += Math.abs(period.getDays()) + "天";

        ArrayList<String> dateList = new ArrayList<>();
        dateList.add(tipStr);
        dateList.add(date);

        return dateList;
    }

}
