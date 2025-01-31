package com.android.renly.meetingreservation.module.home.fullscreen;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import com.acker.simplezxing.activity.CaptureActivity;
import com.android.renly.meetingreservation.App;
import com.android.renly.meetingreservation.R;
import com.android.renly.meetingreservation.adapter.LectureAdapter;
import com.android.renly.meetingreservation.api.RetrofitService;
import com.android.renly.meetingreservation.api.bean.Lecture;
import com.android.renly.meetingreservation.api.bean.TestB;
import com.android.renly.meetingreservation.api.bean.TestBean;
import com.android.renly.meetingreservation.injector.components.DaggerHomeFragComponent;
import com.android.renly.meetingreservation.listener.ItemClickListener;
import com.android.renly.meetingreservation.module.base.BaseFragment;
import com.android.renly.meetingreservation.module.booking.roomList.RoomListActivity;
import com.android.renly.meetingreservation.module.booking.search.SearchActivity;
import com.android.renly.meetingreservation.module.face.facerecognize.FaceRecognizeActivity;
import com.android.renly.meetingreservation.module.home.HomeActivity;
import com.android.renly.meetingreservation.module.meeting.MeetingActivity;
import com.android.renly.meetingreservation.module.mine.mymeeting.MyMeetingActivity;
import com.android.renly.meetingreservation.module.user.login.LoginActivity;
import com.android.renly.meetingreservation.utils.LogUtils;
import com.android.renly.meetingreservation.utils.toast.ToastUtils;
import com.android.renly.meetingreservation.widget.RecycleViewDivider;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.wang.avi.AVLoadingIndicatorView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;

public class HomeFrag extends BaseFragment implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnCalendarLongClickListener,
        CalendarView.OnYearChangeListener,
        View.OnClickListener{
    @BindView(R.id.btn01)
    LinearLayout btn01;
    @BindView(R.id.tv_month_day)
    TextView mTvMonthDay;
    @BindView(R.id.tv_year)
    TextView mTvYear;
    @BindView(R.id.tv_lunar)
    TextView mTvLunar;
    @BindView(R.id.tv_current_day)
    TextView mTvCurrentDay;
    @BindView(R.id.rl_tool)
    RelativeLayout mRlTool;
    @BindView(R.id.calendarView)
    CalendarView mCalendarView;
    @BindView(R.id.calendarLayout)
    CalendarLayout mCalendarLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.expandable_layout_day_content)
    ExpandableLayout dayContent;
    @BindView(R.id.expandable_layout_day_content_empty)
    ExpandableLayout dayContentEmpty;
    @BindView(R.id.mymeeting_time)
    TextView mymeetingTime;
    @BindView(R.id.mymeeting_address)
    TextView mymeetingAddress;
    @BindView(R.id.mymeeting_title)
    TextView mymeetingTitle;
    @BindView(R.id.mymeeting_worker)
    TextView mymeetingWorker;
    @BindView(R.id.rl_logintip)
    RelativeLayout tipLayout;
    @BindView(R.id.active_activity)
    LinearLayout activeLayout;
    @BindView(R.id.day_activity)
    LinearLayout dayLayout;
    @BindView(R.id.layout_pushtip)
    LinearLayout layoutPushtip;

    private int mYear;

    private List<Lecture> lectureList;

    @Inject
    TestB testB;

    @Inject
    TestB testB1;

    @Override
    protected void initInjector() {
        DaggerHomeFragComponent.builder()
                .build()
                .inject(this);
        LogUtils.printLog("hashCode=="+testB.hashCode()+" hashCode2="+testB1.hashCode());
    }

    @Override
    public int getLayoutid() {
        return R.layout.frag_home;
    }

    @Override
    protected void initData(Context content) {
        if (App.iSLOGIN())
            initCalendarEvent();
        initLectureListData();
        Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> mCalendarLayout.shrink(),
                        throwable -> LogUtils.printError("HomeFrag initdata() timer " + throwable.getMessage()));

        initView();
    }

    private void initCalendarEvent() {
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();

        Map<String, Calendar> map = new HashMap<>();
        map.put(getSchemeCalendar(year, month, 3, 0xFF40db25, "假").toString(),
                getSchemeCalendar(year, month, 3, 0xFF40db25, "假"));
        map.put(getSchemeCalendar(year, month, 6, 0xFFe69138, "事").toString(),
                getSchemeCalendar(year, month, 6, 0xFFe69138, "事"));
        map.put(getSchemeCalendar(year, month, 9, 0xFFdf1356, "议").toString(),
                getSchemeCalendar(year, month, 9, 0xFFdf1356, "议"));
        map.put(getSchemeCalendar(year, month, 13, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 13, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, 14, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 14, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, 15, 0xFFaacc44, "假").toString(),
                getSchemeCalendar(year, month, 15, 0xFFaacc44, "假"));
        map.put(getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记").toString(),
                getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记"));
        map.put(getSchemeCalendar(year, month, 25, 0xFF13acf0, "假").toString(),
                getSchemeCalendar(year, month, 25, 0xFF13acf0, "假"));
        map.put(getSchemeCalendar(year, month, 27, 0xFF13acf0, "多").toString(),
                getSchemeCalendar(year, month, 27, 0xFF13acf0, "多"));
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(map);
    }

    private void initLectureListData() {
        lectureList = new ArrayList<>();
        lectureList.add(new Lecture("http://blog.graydove.cn/upload/2019/6/speaker01-10c39b762fc24f0192acc4c2cc0fb2c9.jpg",
                "个人发展如何借助趋势的力量", "叶修，一个专门研究思维方法与学习策略的人，《深度思维》作者"
                , new Date().getTime(), "5小时"));
        lectureList.add(new Lecture("http://blog.graydove.cn/upload/2019/6/speaker02-a08b66e6299a4476921b64ec4032e0f2.jpg",
                "GDPR来了，你我应当注意什么", "王融，腾讯研究院资深专家。长期从事电信、互联网立法与监管政策研究"
                , new Date().getTime(), "5小时"));
        lectureList.add(new Lecture("http://blog.graydove.cn/upload/2019/6/speaker01-10c39b762fc24f0192acc4c2cc0fb2c9.jpg",
                "如何选到适合自己的好专业", "叶修，一个专门研究思维方法与学习策略的人，《深度思维》作者"
                , new Date().getTime(), "5小时"));
        lectureList.add(new Lecture("http://blog.graydove.cn/upload/2019/6/speaker02-a08b66e6299a4476921b64ec4032e0f2.jpg",
                "GDPR来了，你我应当注意什么", "王融，腾讯研究院资深专家。长期从事电信、互联网立法与监管政策研究"
                , new Date().getTime(), "5小时"));
    }

    protected RecyclerView.LayoutManager mLayoutManager;

    private LectureAdapter adapter;

    private void initAdapter() {
        adapter = new LectureAdapter(mActivity, lectureList);
        adapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
//                jumpToActivity();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        // 调整draw缓存,加速recyclerview加载
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    @Override
    protected void initView() {
        recyclerView.setFocusable(false);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnCalendarLongClickListener(this, false);
        mTvYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTvMonthDay.setText(mCalendarView.getCurMonth() + "月"
                + mCalendarView.getCurDay() + "日");
        mTvLunar.setText("今日");
        mTvCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        avi.show();

        initAdapter();
        initRecyclerView();
        doRefresh();
    }

    @Override
    public void ScrollToTop() {
        scrollView.smoothScrollTo(0, 0);
    }

    private AlertDialog dialog;

    @OnClick({R.id.btn01, R.id.btn02, R.id.btn03, R.id.btn04, R.id.tv_month_day,
            R.id.fl_current, R.id.active_activity, R.id.tip_login, R.id.layout_pushtip})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn01:
                Objects.requireNonNull(getActivity()).startActivityForResult(
                        new Intent(mContent, CaptureActivity.class), CaptureActivity.REQ_CODE);
                break;
            case R.id.btn02:
                jumpToActivity(SearchActivity.class);
                break;
            case R.id.btn03:
                intent = new Intent(mActivity, MeetingActivity.class);
                intent.putExtra("isAnnouncer", true);
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
                break;
            case R.id.btn04:
                View mView = View.inflate(mActivity, R.layout.layout_service, null);
                mView.findViewById(R.id.water).setOnClickListener(this);
                mView.findViewById(R.id.clean).setOnClickListener(this);
                mView.findViewById(R.id.sign).setOnClickListener(this);
                mView.findViewById(R.id.other).setOnClickListener(this);
                dialog = new AlertDialog.Builder(mActivity)
                        .setView(mView)
                        .setCancelable(true)
                        .create();
                dialog.show();
                break;
            case R.id.tv_month_day:
                if (!mCalendarLayout.isExpand()) {
                    mCalendarView.showYearSelectLayout(mYear);
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTvLunar.setVisibility(View.GONE);
                mTvYear.setVisibility(View.GONE);
                mTvMonthDay.setText(String.valueOf(mYear));
                break;
            case R.id.fl_current:
                mCalendarView.scrollToCurrent();
                break;
            case R.id.active_activity:
                intent = new Intent(mActivity, MeetingActivity.class);
                intent.putExtra("isAnnouncer", false);
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
                break;
            case R.id.tip_login:
                intent = new Intent(mActivity, LoginActivity.class);
                mActivity.startActivityForResult(intent, LoginActivity.requestCode);
                break;
            case R.id.layout_pushtip:
                intent = new Intent(mActivity, MeetingActivity.class);
                intent.putExtra("isAnnouncer", true);
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.translate_in, R.anim.translate_out);
                layoutPushtip.setVisibility(View.GONE);
                App.clearBadge();
                break;
        }
    }

    /**
     * 显示该日的所有会议消息
     */
    private void showDayMeeting(boolean hasMeeting,int day) {
        if (hasMeeting) {
            dayContentEmpty.collapse();
            dayContent.expand();

            if (day%3==0){
                mymeetingTitle.setText("关于产品的优化");
                mymeetingAddress.setText("C2楼1208会议室");
                mymeetingTime.setText("2019/06/"+day+" 13:00 - 15:30");
                mymeetingWorker.setText("行政部");
            } else if (day%3==1){
                mymeetingTitle.setText("交通管理制度的优化和漏洞");
                mymeetingAddress.setText("B2楼402会议室");
                mymeetingTime.setText("2019/06/"+day+" 08:30 - 11:30");
                mymeetingWorker.setText("北京市第三区交通委");
            }else{
                mymeetingTitle.setText("产品的进一步市场拓展");
                mymeetingAddress.setText("A1楼711会议室");
                mymeetingTime.setText("2019/06/"+day+" 13:00 - 15:30");
                mymeetingWorker.setText("销售部");
            }

        }else {
            dayContent.collapse();
            dayContentEmpty.expand();
        }
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    private boolean flag = false;
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTvLunar.setVisibility(View.VISIBLE);
        mTvYear.setVisibility(View.VISIBLE);
        mTvMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTvYear.setText(String.valueOf(calendar.getYear()));
        mTvLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();

        if (flag)
            showDayMeeting(!calendar.getScheme().trim().isEmpty(),calendar.getDay());
        else
            flag = true;
    }

    @Override
    public void onYearChange(int year) {
        mTvMonthDay.setText(String.valueOf(year));
    }

    @Override
    public void onCalendarLongClickOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarLongClick(Calendar calendar) {
        Log.e("onDateLongClick", "  -- " + calendar.getDay() + "  --  " + calendar.getMonth());
    }

    public void doRefresh() {
        if (App.iSLOGIN()) {
            tipLayout.setVisibility(View.GONE);
            activeLayout.setVisibility(View.VISIBLE);
            dayLayout.setVisibility(View.VISIBLE);
            initCalendarEvent();
//            LogUtils.printLog("getSelect = " + mCalendarView.getSelectedCalendar().getYear() +
//                    mCalendarView.getSelectedCalendar().getMonth() + mCalendarView.getSelectedCalendar().getDay());
            // TODO:Here is a bug,getScheme() not working[get nothing..]
            if (!mCalendarView.getSelectedCalendar().getScheme().trim().isEmpty()) {
                dayContentEmpty.collapse();
                dayContent.expand();
            } else {
                dayContentEmpty.expand();
                dayContent.collapse();
            }
        } else {
            activeLayout.setVisibility(View.GONE);
            dayLayout.setVisibility(View.GONE);
            tipLayout.setVisibility(View.VISIBLE);
            layoutPushtip.setVisibility(View.GONE);
            mCalendarView.setSchemeDate(null);
        }
    }

    public void showPushtip() {
        if (App.iSLOGIN())
            layoutPushtip.setVisibility(View.VISIBLE);
    }

    private HomeActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (HomeActivity) context;
    }

    // 处理service弹出框的点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clean:
            case R.id.water:
            case R.id.other:
                String content = "";
                if (view.getId() == R.id.clean)
                    content = "打扫卫生";
                else if (view.getId() == R.id.water)
                    content = "倒水";
                else {
                    content = "未知服务";
                }
                dialog.cancel();
                RetrofitService
                        .addService((int)App.getUserUidKey(), content)
                        .subscribe(responseBody -> {
                            LogUtils.printLog(responseBody.string());
                            ToastUtils.ToastLong("已经为您将服务通知给后台");
                        }, throwable -> LogUtils.printError("send service err " + throwable));
                break;
            case R.id.sign:
                dialog.cancel();
                jumpToActivityBottom(FaceRecognizeActivity.class);
            break;
        }
    }
}
