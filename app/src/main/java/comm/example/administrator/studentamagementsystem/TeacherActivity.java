package comm.example.administrator.studentamagementsystem;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class TeacherActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper = new MyDatabaseHelper(this,"class_one.db3",20);//建立数据库
    private TextView text1,text2,text3,text4,text5,text6;
    private Button sure_button1;
    private Button cancel_button1;
    private EditText stu_id_edit1,stu_name_edit1,stu_c_edit1,stu_gao_edit1,stu_ti_edit1,stu_mo_edit1,stu_english_edit1;
    boolean b1,b2,b3;//检测是否匹配正则表达式
    boolean a1 = false,a2 = false,a3 = false,a4 = false,a5 = false,a6 = false,a7 = false,a8 = false;//判断信息是否输入正确条件
    SQLiteDatabase  db_write;//一个可写的数据库
    SQLiteDatabase db_read ;//一个可读的数据库
    String stu_id,stu_c,stu_mo,stu_gao,stu_english,stu_ti,stu_name;//存储数据库各种信息
    ListView myDbList ;//显示全部信息的列表项
    Cursor cursor;//游标
    AlertDialog dialog;
    DbSimpleAdapter dbSimpleAdapter;//适配器
    CheckBox headerCheckBox;//列头的按钮
    Button headerButton;//列头的按钮
    ArrayList<Integer> checkList = new ArrayList<Integer>();

    @Override
    //搜索框
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_search, menu);
       MenuItem searchItem = menu.findItem(R.id.search_in_menu);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("请输入学生的学号");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SQLiteDatabase read = dbHelper.getReadableDatabase();
                Cursor q_cursor = read.query("class_one",null,"stu_id = ?", new String[]{query},null,null,null,null);
                LinearLayout mLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.activity_content,null);
                myDbList = (ListView)findViewById(R.id.myDbList);
                if(q_cursor != null) {
                    dbSimpleAdapter = new DbSimpleAdapter(
                            TeacherActivity.this, R.layout.db_list_layout, q_cursor, new String[]{"stu_id", "stu_name", "stu_avr"},
                            new int[]{R.id.db_stuId_text, R.id.db_name_text, R.id.db_avr_text},
                            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, "_id", "stu_id", "stu_name", "stu_avr",
                            "stu_c", "stu_mo", "stu_gao", "stu_english", "stu_ti"
                            , cursor.getCount(), db_write, db_read);
                    myDbList.setAdapter(dbSimpleAdapter);
                }else {
                    Toast.makeText(TeacherActivity.this,"查无此人，请您确认后再次查询",Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_content);
        myDbList = (ListView)findViewById(R.id.myDbList);

        //actionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //设置是否显示图标
        actionBar.setDisplayHomeAsUpEnabled(true);
        //设置左箭头
        actionBar.setDisplayHomeAsUpEnabled(true);

        //获取TextView对象
        text1 = (TextView)findViewById(R.id.text1);//新建
        text2 = (TextView)findViewById(R.id.text2);//删除
        text3 = (TextView)findViewById(R.id.text3);//统计
        text4 = (TextView)findViewById(R.id.text4);//排序
        text5 = (TextView)findViewById(R.id.text5);//退出系统
        text6 = (TextView)findViewById(R.id.text6);//刷新数据

        //数据库
        db_write = dbHelper.getWritableDatabase();//获取一个可写的数据库
        db_read = dbHelper.getReadableDatabase();//获取一个可读的数据库

        //获取表内所有数据
        setHeader();

        //设置标题栏
        setAdapter();

        //新建信息点击事件
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addView(v);
            }
        });

     /*   //全选事件处理
        check_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbSimpleAdapter.checkAllItems();
                dbSimpleAdapter.notifyDataSetChanged();
            }
        });

        //反选事件处理
        check_reverse_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbSimpleAdapter.checkReverseItems();
                dbSimpleAdapter.notifyDataSetChanged();
            }
        });

        //取消选择事件处理
        check_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbSimpleAdapter.checkCancelItems();
                dbSimpleAdapter.notifyDataSetChanged();
            }
        });*/

        //删除信息对话框
        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> deleteList = dbSimpleAdapter.getSelectedItems();
                try {
                    for (int id : deleteList) {
                        db_write.delete("class_one", "_id = ?", new String[]{Integer.toString(id)});
                        setAdapter();
                    }
                    Toast.makeText(TeacherActivity.this, "删除数据成功", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("exception", e.toString());
                }
            }
        });

        //刷新数据
        text6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db_write = dbSimpleAdapter.getDbWriter();
                setAdapter();
                Toast.makeText(TeacherActivity.this,"刷新数据成功",Toast.LENGTH_SHORT).show();
            }
        });

        //统计信息
        text3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor1 = db_read.rawQuery("select * from class_one",null);
                StatisticsDialog sDialog = new StatisticsDialog(TeacherActivity.this,cursor1,"stu_c","stu_mo",
                        "stu_gao","stu_english","stu_ti",cursor1.getCount());
            }
        });

        //成绩排序
        text4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortAdapter();
                Toast.makeText(TeacherActivity.this,"成绩排序完成",Toast.LENGTH_SHORT).show();
            }
        });

        //退出系统
        text5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Toast.makeText(TeacherActivity.this, "退出系统成功", Toast.LENGTH_SHORT).show();
            }
        });

        //全部选择按钮
        headerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbSimpleAdapter.checkReverseItems();
                dbSimpleAdapter.notifyDataSetChanged();
            }
        });

        //查看全部按钮
        headerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapter();
            }
        });
    }

    //新建信息对话框
    public  void addView(View v)
    {
        //创建对话框
        LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.add_layout,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(linearLayout);
         dialog =  builder.create();

        //获取按钮对象
        sure_button1 = (Button)linearLayout.findViewById(R.id.sure_button1);
        cancel_button1 = (Button)linearLayout.findViewById(R.id.cancel_button1);
        //获取EditText对象
        stu_id_edit1 = (EditText)linearLayout.findViewById(R.id.stu_id_edit1);
        stu_name_edit1 = (EditText)linearLayout.findViewById(R.id.stu_name_edit1);
        stu_c_edit1 = (EditText)linearLayout.findViewById(R.id.stu_c_edit1);
        stu_ti_edit1 = (EditText)linearLayout.findViewById(R.id.stu_ti_edit1);
        stu_mo_edit1 = (EditText)linearLayout.findViewById(R.id.stu_mo_edit1);
        stu_english_edit1 = (EditText)linearLayout.findViewById(R.id.stu_english_edit1);
        stu_gao_edit1 = (EditText)linearLayout.findViewById(R.id.stu_gao_edit1);

        //确定按钮点击事件
        sure_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stu_ti = stu_ti_edit1.getText().toString();
                stu_id = stu_id_edit1.getText().toString();
                stu_name = stu_name_edit1.getText().toString();
                stu_c = stu_c_edit1.getText().toString();
                stu_mo = stu_mo_edit1.getText().toString();
                stu_ti = stu_ti_edit1.getText().toString();
                stu_gao = stu_gao_edit1.getText().toString();
                stu_english = stu_english_edit1.getText().toString();
                a1 = is_accord_id(stu_id);
                a2 = is_Accord(stu_c);
                a3 = is_Accord(stu_mo);
                a4 = is_Accord(stu_gao);
                a5 = is_Accord(stu_english);
                a6 = is_Accord(stu_ti);
                a7 = stu_id_exist(stu_id);
                a8 = is_accord_name(stu_name);
                if (a1 && a2 && a3 && a4 && a5 && a6 && a7 && a8) {
                    ContentValues values = new ContentValues();
                    a1 = a2 = a3 = a4 = a5 = a6 = a7 = a8 = false;
                    values.put("stu_id", stu_id);
                    values.put("stu_name",stu_name);
                    values.put("stu_c", stu_c);
                    values.put("stu_mo", stu_mo);
                    values.put("stu_gao", stu_gao);
                    values.put("stu_english", stu_english);
                    values.put("stu_ti", stu_ti);
                    values.put("stu_avr",average(stu_c,stu_mo,stu_gao,stu_english,stu_ti));
                    db_write.insert("class_one", null, values);

                    Toast.makeText(TeacherActivity.this, "添加信息成功", Toast.LENGTH_SHORT).show();
                    try {
                        setAdapter();
                        dialog.dismiss();
                    }
                    catch (Exception e)
                    {
                        Log.i("exception", e.toString());
                    }
                } else if(!a1){
                    Toast.makeText(TeacherActivity.this, "请输入正确的学号", Toast.LENGTH_SHORT).show();
                }else if(!a2){
                    warn_information("C语言");
                }else if(!a3){
                    warn_information("模电");
                }else if(!a4){
                    warn_information("高数");
                }else if(!a5){
                    warn_information("英语");
                }else if(!a6){
                    warn_information("体育");
                }else if(!a7) {
                    Toast.makeText(TeacherActivity.this,"该学号已经存在,请您重新输入",Toast.LENGTH_SHORT).show();
                }else if(!a8){
                    Toast.makeText(TeacherActivity.this,"请输入中文姓名",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //返回按钮点击事件
        cancel_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(1000, 1400);
    }


    //将数据库内容显示出来
    public void setAdapter()
    {
        cursor = db_read.rawQuery("select * from class_one",null);
        dbSimpleAdapter = new DbSimpleAdapter(
                TeacherActivity.this,R.layout.db_list_layout,cursor,new String[]{"stu_id","stu_name","stu_avr"},
                new int[]{R.id.db_stuId_text,R.id.db_name_text,R.id.db_avr_text},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,"_id","stu_id","stu_name","stu_avr",
                        "stu_c","stu_mo","stu_gao","stu_english","stu_ti"
                ,cursor.getCount(),db_write,db_read);
        myDbList.setAdapter(dbSimpleAdapter);
    }

    //排序显示
    public void sortAdapter(){
        Cursor cursorSort = db_read.rawQuery("select * from class_one ORDER BY stu_avr desc, _id desc", null);
        dbSimpleAdapter = new DbSimpleAdapter(
                TeacherActivity.this,R.layout.db_list_layout,cursorSort,new String[]{"stu_id","stu_name","stu_avr"},
                new int[]{R.id.db_stuId_text,R.id.db_name_text,R.id.db_avr_text},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,"_id","stu_id","stu_name","stu_avr",
                "stu_c","stu_mo","stu_gao","stu_english","stu_ti"
                ,cursor.getCount(),db_write,db_read);
        myDbList.setAdapter(dbSimpleAdapter);
    }

    //设置标题栏
    public void setHeader()
    {
        LinearLayout headerLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.header_layout,null);
        myDbList.addHeaderView(headerLayout,null,false);
        headerCheckBox = (CheckBox)headerLayout.findViewById(R.id.header_checkbox);
        headerButton = (Button)headerLayout.findViewById(R.id.all_button);
    }

    //计算平均分
    public Double average(String c,String mo,String gao,String english, String ti)
    {
        int int_c = Integer.parseInt(c);
        int int_mo = Integer.parseInt(mo);
        int int_gao = Integer.parseInt(gao);
        int int_english = Integer.parseInt(english);
        int int_ti = Integer.parseInt(ti);
        if(c == null)
            int_c = 0;
        if(mo == null)
            int_mo = 0;
        if(gao == null)
            int_gao = 0;
        if(english == null)
            int_english = 0;
        if(ti == null)
            int_ti =0;
        double avr = (double)(int_c + int_mo + int_gao + int_english + int_ti)/5;;
       DecimalFormat df = new DecimalFormat("0.000");//保留几位小数
        String s = df.format(avr);

        return Double.parseDouble(s);
    }

    //判断是否符合标准
    public boolean is_Accord(String stu)
    {
        b1 = stu.matches("[1-9][0-9]");
        b2 = stu.matches("[0-9]");
        b3 = stu.matches("100");
        if (b1 || b2 || b3) {
            return true;
        } else {
            return false;
        }
    }

    //判断用户名是否符合标准
    public boolean is_accord_name(String stu_name)
    {
        if(stu_name.matches("[\u4e00-\u9fa5]{1,4}"))
            return  true;
        else
            return false;
    }
    //判断学号是否符合标准
    public boolean is_accord_id(String stu_id)
    {
      return true;
    }

    //输出警告信息
    public void warn_information(String text)
    {
        Toast.makeText(TeacherActivity.this, "请输入正确的"+text+"分数", Toast.LENGTH_SHORT).show();
    }

    //判断学号是否存在
    public boolean stu_id_exist(String stu_id)
    {
        Cursor cursor2 = db_read.rawQuery("select * from class_one",null);
        for(int k = 0; k < cursor2.getCount(); k++ )
        {
            cursor2.moveToPosition(k);
            String exist_text = cursor2.getString(cursor2.getColumnIndex("stu_id"));
            if(stu_id.equals(exist_text))
            {
                return  false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dbHelper != null)
            dbHelper.close();
    }
}


