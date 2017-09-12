package comm.example.administrator.studentamagementsystem;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ModifyPasswordActivity extends AppCompatActivity {
    private EditText ModifyIdEdit,ModifyOriginalEdit,ModifyNewOneEdit,ModifyNewTwoEdit;
    private Button ModifySureButton,ModifyCancelButton;
    private MyDatabaseHelper dbHelper = new MyDatabaseHelper(this,"class_one.db3",20);//建立数据库
    private SQLiteDatabase db_write;//获取一个可写的数据库
    private SQLiteDatabase db_read;//获取一个可读的数据库
    private String stu_id,stu_original_password,stu_new_one_password,stu_new_two_password;
    private boolean a1 = false,a2 = false,a3 = false,a4 = false,a5 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_password_layout);
        initView();

        //确定按钮
        ModifySureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getText();
                a1 = a2 = a3 = a4 = a5 = false;
                a1 = stu_id_exist(stu_id);
                a2 = stu_password_exist(stu_id,stu_original_password);
                a3 = is_accord_password(stu_new_one_password);
                a4 = is_password_same(stu_new_one_password,stu_new_two_password);
                if(a1 && a2 && a3 && a4){
                    ContentValues values = new ContentValues();
                    values.put("stu_password", stu_new_one_password);
                    db_read.update("student", values, "stu_num = ?", new String[]{stu_id});
                    Toast.makeText(ModifyPasswordActivity.this,"修改密码成功",Toast.LENGTH_SHORT).show();
                    finish();
                }else if(!a1){
                    Toast.makeText(ModifyPasswordActivity.this,"您输入的学号不存在,请确认后输入",Toast.LENGTH_SHORT).show();
                }else if(!a2){
                    Toast.makeText(ModifyPasswordActivity.this,"您输入的密码错误,请确认后输入",Toast.LENGTH_SHORT).show();
                }else if(!a3){
                    Toast.makeText(ModifyPasswordActivity.this,"为了您账号的安全,请您设置6-20位数字+字母的密码",Toast.LENGTH_SHORT).show();
                }else if(!a4){
                    Toast.makeText(ModifyPasswordActivity.this,"您输入的密码和第一次不一致,请您确认后输入",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //取消按钮
        ModifyCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //初始化控件
    private void initView() {
        ModifyIdEdit = (EditText)findViewById(R.id.modify_id_edit);
        ModifyOriginalEdit = (EditText)findViewById(R.id.modify_password_original_edit);
        ModifyNewOneEdit = (EditText)findViewById(R.id.modify_password_new_one_edit);
        ModifyNewTwoEdit = (EditText)findViewById(R.id.modify_password_new_two_edit);
        ModifySureButton = (Button)findViewById(R.id.modify_sure_button);
        ModifyCancelButton = (Button)findViewById(R.id.modify_cancel_button);
        db_read = dbHelper.getReadableDatabase();
        db_write = dbHelper.getWritableDatabase();
    }

    //获取文本
    private void getText(){
        stu_id = ModifyIdEdit.getText().toString();
        stu_original_password = ModifyOriginalEdit.getText().toString();
        stu_new_one_password = ModifyNewOneEdit.getText().toString();
        stu_new_two_password = ModifyNewTwoEdit.getText().toString();
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
                return  true;
            }
        }
        return false;
    }

    //判断密码是否存在
    public boolean stu_password_exist(String stu_id,String stu_password)
    {
        Cursor cursor = db_read.rawQuery("select * from student",null);
        for(int k = 0; k < cursor.getCount();k++)
        {
            cursor.moveToPosition(k);
            String exist_text = cursor.getString(cursor.getColumnIndex("stu_num"));

            if(stu_id.equals(exist_text)) {
                break;
            }
        }
        if(cursor.getString(cursor.getColumnIndex("stu_password")).equals(stu_password))
            return true;
        else
            return false;
    }

    //判断密码是不是数字加字母
    public boolean is_accord_password(String register_password)
    {
        if(register_password.matches("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$"))
        {
            return true;
        }
        else
            return false;
    }

    //判断两次密码是否一致
    public boolean is_password_same(String p1,String p2){
        if(p1.equals(p2))
            return true;
        else
            return false;
    }
}
