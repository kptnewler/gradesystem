package comm.example.administrator.studentamagementsystem;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/5/16.
 */
//重写
class DbSimpleAdapter extends SimpleCursorAdapter {
    boolean a1 = false,a2 = false,a3 = false,a4 = false,a5 = false,a6 = false,a7 = false,a8 = false;
    boolean b1,b2,b3;//检测是否匹配正则表达式
    private  AlertDialog dialog;
    String stu_id,stu_c,stu_mo,stu_gao,stu_english,stu_ti,stu_name;
    private Button sure_button2,cancel_button2,revise_button;//确定，取消，修改按钮
    private EditText stu_id_edit2,stu_name_edit2,stu_c_edit2,stu_gao_edit2,stu_ti_edit2,stu_mo_edit2,stu_english_edit2;
    private static HashMap<Integer, Boolean> isSelected = new HashMap<Integer,Boolean>();//判断是不是选中
    private int mCount;//获取item数量
    private Context context1;
    private Cursor cursor;
    private  String first;
    private LayoutInflater mInflater,reviseInflater;
    private ArrayList<Integer> selection = new ArrayList<Integer>();//记录被选中条目id
    private String mIdColumn, mstuIdColumn,mstuNameColumn,mstuAvrColumn,mstuCColumn,
            mstuMoColumn,mstuGaoColumn,mstuEnglishColumn,mstuTiColumn;//数据库表列名称
    private SQLiteDatabase DbReader,DbWriter;
    ViewHolder holder;

    public DbSimpleAdapter(Context context, int layout, Cursor c, String[] from,
                           int[] to, int flags, String idColumn,String stuIdColumn,String stuNameColumn,
                           String stuAvrColumn,String stuCColumn,String stuMoColumn,String stuGaoColumn,
                           String stuEnglishColumn,String stuTiColumn, int count,
                           SQLiteDatabase db_writer,SQLiteDatabase db_reader) {
        super(context, layout, c, from, to, flags);
        context1 = context;
        mstuIdColumn = stuIdColumn;
        mstuNameColumn = stuNameColumn;
        mstuAvrColumn = stuAvrColumn;
        mstuCColumn = stuCColumn;
        mstuMoColumn = stuMoColumn;
        mstuGaoColumn = stuGaoColumn;
        mstuEnglishColumn = stuEnglishColumn;
        mstuTiColumn = stuTiColumn;
        mIdColumn = idColumn;
        mCount =count;
        reviseInflater = mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DbWriter = db_writer;
        DbReader = db_reader;
        init_isSelected();
    }

    //重写getView
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HashMap<Integer,View> mapView = new HashMap<Integer,View>();
        View v = super.getView(position, convertView, parent);
        holder = null;

        if ( mapView.get(position) == null) {
            v = mInflater.inflate(R.layout.db_list_layout, null);
            holder = new ViewHolder();
            holder.text_name = (TextView) v.findViewById(R.id.db_name_text);
            holder.text_id = (TextView) v.findViewById(R.id.db_stuId_text);
            holder.text_avr = (TextView) v.findViewById(R.id.db_avr_text);
            holder.checkBox = (CheckBox) v.findViewById(R.id.myCheck);
            holder.detailButton = (Button)v.findViewById(R.id.detail_button);
            cursor = getCursor();
            cursor.moveToPosition(position);
            holder.text_id.setText(cursor.getString(cursor.getColumnIndex(mstuIdColumn)));
            holder.text_name.setText(cursor.getString(cursor.getColumnIndex(mstuNameColumn)));
            holder.text_avr.setText(cursor.getString(cursor.getColumnIndex(mstuAvrColumn)));
            mapView.put(position, v);
            v.setTag(holder);
        }else{
            v=  mapView.get(position);
            holder = (ViewHolder)v.getTag();
            cursor = getCursor();
            cursor.moveToPosition(position);
            holder.text_id.setText(cursor.getString(cursor.getColumnIndex(mstuIdColumn)));
            holder.text_name.setText(cursor.getString(cursor.getColumnIndex(mstuNameColumn)));
            holder.text_avr.setText(cursor.getString(cursor.getColumnIndex(mstuAvrColumn)));
        }
        final CheckBox box = holder.checkBox;
        //设置字体大小
        holder.text_name.setTextSize(15);
        holder.text_id.setTextSize(15);
        holder.text_avr.setTextSize(15);
        //设置字体颜色
        holder.text_id.setTextColor(Color.BLACK);
        holder.text_name.setTextColor(Color.BLACK);
        holder.text_avr.setTextColor(Color.BLACK);

        //checkbox点击事件
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor = getCursor();//获取到游标
                cursor.moveToPosition(position);//移动点击出的地方
                if (getIsSelected().get(position)) {
                    getIsSelected().put(position, false);
                } else {
                    getIsSelected().put(position, true);
                }
                setIsSelected(getIsSelected());
                box.setChecked(getIsSelected().get(position));
                isAdd(cursor,position);
            }
        });

        //查看详情点击事件
        holder.detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor = getCursor();//获取到游标
                reviseDialog(cursor,position);
            }
        });
        box.setChecked(getIsSelected().get(position));
        return v;
    }

    //判读是否要把id加进去
    public void isAdd(Cursor cursor,int position)
    {
        if (getIsSelected().get(position)) {
            //如果选中就把id添加进去
            selection.add(cursor.getInt(new Integer(cursor.getColumnIndex(mIdColumn))));
        } else {
            //如果没有选中就id移除出去
            selection.remove(new Integer(cursor.getInt(cursor.getColumnIndex(mIdColumn))));
        }
    }

    //设置isSelected
    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        DbSimpleAdapter.isSelected = isSelected;
    }


    //初始化HashMap
    public void init_isSelected(){
        for(int i = 0; i < mCount;i++)
        {
            getIsSelected().put(i,false);
        }
    }

    //获取到HashMap
    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    //返回集合
    public ArrayList<Integer> getSelectedItems ()
    {
        checkAdd();
        return selection;
    }

    //全选
    public void checkAllItems()
    {
        for(int i = 0; i < mCount; i++ )
        {
            isSelected.put(i, true);
        }
        setIsSelected(isSelected);
    }

    //取消全选
    public void checkCancelItems()
    {
        for(int i = 0; i < mCount; i++)
        {
            isSelected.put(i, false);
        }
        setIsSelected(isSelected);
    }

    //反选
    public void checkReverseItems()
    {
        for(int i = 0; i < mCount; i++)
        {
            if(getIsSelected().get(i)) {
                isSelected.put(i, false);
            }
            else {
                isSelected.put(i, true);
            }
            setIsSelected(isSelected);
        }
    }

    //检测
    public void checkAdd()
    {
        for(int i = 0; i < mCount; i++)
        {
            Cursor cursor = getCursor();
            cursor.moveToPosition(i);
            isAdd(cursor, i);
        }
    }

    //修改信息对话框
    public void reviseDialog(final Cursor cursor5,int p)
    {
        LinearLayout reviseLayout = (LinearLayout)reviseInflater.inflate(R.layout.revise_dialog_layout,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context1);
        builder.setView(reviseLayout);
        dialog = builder.create();
        dialog.show();
        dialog.getWindow().setLayout(1000,1500);
        //获取按钮对象
        sure_button2 = (Button)reviseLayout.findViewById(R.id.sure_button2);
        cancel_button2 = (Button)reviseLayout.findViewById(R.id.cancel_button2);
        revise_button = (Button)reviseLayout.findViewById(R.id.revise_button);
        //获取EditText对象
        stu_id_edit2 = (EditText)reviseLayout.findViewById(R.id.stu_id_edit2);
        stu_name_edit2 = (EditText)reviseLayout.findViewById(R.id.stu_name_edit2);
        stu_c_edit2 = (EditText)reviseLayout.findViewById(R.id.stu_c_edit2);
        stu_ti_edit2 = (EditText)reviseLayout.findViewById(R.id.stu_ti_edit2);
        stu_mo_edit2 = (EditText)reviseLayout.findViewById(R.id.stu_mo_edit2);
        stu_english_edit2 = (EditText)reviseLayout.findViewById(R.id.stu_english_edit2);
        stu_gao_edit2 = (EditText)reviseLayout.findViewById(R.id.stu_gao_edit2);

        //获取id值，从而进行更新操作
        cursor5.moveToPosition(p);
        final int id = cursor5.getInt(cursor5.getColumnIndex(mIdColumn));

        //设置文本，从数据库中获取
        stu_id_edit2.setText(cursor.getString(cursor.getColumnIndex(mstuIdColumn)));
        stu_name_edit2.setText(cursor.getString(cursor.getColumnIndex(mstuNameColumn)));
        stu_c_edit2.setText(cursor.getString(cursor.getColumnIndex(mstuCColumn)));
        stu_mo_edit2.setText(cursor.getString(cursor.getColumnIndex(mstuMoColumn)));
        stu_gao_edit2.setText(cursor.getString(cursor.getColumnIndex(mstuGaoColumn)));
        stu_ti_edit2.setText(cursor.getString(cursor.getColumnIndex(mstuTiColumn)));
        stu_english_edit2.setText(cursor.getString(cursor.getColumnIndex(mstuEnglishColumn)));
        first = stu_id_edit2.getText().toString();

        //修改按钮点击之后可编辑
        revise_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stu_id_edit2.setEnabled(true);
                stu_name_edit2.setEnabled(true);
                stu_c_edit2.setEnabled(true);
                stu_ti_edit2.setEnabled(true);
                stu_mo_edit2.setEnabled(true);
                stu_gao_edit2.setEnabled(true);
                stu_english_edit2.setEnabled(true);
            }
        });

        //确定按钮点击事件
        sure_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stu_id = stu_id_edit2.getText().toString();
                stu_name = stu_name_edit2.getText().toString();
                stu_c = stu_c_edit2.getText().toString();
                stu_mo = stu_mo_edit2.getText().toString();
                stu_ti = stu_ti_edit2.getText().toString();
                stu_gao = stu_gao_edit2.getText().toString();
                stu_english = stu_english_edit2.getText().toString();
                a1 = is_accord_id(stu_id);
                a2 = is_Accord(stu_c);
                a3 = is_Accord(stu_mo);
                a4 = is_Accord(stu_gao);
                a5 = is_Accord(stu_english);
                a6 = is_Accord(stu_ti);
                a7 = stu_id_exist(stu_id,first);
                a8 = is_accord_name(stu_name);
                if ( a1 && a4 && a5 && a2 && a3 && a6 && a7 && a8) {
                    a1 = a2 = a3 = a4 = a5 = a6 = a7 = a8 = false;
                    ContentValues values = new ContentValues();
                    values.put("stu_id", stu_id);
                    values.put("stu_name",stu_name);
                    values.put("stu_c", stu_c);
                    values.put("stu_mo", stu_mo);
                    values.put("stu_gao", stu_gao);
                    values.put("stu_english", stu_english);
                    values.put("stu_ti", stu_ti);
                    values.put("stu_avr", average(stu_c, stu_mo, stu_gao, stu_english, stu_ti));
                    try {
                        DbWriter.update("class_one", values, "_id=?", new String[]{Integer.toString(id)});
                    }
                    catch (Exception e) {
                        Log.e("1", e.toString());
                    }
                    Toast.makeText(context1, "修改信息成功", Toast.LENGTH_SHORT).show();
                    try {
                        stu_id_edit2.setEnabled(false);
                        stu_name_edit2.setEnabled(false);
                        stu_c_edit2.setEnabled(false);
                        stu_ti_edit2.setEnabled(false);
                        stu_mo_edit2.setEnabled(false);
                        stu_gao_edit2.setEnabled(false);
                        stu_english_edit2.setEnabled(false);
                        dialog.dismiss();
                    }
                    catch (Exception e)
                    {
                        Log.i("exception", e.toString());
                    }
                } else if(!a1){
                    Toast.makeText(context1, "请输入正确的学号", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context1,"该学号已经存在,请您重新输入",Toast.LENGTH_SHORT).show();
                }else if(!a8){
                    Toast.makeText(context1,"请输入中文姓名",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //取消按钮
        cancel_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void warn_information(String text)
    {
        Toast.makeText(context1, "请输入正确的"+text+"分数", Toast.LENGTH_SHORT).show();
    }

    //返回数据库
    public SQLiteDatabase getDbWriter()
    {
        return DbWriter;
    }

    // 计算平均分
    public Double average(String c,String mo,String gao,String english, String ti)
    {
        int int_c = Integer.parseInt(c);
        int int_mo = Integer.parseInt(mo);
        int int_gao = Integer.parseInt(gao);
        int int_english = Integer.parseInt(english);
        int int_ti = Integer.parseInt(ti);
        double avr = (double)(int_c + int_mo + int_gao + int_english + int_ti)/5;
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
        return avr;
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

    //判读学号是否符合标准
    public boolean is_accord_id(String stu_id)
    {
        b1 = stu_id.matches("31411012[0-5][0-9]");
        b2 = stu_id.matches("3141101260");
        b3 = stu_id.matches("3141101261");
        if(b1 || b2 || b3)
        {
            return true;
        }
        else {
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

    //判断学号是否存在
    public boolean stu_id_exist(String stu_id,String first)
    {
        Cursor cursor1 = DbReader.rawQuery("select * from class_one",null);
        if(first.equals(stu_id))
            return true;
        else {
            for (int k = 0; k < cursor1.getCount(); k++) {
                cursor1.moveToPosition(k);
                String exist_text = cursor1.getString(cursor1.getColumnIndex("stu_id"));
                if (stu_id.equals(exist_text)) {
                    return false;
                }
            }
        }
        return true;
    }
    //holder类
    class   ViewHolder{
        public TextView text_name, text_id, text_avr;//姓名和学号,平均分的TextView
        public CheckBox checkBox;//勾选是否删除
        public Button detailButton;//查看详情按钮
    }
}
