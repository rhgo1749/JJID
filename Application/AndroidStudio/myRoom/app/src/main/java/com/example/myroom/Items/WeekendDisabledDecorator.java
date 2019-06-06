package com.example.myroom.Items;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.example.myroom.Activities.MainReservationActivity;
import com.example.myroom.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.security.AccessController.getContext;

public class WeekendDisabledDecorator implements DayViewDecorator {

    private final Calendar calendar = Calendar.getInstance();
    SimpleDateFormat Hour = new SimpleDateFormat("HH");
    long now = System.currentTimeMillis(); // 현재시간 가져오기
    Date nowDate = new Date(now);

    Context ct;
    public WeekendDisabledDecorator(Context context) {
        ct=context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        day.copyTo(calendar);

        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        if(weekDay == Calendar.SUNDAY)
        {
            return true;
        }
        else if(weekDay == Calendar.SATURDAY)
        {
            return true;
        }
        else if(returnMindate(calendar).before(nowDate))
        {
            return true;
        }
        else if(returnMaxdate(calendar).after(nowDate))
        {
            return true;
        }
        else if(Hour.format(nowDate).equals("23") && nowDate.after(day.getDate()))//23시인경우 더이상 예약가능한시간이없음
        {
            return true;
        }
        else
            return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setDaysDisabled(true);

        view.setBackgroundDrawable(ContextCompat.getDrawable(ct,R.drawable.reservation_x));
    }
    public Date returnMindate(Calendar cd)
    {
        cd.add(Calendar.DATE,1);
        return cd.getTime();
    }
    public Date returnMaxdate(Calendar cd)
    {
        cd.add(Calendar.DATE,-8);
        return cd.getTime();
    }
}