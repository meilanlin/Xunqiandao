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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static cn.lizonglin.meilan.xunqiandao.R.id.class_time;
import static cn.lizonglin.meilan.xunqiandao.R.id.major_class;

/**
 * Created by Lizl on 2017/5/13.
 */

@SuppressLint("SimpleDateFormat")
public class CheckActivityTeacher extends Activity implements View.OnClickListener {
    private EditText studentIDEdt;
    private EditText classTimeEdt;
    private EditText majorEdt;
    private EditText checkEdt;
    private Spinner spinner = null;
    private Button export_list, import_list,clean,manual;
    private File file;
    private String[] title = {"日期", "学号", "上课时间", "班级", "状态"};
    private String[] saveData;
    private DBHelper mDbHelper;
    private ArrayList<ArrayList<String>> stu2List;
    private ListView checkIn_listview;
    private String Major ="";
    private String check ="";
    private String ClassTime,StuFrom,TotleNumber  = "";
    private RadioGroup rg1 = null;
    private RadioButton rbCheck = null;
    private RadioButton rbLeave = null;
    private TextView stuCount,stuNoneCount;

    //Toast数据
    private void display(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_teacher);
        findViewsById();
        mDbHelper = new DBHelper(this);
        mDbHelper.open();
        stu2List = new ArrayList<ArrayList<String>>();
    }

    private void findViewsById() {
        studentIDEdt     = (EditText) findViewById(R.id.studentID);        //学号
        classTimeEdt     = (EditText) findViewById(class_time);            //上课时间
        majorEdt         = (EditText) findViewById(R.id.major_class);      //专业与班级
        checkEdt         = (EditText) findViewById(R.id.check_leave);      //状态，签到还是请假
        checkIn_listview = (ListView) findViewById(R.id.checkIn_listview); //签到列表
        export_list      = (Button) findViewById(R.id.button_checkIn);     //签到按钮，并将数据存入.xls文件中
        import_list      = (Button) findViewById(R.id.button_checkList);   //手机端查看签到表，实际上是将.xls文件导入，导入后再次签到会导致数据重复
        clean            = (Button) findViewById(R.id.button_clean);       //清除数据
        manual           = (Button) findViewById(R.id.button_manual);      //清除数据
        stuCount         =(TextView) findViewById(R.id.stu_count);         //签到人数
        stuNoneCount         =(TextView) findViewById(R.id.stu_none_count);//未签到人数
        //从LoginActivity拿到上课时间
        Intent b = getIntent();
        ClassTime= b.getStringExtra("Class_time");
        StuFrom = b.getStringExtra("Student_from");
        TotleNumber = b.getStringExtra("Totle_stuNumber");
        export_list.setOnClickListener(this);
        import_list.setOnClickListener(this);
        clean.setOnClickListener(this);
        manual.setOnClickListener(this);
        majorEdt.setText(StuFrom);
        View contentHeader = LayoutInflater.from(this).inflate(
                R.layout.listview_header, null);
        checkIn_listview.addHeaderView(contentHeader);
        //签到与请假单选效果
        rg1 = (RadioGroup)findViewById(R.id.radioGroup);
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
                } else {
                    Toast.makeText(this, "请填写任意一项内容", Toast.LENGTH_SHORT).show();
                }
                break;
            //点击检视按钮，将读取已存入的list.xls文件
            case R.id.button_checkList:
                ArrayList<CheckObject> stuList = (ArrayList<CheckObject>) Excel
                        .read2DB(new File(getSDPath() + "/Course/list.xls"), this);
                checkIn_listview.setAdapter(new CheckAdapter(this, stuList));
                //通过统计listView中item的数量而得到签到的人数
                int count = checkIn_listview.getCount();
                count = count - 1;
                String Count = Integer.toString(count);
                Count = "·" + Count +"·";
                stuCount.setText(Count);
                //此时拿到的TotleNumber是字符串，需要转换成整型进行计算
                int TotleNumberInt = Integer.parseInt(TotleNumber);
                Integer noneCount = TotleNumberInt - count;
                String NoneCount = Integer.toString(noneCount);
                NoneCount = "·" + NoneCount + "·";
                stuNoneCount.setText(NoneCount);
                break;
            //点击清除按钮，将清除list.xls文件
            case R.id.button_clean:
                this.deleteDatabase("checkIn_list");
                Toast.makeText(this, "将在下一个签到前清除", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_manual:
                Toast.makeText(this, "请教师确认后进行签到", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(CheckActivityTeacher.this, CheckActivity.class);
                //String class_time = ClassTime;
                intent3.putExtra("class_time", ClassTime);
                intent3.putExtra("student_from", StuFrom);
                startActivity(intent3);
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
