package comm.example.administrator.studentamagementsystem;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton, registerButton;
    private String termText;
    private TextView titleText;
    private RadioGroup radioGroup;
    private RadioButton r1, r2, r3;
    private TextView text0, text1, text2, edit0;
    private EditText edit1, edit2;
    private AlertDialog dialog;
    private MyDatabaseHelper dbHelper = new MyDatabaseHelper(this, "class_one.db3", 20);//建立数据库
    private SQLiteDatabase db_write;//获取一个可写的数据库
    private SQLiteDatabase db_read;//获取一个可读的数据库
    private CheckBox remember_password;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private CheckView checkView;//验证码
    private TextView changeCodeText;//刷新验证码按钮
    private EditText checkEditText;//输入验证码
    private int [] checkNum =null;
    private TextView ModifyText;
    String term, stu_num, stu_password,check_code;
    Boolean a1 = false, a2 = false, a3 = false, a4 = false, a5 = false,a6 = false;//进行判断是否符合标准
    int c = 0;
    RadioGroup rg;//单选按钮

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
        setContentView(R.layout.activity_main);

        init_view();//初始化
        initCheckView();//初始化验证码

        //获取checkBox 记住密码
        remember_password = (CheckBox)findViewById(R.id.remember);

        //获取SharedPreferences对象
        preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        //设置是否要记住密码
        boolean isRemember = preferences.getBoolean("remember_password",false);//默认设置为false
        if(isRemember){
            String user = preferences.getString("stu_id","请输入您的账号");//第一个参数是键值,第二参数是如果没有这个键值返回的默认值
            String password = preferences.getString("stu_password","请输入您的密码");
            String term = preferences.getString("stu_term","点击请输入您的学期");
            edit1.setText(user);
            edit2.setText(password);
            edit0.setText(term);
            edit0.setTextColor(Color.BLACK);
            remember_password.setChecked(true);
        }

        //获取可写，可读的数据库
        db_read = dbHelper.getReadableDatabase();//获取一个可写的数据库
        db_write = dbHelper.getWritableDatabase();//获取一个可读的数据库
       // Toast.makeText(LoginActivity.this,""+db_read,Toast.LENGTH_SHORT).show();
        //输入学期
        edit0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit0.setTextColor(Color.BLACK);
                termChoice(v);
            }
        });

        //单选按钮触发事件
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.studentRadio) {
                    c = checkedId;
                } else if (checkedId == R.id.teacherRadio) {
                    c = checkedId;
                }
            }
        });

        //登录按钮
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取文本
                term = edit0.getText().toString();
                stu_num = edit1.getText().toString();
                stu_password = edit2.getText().toString();
                check_code = checkEditText.getText().toString();
                //文本框输入不能为空
                if (term != null) {
                    a1 = true;
                }
                if (stu_num != null) {
                    a4 = true;
                }
                if (stu_password != null) {
                    a5 = true;
                }
                //判断学号，密码是否输入正确
                if (stu_id_true(stu_num)) {
                    a2 = true;
                }
                if (stu_password_true(stu_password)) {
                    a3 = true;
                }
                if (CheckUtil.checkNum(check_code,checkNum)) {
                    //Toast.makeText(LoginActivity.this, ""+check_code+"  "+checkNum, Toast.LENGTH_SHORT).show();
                    a6 = true;
                }

                //输入正确就进入学生端
                if(c == R.id.studentRadio) {
                    if (a1 && a2 && a3 && a4 && a5 && a6 ) {
                        a1 = a2 = a3 = a4 = a5 = a6  = false;
                        editor = preferences.edit();//获取Editor对象
                        //检测密码复选框是否被选中
                        if(remember_password.isChecked()) {
                            editor.putBoolean("remember_password", true);
                            editor.putString("stu_id", stu_num);
                            editor.putString("stu_password", stu_password);
                            editor.putString("stu_term", term);

                        }else {
                            editor.clear();//清空
                        }
                        editor.commit();//提交数据的修改
                        Intent intent1 = new Intent(LoginActivity.this, StudentActivity.class);
                        intent1.putExtra("stu_id", stu_num);
                        startActivity(intent1);
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    } else if (!a1) {
                        Toast.makeText(LoginActivity.this, "学期不能为空", Toast.LENGTH_SHORT).show();
                    } else if (!a4) {
                        Toast.makeText(LoginActivity.this, "学号不能为空", Toast.LENGTH_SHORT).show();
                    } else if (!a5) {
                        Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    } else if (!a2) {
                        Toast.makeText(LoginActivity.this, "您输入的学号错误，请您重新输入", Toast.LENGTH_SHORT).show();
                    } else if (!a3) {
                        Toast.makeText(LoginActivity.this, "您输入的密码错误，请您重新输入", Toast.LENGTH_SHORT).show();
                    }else if(!a6){
                        Toast.makeText(LoginActivity.this,"您输入的验证码错误,请您重新输入",Toast.LENGTH_SHORT).show();
                    }
                }else if(c == R.id.teacherRadio){
                    Intent intent1 = new Intent(LoginActivity.this,TeacherActivity.class);
                    startActivity(intent1);
                }else if(c == 0){
                    Toast.makeText(LoginActivity.this,"请您选择身份",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //注册按钮
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //换一张按钮单击事件
        changeCodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCheckView();
            }
        });

        //修改密码
        ModifyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ModifyPasswordActivity.class);
                startActivity(intent);
            }
        });
    }//OnCreate结束

    //初始化View
    public void init_view()
    {
        PermissionUtils.getInstance().build(this, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        //设置标题字体颜色和字体
        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setTextColor(Color.BLACK);

        //actionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //设置是否显示图标
        actionBar.setDisplayHomeAsUpEnabled(true);
        //设置左箭头
        actionBar.setDisplayHomeAsUpEnabled(true);

        //获取TextView和EditView对象,Button对象
        text0 = (TextView) findViewById(R.id.term_text);
        text1 = (TextView) findViewById(R.id.id_text);
        text2 = (TextView) findViewById(R.id.password_text);
        edit0 = (TextView) findViewById(R.id.term_edit);
        edit1 = (EditText) findViewById(R.id.id_edit);
        edit2 = (EditText) findViewById(R.id.password_edit);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.register_button);
        ModifyText = (TextView)findViewById(R.id.modify_text);
        //获取RadioGroup
        rg = (RadioGroup) findViewById(R.id.identityGroup);

        //获取验证码对象
        checkView = (CheckView)findViewById(R.id.checkCodeView);
        checkEditText = (EditText)findViewById(R.id.checkEditText);
        changeCodeText = (TextView)findViewById(R.id.changeCodeText);
    }

    //判断学号是否正确
    public boolean stu_id_true(String stu_id) {
        Cursor cursor2 = db_read.rawQuery("select * from student", null);
        for (int k = 0; k < cursor2.getCount(); k++) {
            cursor2.moveToPosition(k);
            String exist_text = cursor2.getString(cursor2.getColumnIndex("stu_num"));
            if (stu_id.equals(exist_text)) {
                return true;
            }
        }
        return false;
    }

    //判断密码是否正确
    public boolean stu_password_true(String stu_password) {
        Cursor cursor2 = db_read.rawQuery("select * from student", null);
        for (int k = 0; k < cursor2.getCount(); k++) {
            cursor2.moveToPosition(k);
            String exist_text = cursor2.getString(cursor2.getColumnIndex("stu_password"));
            if (stu_password.equals(exist_text)) {
                return true;
            }
        }
        return false;
    }

    //学期选择对话框
    public void termChoice(View v) {
         LinearLayout term_layout = (LinearLayout) getLayoutInflater().inflate(R.layout.login_item, null);
        term_layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_term));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        radioGroup = (RadioGroup) term_layout.findViewById(R.id.radio_group);
        r1 = (RadioButton) term_layout.findViewById(R.id.radio_one);
        r2 = (RadioButton) term_layout.findViewById(R.id.radio_two);
        r3 = (RadioButton) term_layout.findViewById(R.id.radio_three);
        Button b1 = (Button) term_layout.findViewById(R.id.term_sure_button);
        Button b2 = (Button) term_layout.findViewById(R.id.term_cancel_button);
        builder.setView(term_layout);
        dialog = builder.create();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_one) {
                    termText = "2014-2015第一学期";
                    r1.setTextColor(Color.GREEN);
                    r3.setTextColor(Color.BLACK);
                    r2.setTextColor(Color.BLACK);
                } else if (checkedId == R.id.radio_two) {
                    termText = "2014-2015第二学期";
                    r2.setTextColor(Color.GREEN);
                    r1.setTextColor(Color.BLACK);
                    r3.setTextColor(Color.BLACK);
                } else if (checkedId == R.id.radio_three) {
                    termText = "2015-2016第二学期";
                    r3.setTextColor(Color.GREEN);
                    r1.setTextColor(Color.BLACK);
                    r2.setTextColor(Color.BLACK);
                }
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit0.setText(termText);
                dialog.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit0.setTextColor(Color.LTGRAY);
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(800, 700);
    }

    //初始化验证码并且刷新界面
    public void initCheckView()
    {
        checkNum = CheckUtil.getCheckNum();//获取到随机数产生的验证码
        checkView.setCheckNum(checkNum);//设置验证码
        checkView.invaliCheckNum();
    }
}

/*//重写类
class MySimpleAdapter extends SimpleAdapter {
    private LayoutInflater mInflater;

    public MySimpleAdapter(Context context, List<Map<String, String>> data,
                           int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = super.getView(position, convertView, parent);
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.term_item, parent, false);
        }
        TextView myTerm = (TextView) convertView.findViewById(R.id.term);
        myTerm.setTextColor(Color.BLACK);
        myTerm.setTextSize(20);
        return convertView;
    }
}*/
