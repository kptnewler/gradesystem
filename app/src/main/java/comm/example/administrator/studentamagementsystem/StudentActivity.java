package comm.example.administrator.studentamagementsystem;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class StudentActivity extends AppCompatActivity {
    private String stu_id,stu_c,stu_mo,stu_gao,stu_english,stu_ti;
    private MyDatabaseHelper dbHelper = new MyDatabaseHelper(this, "class_one.db3", 20);//建立数据库
    private SQLiteDatabase db_write;//获取一个可写的数据库
    private SQLiteDatabase db_read;//获取一个可读的数据库
    private Cursor cursor;
    private String[] sub_name;//存储课程信息
    private String[] sub_score;//存储课程分数
    private String[][] sub_information;//存储具体信息
    private  int p;

    @Override
    //单击左箭头
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_layout);

        //actionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //设置是否显示图标
        actionBar.setDisplayHomeAsUpEnabled(true);
        //设置左箭头
        actionBar.setDisplayHomeAsUpEnabled(true);

        //获取到传入的值
        Intent intent = getIntent();
        stu_id = intent.getStringExtra("stu_id");


        //获取可写，可读的数据库
        db_read = dbHelper.getReadableDatabase();//获取一个可写的数据库
        db_write = dbHelper.getReadableDatabase();//获取一个可读的数据库

        //获取该学号学生的成绩
        try {
            cursor = db_read.rawQuery("select * from class_one",null);
            for(p = 0; p < cursor.getCount(); p++)
            {
                cursor.moveToPosition(p);
                if(cursor.getString(cursor.getColumnIndex("stu_id")).equals(stu_id)) {
                    break;
                }
            }
            stu_c = cursor.getString(cursor.getColumnIndex("stu_c"));
            stu_mo = cursor.getString(cursor.getColumnIndex("stu_mo"));
            stu_gao = cursor.getString(cursor.getColumnIndex("stu_gao"));
            stu_english = cursor.getString(cursor.getColumnIndex("stu_english"));
            stu_ti = cursor.getString(cursor.getColumnIndex("stu_ti"));
            Toast.makeText(StudentActivity.this,stu_c,Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Log.e("1",e.toString());
            Log.e("2",""+stu_c);
            Log.e("3",""+stu_id);
            Log.e("4",""+cursor.getCount());
        }

        init_data();
        ExpandableListView expandableListView = (ExpandableListView)findViewById(R.id.stu_expandable);
        LinearLayout expandable_list_head_layout = (LinearLayout)getLayoutInflater().inflate(R.layout.student_head_list_layout,null);
        expandableListView.addHeaderView(expandable_list_head_layout);
        expandableListView.setAdapter(new MyAdapter());
    }
    //初始化数值
    public void init_data()
    {
        sub_name  = new String[]{"大学英语1","C语言","电路与模拟电子技术1","高等数学A1","大学体育1"};
        sub_score = new String[]{stu_english,stu_c,stu_mo,stu_gao,stu_ti};
        sub_information = new String[][]{
                {"课程名称:大学英语1","课程性质:公共必修课","学分:4.0", "绩点:"+ grade_point(stu_english),"成绩:"+stu_english,"专业:计算机科学与技术"},
                {"课程名称:C语言","课程性质:学科基础课","学分:4.0", "绩点:"+ grade_point(stu_c),"成绩:"+stu_c,"专业:计算机科学与技术"},
                {"课程名称:电路与模拟电子技术1","课程性质:学科基础课","学分:2.0", "绩点:"+ grade_point(stu_mo),"成绩"+stu_mo,"专业:计算机科学与技术"},
                {"课程名称:高等数学A1","课程性质:公共必修课","学分:5.0", "绩点:"+ grade_point(stu_gao),"成绩:"+stu_gao,"专业:计算机科学与技术"},
                {"课程名称:大学体育","课程性质:公共必修课","学分:1.0", "绩点:"+ grade_point(stu_ti),"成绩:"+stu_ti,"专业:计算机科学与技术"},
        };
    }

    //计算绩点
    public String grade_point(String stu)
    {
        double p;
        int s = Integer.parseInt(stu);
        if(s >= 60) {
            p = (double) ((s - 60) / 10 + 1.0);
        }else {
            p = (double)0;
        }
        DecimalFormat f = new DecimalFormat("0.00");
        return f.format(p);
    }

    //重写可展开类
    class MyAdapter extends BaseExpandableListAdapter
    {
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return sub_information[groupPosition][childPosition];
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return sub_information[groupPosition].length;
        }

        //设置每个子选项的外观
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if(convertView == null){
                LayoutInflater inflater = (LayoutInflater)StudentActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expand_children_layout,null);
            }
            TextView sub_information_text = (TextView)convertView.findViewById(R.id.expand_sub_information);
            sub_information_text.setText(getChild(groupPosition,childPosition).toString());
            return sub_information_text;
        }

        //设置指定组的位置数据
        @Override
        public Object getGroup(int groupPosition) {
            return sub_name[groupPosition];
        }

        @Override
        public int getGroupCount() {
            return sub_name.length;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.expandable_parent_layout,null);
            TextView sub_name_text = (TextView)linearLayout.findViewById(R.id.expand_sub_text);
            TextView sub_score_text = (TextView)linearLayout.findViewById(R.id.expand_score_text);
            sub_name_text.setText(getGroup(groupPosition).toString());
            sub_score_text.setText(sub_score[groupPosition]);
            if(Integer.parseInt(sub_score[groupPosition]) < 60)
                sub_score_text.setTextColor(Color.RED);
            else
                sub_score_text.setTextColor(Color.GREEN);
            return linearLayout;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
