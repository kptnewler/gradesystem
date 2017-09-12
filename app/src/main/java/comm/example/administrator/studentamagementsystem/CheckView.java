package comm.example.administrator.studentamagementsystem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import comm.example.administrator.studentamagementsystem.CheckUtil;

/**
 * Created by Administrator on 2016/5/31.
 */
public class CheckView extends View{
    Context mContext;
    public boolean ak;
    int[] CheckNum = null;
    //设置画笔
    Paint mTempPaint = new Paint();
    //验证码,AttributeSet自己创建属性
    public CheckView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        mContext = context;
        mTempPaint.setAntiAlias(true);//判断是否消除抗锯齿,就是图形边缘的锯齿形状
        mTempPaint.setTextSize(CheckUtil.TEXT_SIZE);//设置绘制文本是文字大小
        mTempPaint.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(CheckUtil.COLOR);//设置颜色
        final int height = getHeight();//获取checkView的高度
        final int width = getWidth();//获取checkView的宽度
        int dx = 40;
        //绘制文本
        for(int i = 0; i < 4; i++)
        {
            /*第一个参数表示文本
            * 第二个参数起点
            * 第三个参数终点
            * 画笔*/
            canvas.drawText(""+CheckNum[i],dx,CheckUtil.getPosition(height),mTempPaint);
            dx += width / 5;//往右边移动
        }
        //绘制划线
        int[] line1,line2;
        line1 = CheckUtil.getLine1(height, width);
        /*第一个参数起点的X坐标
         * 第二个参数起点的Y坐标
         * 第三个参数终点的X坐标
         * 第四个参数终点的Y坐标
         * 第五个参数画笔*/
        canvas.drawLine(line1[0],line1[1],line1[2],line1[3],mTempPaint);
        line2 = CheckUtil.getLine2(height,width);
        canvas.drawLine(line2[0],line2[1],line2[2],line2[3],mTempPaint);
        //绘制小圆点
        int [] point;
        for(int i = 0; i < CheckUtil.POINT_NUM; i++)
        {
            point = CheckUtil.getPoint(height,width);
            /*
            * 圆心的X坐标
            * 圆心的Y坐标
            * 圆的半径
            * 画笔*/
            canvas.drawCircle(point[0],point[1],1,mTempPaint);
        }
    }
    //设置验证码
    public void setCheckNum(int [] setNum)
    {
        CheckNum = setNum;
    }
    //获得验证码
    public int[] getCheckNum()
    {
        return CheckNum;
    }
    //直接重新绘制
    public void invaliCheckNum()
    {
        invalidate();
    }

}
