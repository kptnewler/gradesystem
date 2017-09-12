package comm.example.administrator.studentamagementsystem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by Administrator on 2016/5/14.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {    final String CREATE_TABLE_SQL_CLASS_ONE =
        "create table class_one(_id integer primary key autoincrement, "+
                "stu_id text not null , stu_name text not null,stu_c  ,stu_mo ,stu_gao ,stu_english ,stu_ti ,stu_avr decimal(6,3))";

    final String CREATE_TABLE_SQL_STUDENT =
            "create table student(_id integer primary key autoincrement, "+
                    "stu_num text not null , stu_password text not null)";

    final String CREATE_TABLE_SQL_TEACHER =
            "create table teacher(_id integer primary key autoincrement, "+
                    "tea_num text not null , tea_password text not null)";

    // private static  final int VERSION = 1;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        //必须通过super调用父类当中的构造函数
        super(context, name, factory, version);
    }

    public MyDatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

/*    public MyDatabaseHelper(Context context,String name){
        super(context,name,null,VERSION);
    }*/


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL_CLASS_ONE);
        db.execSQL(CREATE_TABLE_SQL_STUDENT);
        db.execSQL(CREATE_TABLE_SQL_TEACHER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists class_one");
        db.execSQL("drop table if exists student");
        db.execSQL("drop table if exists teacher");
        db.execSQL(CREATE_TABLE_SQL_CLASS_ONE);
        db.execSQL(CREATE_TABLE_SQL_STUDENT);
        db.execSQL(CREATE_TABLE_SQL_TEACHER);
        System.out.println("更新版本");
    }

}
