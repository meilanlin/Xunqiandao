package cn.lizonglin.meilan.xunqiandao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Created by Lizl on 2017/5/12.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "checkIn_list"; // 数据库的名字
    private Context mcontext;
    private DBHelper mDbHelper;
    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 11);
        this.mcontext = context;
    }

    public DBHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
    }

     //第一次使用软件时调用的操作，用于获取数据库创建语句（SW）,然后创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        //,clothes text,doctor text,laiwang text,baby text,live text,other text,remark text
        String sql = "create table if not exists student_list(id integer primary key,time text,stuid text,classtime text,major text,status text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // 打开数据库,如果已经打开就使用，否则创建
    public DBHelper open() {
        if (null == mDbHelper) {
            mDbHelper = new DBHelper(mcontext);
        }
        db = mDbHelper.getWritableDatabase();
        return this;
    }

    // 关闭数据库
    public void close() {
        db.close();
        mDbHelper.close();
    }

    //删除数据，未使用到
    public void delete(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete("person", "personid<?", new String[]{"677"});
        db.close();
    }

    //添加数据
    public long insert(String tableName, ContentValues values) {
        return db.insert(tableName, null, values);
    }

    //查询数据
    public Cursor findList(String tableName, String[] columns,
                           String selection, String[] selectionArgs, String groupBy,
                           String having, String orderBy, String limit) {
        return db.query(tableName, columns, selection, selectionArgs, groupBy,
                having, orderBy, limit);
    }

    public Cursor exeSql(String sql) {
        return db.rawQuery(sql, null);
    }
}