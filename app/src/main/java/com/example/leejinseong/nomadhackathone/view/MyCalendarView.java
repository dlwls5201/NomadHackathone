package com.example.leejinseong.nomadhackathone.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.leejinseong.nomadhackathone.Dlog;
import com.example.leejinseong.nomadhackathone.R;
import com.example.leejinseong.nomadhackathone.model.Money;
import com.example.leejinseong.nomadhackathone.ui.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by iwedding on 2018. 3. 27..
 */
public class MyCalendarView extends LinearLayout
{
    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // default date format
    private static final String DATE_FORMAT = "MMMM yyyy";

    // date format
    private String dateFormat;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    //event handling
    private EventHandler eventHandler = null;

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;

    // seasons' rainbow
    int[] rainbow = new int[] {
            R.color.summer,
            R.color.fall,
            R.color.winter,
            R.color.spring
    };

    // month-season association (northern hemisphere, sorry australia :)
    int[] monthSeason = new int[] {2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};

    public MyCalendarView(Context context)
    {
        super(context);
    }

    public MyCalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initControl(context, attrs);
    }

    public MyCalendarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_calendar, this);

        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();

        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs)
    {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MyCalendarView);

        try
        {
            // try to load provided date format, and fallback to default otherwise
            dateFormat = ta.getString(R.styleable.MyCalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        }
        finally
        {
            ta.recycle();
        }
    }
    private void assignUiElements()
    {
        // layout is inflated, assign local variables to components
        header = findViewById(R.id.llViewCalendarHeader);
        btnPrev = findViewById(R.id.ivViewCalendarPrev);
        btnNext = findViewById(R.id.ivViewCalendarNext);
        txtDate = findViewById(R.id.tvViewCalendarTitle);
        grid = findViewById(R.id.gvViewCalendar);
    }

    private void assignClickHandlers()
    {
        // add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentDate.add(Calendar.MONTH, 1);
                updateCalendar();
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

        // long-pressing a day
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> view, View cell, int position, long id)
            {
                // handle long-press
                if (eventHandler == null)
                    return false;

                eventHandler.onDayLongPress((Date)view.getItemAtPosition(position));
                return true;
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (eventHandler != null) {
                    eventHandler.onDayPress((Date)parent.getItemAtPosition(position), view);
                }

            }
        });
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar()
    {
        updateCalendar(null);
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(HashSet<Date> events)
    {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        // determine the cell for current month's beginning
        //Dlog.d("현재 월의 날짜 : " + calendar.get(Calendar.DAY_OF_MONTH));
        //Dlog.d("현재 요일 : " + calendar.get(Calendar.DAY_OF_WEEK));

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        //Dlog.v("현재 월의 날짜 : " + calendar.get(Calendar.DAY_OF_MONTH));
        //Dlog.v("현재 요일 : " + calendar.get(Calendar.DAY_OF_WEEK));
        //Dlog.e("monthBeginningCell : " + monthBeginningCell);

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        //Dlog.e("after 현재 월의 날짜 : " + calendar.get(Calendar.DAY_OF_MONTH));

        // fill cells
        while (cells.size() < DAYS_COUNT)
        {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        grid.setAdapter(new CalendarAdapter(getContext(), cells, events));

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        txtDate.setText(sdf.format(currentDate.getTime()));

        // set header color according to current season
        int month = currentDate.get(Calendar.MONTH);
        int season = monthSeason[month];
        int color = rainbow[season];

        header.setBackgroundColor(getResources().getColor(color));
    }


    private class CalendarAdapter extends ArrayAdapter<Date>
    {
        // days with events
        private HashSet<Date> eventDays;

        // for view inflation
        private LayoutInflater inflater;

        // realm
        private Realm realm;

        // perDayMoney
        private String perDayMoney;

        public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays)
        {
            super(context, R.layout.item_view_calendar, days);
            this.eventDays = eventDays;
            inflater = LayoutInflater.from(context);
            realm = Realm.getDefaultInstance();

            perDayMoney = PreferenceManager.getDefaultSharedPreferences(context)
                    .getString("per_day_money", null);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            // day in question
            Date date = getItem(position);
            int day = date.getDate();
            int month = date.getMonth();
            int year = date.getYear();

            // today
            Date today = new Date();

            // inflate item if it does not exist yet
            if (view == null)
                view = inflater.inflate(R.layout.item_view_calendar, parent, false);

            final RelativeLayout rlItemViewCalendar = view.findViewById(R.id.rlItemViewCalendar);
            final TextView tvItemViewCalendar = view.findViewById(R.id.tvItemViewCalendar);

            // if this day has an event, specify event image
            if (eventDays != null)
            {
                for (Date eventDate : eventDays)
                {
                    if (eventDate.getDate() == day &&
                            eventDate.getMonth() == month &&
                            eventDate.getYear() == year)
                    {
                        // mark this day for event
                        break;
                    }
                }
            }

            // clear styling
            tvItemViewCalendar.setTypeface(null, Typeface.NORMAL);
            tvItemViewCalendar.setTextColor(Color.BLACK);

            if (month != today.getMonth() || year != today.getYear())
            {
                // if this day is outside current month, grey it out
                tvItemViewCalendar.setTextColor(getResources().getColor(R.color.greyed_out));
            }
            else if (day == today.getDate())
            {
                // if it is today, set it to blue/bold
                tvItemViewCalendar.setTypeface(null, Typeface.BOLD);
                tvItemViewCalendar.setTextColor(getResources().getColor(R.color.today));

                rlItemViewCalendar.setBackgroundColor(getResources().getColor(R.color.darkOrange));
            }

            // set text
            tvItemViewCalendar.setText(String.valueOf(date.getDate()));

            // realm data setting
            String nowYear = String.valueOf(year+1900);
            String nowMonth = (month+1) < 10 ? "0" + String.valueOf(month+1) : String.valueOf(month+1);

            String nowTime = nowYear + "/" + nowMonth + "/" +String.valueOf(day);

            RealmResults<Money> datas = realm.where(Money.class).equalTo("date1", nowTime).findAllSortedAsync("date2");

            if(datas.toString().equals("[]")) {

                datas.removeAllChangeListeners();

            } else {

                datas.addChangeListener(new RealmChangeListener<RealmResults<Money>>() {
                    @Override
                    public void onChange(RealmResults<Money> monies) {
                        //Dlog.i("perDayMoney : " + perDayMoney + " , monies : " + monies + " , size : " + monies.size());

                        if(!TextUtils.isEmpty(perDayMoney)) {

                            int halfPerDayMoney = Integer.parseInt(perDayMoney) / 2;
                            int intPerDayMoney = Integer.parseInt(perDayMoney);

                            for(int i = 0; i < monies.size(); i++) {
                                Money money = monies.get(i);
                                intPerDayMoney -= Integer.parseInt(money.getMoney());
                            }

                            //Dlog.e("intPerDayMoney : " + intPerDayMoney);

                            if(intPerDayMoney < 0) {
                                rlItemViewCalendar.setBackgroundColor(getResources().getColor(R.color.warmPink));
                            } else if(intPerDayMoney > 0 && intPerDayMoney < halfPerDayMoney) {
                                rlItemViewCalendar.setBackgroundColor(getResources().getColor(R.color.yellowOrange));
                            } else {
                                rlItemViewCalendar.setBackgroundColor(getResources().getColor(R.color.warmBlue));
                            }

                        }
                    }
                });

            }

            return view;
        }
    }

    /**
     * Assign event handler to be passed needed events
     */
    public void setEventHandler(EventHandler eventHandler)
    {
        this.eventHandler = eventHandler;
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler
    {
        void onDayLongPress(Date date);

        void onDayPress(Date date, View view);
    }
}
