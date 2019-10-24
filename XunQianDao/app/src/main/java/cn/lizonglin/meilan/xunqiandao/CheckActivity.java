package cn.lizonglin.meilan.xunqiandao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static cn.lizonglin.meilan.xunqiandao.IsvActivity.isvflag;
import static cn.lizonglin.meilan.xunqiandao.R.id.class_time;

/**
 * Created by Lizl on 2017/5/12.
 */

@SuppressLint("SimpleDateFormat")
public class CheckActivity extends Activity implements View.OnClickListener {
    private EditText studentIDEdt;
    private EditText classTimeEdt;
    private EditText majorEdt;
    private EditText checkEdt;
    private Spinner spinner = null;
    private Button export_list, import_list,clean;
    private File file;
    private String[] title = {"日期", "学号", "上课时间", "班级", "状态"};
    private String[] saveData;
    private DBHelper mDbHelper;
    private ArrayList<ArrayList<String>> stu2List;
    private ListView checkIn_listview;
    private String Major ="";
    private String check ="";
    private String ClassTime="";
    private RadioGroup rg1 = null;
    private RadioButton rbCheck = null;
    private RadioButton rbLeave = null;

    //Toast数据
    private void display(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        findViewsById();
        mDbHelper = new DBHelper(this);
        mDbHelper.open();
        stu2List = new ArrayList<ArrayList<String>>();
    }

    private void findViewsById() {
        studentIDEdt     = (EditText) findViewById(R.id.studentID);         //学号
        classTimeEdt     = (EditText) findViewById(class_time);             //上课时间
        majorEdt         = (EditText) findViewById(R.id.major_class);       //专业与班级
        checkEdt         = (EditText) findViewById(R.id.check_leave);       //状态，签到还是请假
        checkIn_listview = (ListView) findViewById(R.id.checkIn_listview);  //签到列表
        export_list      = (Button) findViewById(R.id.button_checkIn);      //签到按钮，并将数据存入.xls文件中
        import_list      = (Button) findViewById(R.id.button_checkList);    //手机端查看签到表，实际上是将.xls文件导入，导入后再次签到会导致数据重复
        clean            = (Button) findViewById(R.id.button_clean);        //清除数据
        export_list.setOnClickListener(this);
        import_list.setOnClickListener(this);
        clean.setOnClickListener(this);
        //从LoginActivity拿到学号，上课时间，学院专业班级
        Intent b = getIntent();
        String StuID = b.getStringExtra("mAuthId");
        ClassTime = b.getStringExtra("class_time");
        String StuFrom = b.getStringExtra("student_from");
        studentIDEdt.setText(StuID);
        classTimeEdt.setText(ClassTime);
        majorEdt.setText(StuFrom);

        View contentHeader = LayoutInflater.from(this).inflate(
                R.layout.listview_header, null);
        checkIn_listview.addHeaderView(contentHeader);
        //签到与请假单选效果
        rg1     = (RadioGroup)findViewById(R.id.radioGroup);
        rbCheck = (RadioButton)findViewById(R.id.button_check);
        rbLeave = (RadioButton)findViewById(R.id.button_leave);
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(rbCheck.getId()==checkedId) {
                    display("签到");
                    check = "签到";
                }
                else if(rbLeave.getId()==checkedId) {
                    display("请假");
                    check = "请假";
                }
                checkEdt.setText(check);
            }
        });
    }

    //===============================签到-检视-清除按钮==================================
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击签到按钮，将数据存入各个位置，第一列为时间
            case R.id.button_checkIn:
                saveData = new String[] {
                        new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                        studentIDEdt.getText().toString().trim(),
                        classTimeEdt.getText().toString().trim(),
                        majorEdt.getText().toString().trim(),
                        checkEdt.getText().toString().trim() };
                if (canSave(saveData)) {
                        ContentValues values = new ContentValues();
                        values.put("time",
                                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        values.put("stuid", studentIDEdt.getText().toString());
                        values.put("classtime", classTimeEdt.getText().toString());
                        values.put("major", majorEdt.getText().toString());
                        values.put("status", checkEdt.getText().toString());
                        long insert = mDbHelper.insert("student_list", values);
                        if (insert > 0) {
                            initData();
                        }
                    //06.02
                    //Intent intent = new Intent(CheckActivity.this, LoginActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//返回旧的activity，而不是在创建一个新的
                    //startActivity(intent);
                    finish();
                    isvflag.finish();
                } else {
                    Toast.makeText(this, "请填写任意一项内容", Toast.LENGTH_SHORT).show();
                }
                break;
            //点击检视按钮，将读取已存入的list.xls文件
            case R.id.button_checkList:
                ArrayList<CheckObject> stuList = (ArrayList<CheckObject>) Excel
                        .read2DB(new File(getSDPath() + "/Course/list.xls"), this);
                checkIn_listview.setAdapter(new CheckAdapter(this, stuList));
                break;
            //点击清除按钮，将清除list.xls文件
            case R.id.button_clean:
                this.deleteDatabase("checkIn_list");
                Toast.makeText(this, "已清除数据库", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void initData() {
        file = new File(getSDPath() + "/Course");
        makeDir(file);
        Excel.initExcel(file.toString() + "/list.xls", title);
        Excel.writeObjListToExcel(getListData(), getSDPath()
                + "/Course/list.xls", this);
    }

    private ArrayList<ArrayList<String>> getListData() {
        Cursor mCrusor = mDbHelper.exeSql("select * from student_list");
        while (mCrusor.moveToNext()) {
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(mCrusor.getString(1));
            beanList.add(mCrusor.getString(2));
            beanList.add(mCrusor.getString(3));
            beanList.add(mCrusor.getString(4));
            beanList.add(mCrusor.getString(5));
            stu2List.add(beanList);
        }
        mCrusor.close();
        return stu2List;
    }

    public static void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;
    }

    private boolean canSave(String[] data) {
        boolean isOk = false;
        for (int i = 0; i < data.length; i++) {
            if (i > 0 && i < data.length) {
                if (!TextUtils.isEmpty(data[i])) {
                    isOk = true;
                }
            }
        }
        return isOk;
    }
}
