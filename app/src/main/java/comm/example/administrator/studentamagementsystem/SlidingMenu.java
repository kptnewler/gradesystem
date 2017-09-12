package comm.example.administrator.studentamagementsystem;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016/5/13.
 */
public class SlidingMenu extends HorizontalScrollView{
    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;
    private int mMenuRightPadding = 50;
    private  boolean once = false;
    private  int mMenuWidth;

    //未使用自定义属性调用
    public SlidingMenu(Context context,AttributeSet attrs)
    {
        super(context, attrs);
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMeterics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMeterics);
        mScreenWidth = outMeterics.widthPixels;
        //把dp转换为px
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,context.getResources().getDisplayMetrics());
    }

    @Override
    //设置子View宽和高
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(!once)//防止多次调用
        {
            mWapper = (LinearLayout)getChildAt(0);//一个元素
            mMenu = (ViewGroup)mWapper.getChildAt(0);
            mContent = (ViewGroup)mWapper.getChildAt(1);

            mMenuWidth =  mMenu.getLayoutParams().width = 850;
            mContent.getLayoutParams().width = mScreenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    //隐藏Menu
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //隐藏
        if(changed) {
            this.scrollTo(mMenuWidth, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if(scrollX >= mMenuWidth / 2)
                {
                    this.smoothScrollTo(mMenuWidth,0);
                }
                else {
                    this.smoothScrollTo(0,0);
                }
                return  true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public View getRootView() {
        return super.getRootView();
    }
}
