package comm.example.administrator.studentamagementsystem;

import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/5/31.
 */
public class CheckUtil {
    //验证码更新时间
    public static final int UPDATE_TIME = 2000;
    //点数设置
    public static final int POINT_NUM = 100;
    //线段数设置
    public static final int LINE_NUM = 2;
    //设置背景颜色
    public static final int COLOR = Color.WHITE;
    //设置验证码个数
    public static int TEXT_LENGTH = 4;
    //设置验证码字体大小
    public static int TEXT_SIZE = 40;

    //获取随机数字
    public static int [] getCheckNum(){
        int[] tempCheckNum = new int[TEXT_LENGTH];
        for(int i = 0; i < TEXT_LENGTH; i++){
            //产生随机数,随机产生0-10的数字
            tempCheckNum[i] = (int)(Math.random() * 10);
        }
        return tempCheckNum;
    }

    /*随机产生划线的起始点坐标和结束点坐标
    * height 传入CheckView的高度值
    * width 传入CheckView的宽度值
    * 返回起点坐标和终点坐标*/
    public static int[] getLine1(int height,int width)
    {
        int[] Coordinate = {0,0,0,0};
        Coordinate[0] = 42;
        Coordinate[1] = 30;
        Coordinate[2] = 30;
        Coordinate[3] = 80;
        return Coordinate;
    }

    public static int[] getLine2(int height,int width)
    {
        int[] Coordinate = {0,0,0,0};
        Coordinate[0] = 27;
        Coordinate[1] = 45;
        Coordinate[2] = 199;
        Coordinate[3] = 50;
        return Coordinate;
    }

    /*随机产生点的圆心的坐标
    * height 传入CheckView的高度值
    * width 传入CheckView的宽度值*/
    public static int[] getPoint(int height,int width)
    {
        int circleCenter[] ={0,0,0,0};
        circleCenter[0] = (int)(Math.random() * width);
        circleCenter[1] = (int)(Math.random() * height);
        return circleCenter;
    }

    /*验证是否正确
    * userCheck用户输入的验证码
    * checkNum验证控件产生的随机数*/
    public static boolean checkNum(String userCheck,int[] checkNum)
    {
        String checkString = "";//保存获取到的随机数
        for(int i = 0; i < 4; i++){
            checkString += checkNum[i];
        }
        if(userCheck.equals(checkString))
            return true;
        else
            return  false;
    }

    /*计算验证码的绘制y点位置
    * height传入CheckView 的高度值*/
    public static int getPosition(int height)
    {
        int position = (int)(60);
        if(position < 20){
            position = position + 20;
        }
        return position;
    }
}
