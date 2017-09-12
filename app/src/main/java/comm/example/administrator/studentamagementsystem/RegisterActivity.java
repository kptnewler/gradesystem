package comm.example.administrator.studentamagementsystem;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;



public class RegisterActivity extends AppCompatActivity {
    //注册信息
    private EditText register_id_edit,register_password_one_edit,getRegister_password_two_edit;
    //确定，返回按钮
    private Button register_sure_button,register_cancel_button;
    private MyDatabaseHelper dbHelper = new MyDatabaseHelper(this,"class_one.db3",20);//建立数据库
    private SQLiteDatabase db_write;//获取一个可写的数据库
    private SQLiteDatabase db_read;//获取一个可读的数据库
    private String register_id,register_password_one,register_password_two,check_code;//保存输入的文本
    private boolean b1 = false,b2 = false,b3 = false;//判断是否匹配正则表达式
    private boolean a1 = false,a2 = false,a3 = true,a4 = false,a6 = false,a7 = false;//判断是否注册成功
    private String  phoneNum;//手机号码
    private Button checkButton;//验证
    private Button changeCodeButton;//刷新验证码按钮
    private EditText checkEditText;//输入验证码
    private EditText phoneEditText;//手机号码
    private int i = 60;
  //  private int [] checkNum =null;
    private Cursor cursor;

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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        //actionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        //设置是否显示图标
        actionBar.setDisplayHomeAsUpEnabled(true);
        //设置左箭头
        actionBar.setDisplayHomeAsUpEnabled(true);

        //获取数据库
        db_read = dbHelper.getReadableDatabase();//获取一个可读的数据库
        db_write =dbHelper.getWritableDatabase();//获取一个可写的数据库
        Cursor cursor = db_read.rawQuery("select * from student",null);

        //获取EditText对象
        register_id_edit = (EditText)findViewById(R.id.register_id_edit);//学号
        register_password_one_edit = (EditText)findViewById(R.id.register_password_one_edit);//密码
        getRegister_password_two_edit =(EditText)findViewById(R.id.register_password_two_edit);//确认密码

        //获取Button对象
        register_sure_button = (Button)findViewById(R.id.register_sure_button);
        register_cancel_button =(Button)findViewById(R.id.register_cancel_button);

        initCheckCode();
        //确定按钮点击事件
        register_sure_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存输入的文本
                //for(int i = 0; i < 1000000000;i++);
                register_id = register_id_edit.getText().toString();
                register_password_one = register_password_one_edit.getText().toString();
                register_password_two = getRegister_password_two_edit.getText().toString();
                check_code = checkEditText.getText().toString();
                if (register_id == null) {
                    Toast.makeText(RegisterActivity.this, "学号不能为空", Toast.LENGTH_SHORT).show();
                } else if (register_password_one == null)
                    Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                else if (register_password_two == null)
                    Toast.makeText(RegisterActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
                if (stu_id_exist(register_id))//学号是否存在
                    a1 = true;
                if (register_password_one.equals(register_password_two))//两次密码输入是否一致
                    a2 = true;
                if (is_accord_id(register_id))//学号是否符合标准
                    a3 = true;
                if (is_accord_password(register_password_one))//密码是否符合标准
                    a4 = true;
                //判断老师是否填入了这个学生的信息
                if (teacher_stu_id_exist(register_id)) {
                    a7 = true;
                }
                if (a1 && a2 && a4 && a3 && a7 && a6) {
                    ContentValues values = new ContentValues();
                    values.put("stu_num", register_id);
                    values.put("stu_password", register_password_one);
                    db_write.insert("student", null, values);
                    Toast.makeText(RegisterActivity.this, "恭喜您,账号注册成功", Toast.LENGTH_SHORT).show();
                    a1 = a2 = a4 = a3 = a7 =a6=  false;
                    finish();
                } else if (!a1)
                    Toast.makeText(RegisterActivity.this, "该学号已经被注册过，请您重新输入", Toast.LENGTH_SHORT).show();
                else if (!a2)
                    Toast.makeText(RegisterActivity.this, "您输入的两次密码不一致,请您重新输入", Toast.LENGTH_SHORT).show();
                else if (!a3)
                    Toast.makeText(RegisterActivity.this, "请您输入正确的学号,请您重新输入", Toast.LENGTH_SHORT).show();
                else if (!a4)
                    Toast.makeText(RegisterActivity.this, "为了您账号的安全,请您设置6-20位数字+字母的密码", Toast.LENGTH_SHORT).show();
                else if (!a7) {
                    Toast.makeText(RegisterActivity.this, "您注册的学号还未进入存档,请让老师存入后再注册", Toast.LENGTH_SHORT).show();
                } else if (!a6) {
                    Toast.makeText(RegisterActivity.this, "对不起,您的验证码输入错误,请您重新输入", Toast.LENGTH_SHORT).show();
                }
                a1 = a2 = a4 = a3 = a7 = a6 = false;
            }
        });

        //返回按钮触发事件
        register_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        //发送验证码按钮单击事件
        changeCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              phoneNum = phoneEditText.getText().toString();
                if(!judgePhoneNum(phoneNum))
                    return;
                //通过SDK发送短信验证
                //初始化短信SDK
             //   cn.smssdk.SMSSDK.initSDK(RegisterActivity.this, "1389a9b1329e6", "51b29e7e5e751cd45ed75b140a527486");
                cn.smssdk.SMSSDK.getVerificationCode("86",phoneNum);

                //把按钮变成不可点击,然后开始倒计时
                changeCodeButton.setClickable(false);
                changeCodeButton.setText("重新发送(" + i + ")");
                //重新开一个线程倒计时
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(;i > 0; i--){
                            handler.sendEmptyMessage(0x2);
                            if(i <= 0)
                                break;;
                            try {
                                Thread.sleep(1000);
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(0x1);
                    }
                }).start();
            }
        });

        //验证按钮
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交
                SMSSDK.submitVerificationCode("86",phoneNum,checkEditText.getText().toString());
            }
        });
    }

    //初始化验证码
    private void initCheckCode() {
        checkEditText = (EditText) findViewById(R.id.checkEditText);
        changeCodeButton = (Button) findViewById(R.id.changeCodeButton);
        phoneEditText = (EditText) findViewById(R.id.register_phone_edit);
        checkButton = (Button)findViewById(R.id.checkButton);
        //初始化短信SDK
        cn.smssdk.SMSSDK.initSDK(this, "1389a9b1329e6", "51b29e7e5e751cd45ed75b140a527486");
        final EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message message = new Message();
                message.arg1 = event;
                message.arg2 = result;
                message.obj = data;
                Log.e("fsaf","回调"+event+"----"+result);
                handler.sendMessage(message);
            }
        };
        //注册回调接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    //消息处理
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x2)
            {
                changeCodeButton.setText("重新发送("+ i +")");
            }else if(msg.what == 0x1){
                changeCodeButton.setText("获取验证码");
                changeCodeButton.setClickable(true);
                i = 60;
            }else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if(result == SMSSDK.RESULT_COMPLETE){
                    //短信注册成功
                    if(event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                        a6 = true;
                        Toast.makeText(RegisterActivity.this,"验证码验证成功",Toast.LENGTH_SHORT).show();
                    }else if(event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        Toast.makeText(RegisterActivity.this,"验证码成功发送",Toast.LENGTH_SHORT).show();
                    }else {
                        a6 = false;
                        Toast.makeText(RegisterActivity.this,"验证码验证失败",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    //判断手机号码是否符合要求
    public boolean judgePhoneNum(String phoneNum)
    {
        if(phoneNum.length() == 11 && is_accord_phone(phoneNum))
            return true;
        else
        {
            Toast.makeText(RegisterActivity.this,"您输入的手机号码有误,请重新输入",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    //判读手机号码格式是否正确
    public boolean is_accord_phone(String phoneNum)
    {
        String telRegex = "[1][358]\\d{9}";
        if(TextUtils.isEmpty(phoneNum))
            return  false;
        else
            return phoneNum.matches(telRegex);
    }

    //判断学号是否存在
    public boolean stu_id_exist(String stu_id)
    {
        Cursor cursor2 = db_read.rawQuery("select * from student",null);
        for(int k = 0; k < cursor2.getCount(); k++ )
        {
            cursor2.moveToPosition(k);
            String exist_text = cursor2.getString(cursor2.getColumnIndex("stu_num"));
            if(stu_id.equals(exist_text))
            {
                return  false;
            }
        }
        return true;
    }

    //判读学号是否符合标准
    public boolean is_accord_id(String stu_id)
    {
        return true;
    }

    //判断密码是不是数字加字母
    public boolean is_accord_password(String register_password)
    {
        if(register_password.matches("[0-9a-zA-Z]{4,16}"))
        {
            return true;
        }
        else
            return false;
    }

    //判断学号是否被老师加入
    public boolean teacher_stu_id_exist(String stu_id)
    {
        Cursor cursor2 = db_read.rawQuery("select * from class_one",null);
        for(int k = 0; k < cursor2.getCount(); k++ )
        {
            cursor2.moveToPosition(k);
            String exist_text = cursor2.getString(cursor2.getColumnIndex("stu_id"));
            if(stu_id.equals(exist_text))
            {
                return  true;
            }
        }
        return false;
    }

    //初始化验证码并且刷新界面
  /*  public void initCheckView()
    {
        checkNum = CheckUtil.getCheckNum();//获取到随机数产生的验证码
        checkView.setCheckNum(checkNum);//设置验证码
        checkView.invaliCheckNum();
    }
*/
}
