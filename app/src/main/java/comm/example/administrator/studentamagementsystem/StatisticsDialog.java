package comm.example.administrator.studentamagementsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/5/17.
 */
public class StatisticsDialog {
    private Context context1;
    private Cursor cursor1;
    private String mstuCColumn,
            mstuMoColumn, mstuGaoColumn, mstuEnglishColumn, mstuTiColumn;//数据库表列名称
    private LayoutInflater mInflater;
    private String subject = "开始";
    private String score = "开始";
    private int count = 0;
    private int mCount;
    private int i;
    private String[] num;
    ;

    //构造函数
    public StatisticsDialog(Context context, Cursor cursor, String stuCColumn, String stuMoColumn, String stuGaoColumn,
                            String stuEnglishColumn, String stuTiColumn, int amount) {
        context1 = context;
        cursor1 = cursor;
        mstuCColumn = stuCColumn;
        mstuMoColumn = stuMoColumn;
        mstuGaoColumn = stuGaoColumn;
        mstuEnglishColumn = stuEnglishColumn;
        mstuTiColumn = stuTiColumn;
        mCount = amount;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        num = new String[mCount];
        setDialog();
    }

    //创建对话框
    public void setDialog() {
        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context1);
        LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.statistics_layout, null);
        builder.setView(linearLayout);
        RadioGroup rg1 = (RadioGroup) linearLayout.findViewById(R.id.scoreRadioGroup);//选择分数段
        RadioGroup rg2 = (RadioGroup) linearLayout.findViewById(R.id.subjectRadioGroup);//选择科目
        Button b1 = (Button) linearLayout.findViewById(R.id.statistics_sure_button);//确定按钮
        Button b2 = (Button) linearLayout.findViewById(R.id.statistics_cancel_button);//取消按钮

        dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(1000, 1500);
        //是否及格判断
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.failRadioButton) {
                    score = "挂科";
                } else if (checkedId == R.id.passRadioButton) {
                    score = "及格";
                }
            }
        });
        //选择科目判断
        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.cRadioButton)
                    subject = "C语言";
                else if (checkedId == R.id.moRadioButton)
                    subject = "模电";
                else if (checkedId == R.id.gaoRadioButton)
                    subject = "高数";
                else if (checkedId == R.id.englishRadioButton)
                    subject = "英语";
                else if (checkedId == R.id.tiRadioButton)
                    subject = "体育";
            }
        });

        //确定按钮
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    queryData(subject);
                    judge_score(score);
                    if (score == "及格") {
                        Toast.makeText(context1, subject + "及格的人数为" + count, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else if (score == "挂科"){
                        Toast.makeText(context1, subject + "不及格的人数为" + count, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                    else if(score == "开始")
                        Toast.makeText(context1, "请您选择分数段", Toast.LENGTH_SHORT).show();
                    else if(score == "开始")
                        Toast.makeText(context1,"请您选择科目",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("3", e.toString());
                }
            }
        });

        //取消按钮
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    //查询数据
    public void queryData(String subject) {
        try {
            cursor1.moveToFirst();
            switch (subject) {
                case "C语言":
                    for (i = 0; i < mCount; i++) {
                        cursor1.moveToPosition(i);
                        num[i] = cursor1.getString(cursor1.getColumnIndex(mstuCColumn));
                    }
                    break;
                case "模电":
                    for (i = 0; i < mCount; i++) {
                        cursor1.moveToPosition(i);
                        num[i] = cursor1.getString(cursor1.getColumnIndex(mstuMoColumn));

                    }
                    break;
                case "高数":
                    for (i = 0; i < mCount; i++) {
                        cursor1.moveToPosition(i);
                        num[i] = cursor1.getString(cursor1.getColumnIndex(mstuGaoColumn));
                    }
                    break;
                case "英语":
                    for (i = 0; i < mCount; i++) {
                        cursor1.moveToPosition(i);
                        num[i] = cursor1.getString(cursor1.getColumnIndex(mstuEnglishColumn));
                    }
                    break;
                case "体育":
                    for (i = 0; i < mCount; i++) {
                        cursor1.moveToPosition(i);
                        num[i] = cursor1.getString(cursor1.getColumnIndex(mstuTiColumn));
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e("2", e.toString());
        }
    }

    //进行判断分数阶段然后统计人数
    public void judge_score(String score) {
        count = 0;
        if (score == "及格") {
            for (int j = 0; j < mCount; j++) {
                int s = Integer.parseInt(num[j]);
                if (s >= 60)
                    count++;
            }
        } else if(score == "挂科"){
            for (int j = 0; j < mCount; j++) {
                int s = Integer.parseInt(num[j]);
                if (s < 60)
                    count++;
            }
        }
    }

}
