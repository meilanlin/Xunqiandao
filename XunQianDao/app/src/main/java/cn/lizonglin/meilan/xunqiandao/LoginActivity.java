package cn.lizonglin.meilan.xunqiandao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import view.ClearEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Lizl on 2017/5/8.
 */

public class LoginActivity extends Activity implements View.OnClickListener {
    private Toast mToast;
    private Spinner spinner = null;
    private Spinner spinClass = null;
    private Spinner spinCollege, spinMajor;
    private String ClassTime, stuCollege, stuMajor, stuClass = "";
    private ClearEditText uname,total_stu_number;

    private void display(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_confirm).setOnClickListener(LoginActivity.this);
        findViewById(R.id.btn_confirm2).setOnClickListener(LoginActivity.this);
        spinCollege = (Spinner) findViewById(R.id.spin_colloge);
        spinMajor = (Spinner) findViewById(R.id.spin_major);

        mToast = Toast.makeText(LoginActivity.this, "", Toast.LENGTH_SHORT);
        //=============================== 一级联动spinner ==================================
        String[] classTime = {"第1/2节课", "第3/4节课", "第5/6节课", "第7/8节课", "第9/10节课"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classTime);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.spin_classtime);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                display(((TextView) arg1).getText().toString());
                ClassTime = ((TextView) arg1).getText().toString();
                //设置spinner内文字居中
                //其实我可以覆写SDK中对应API文件layout下的spinner.xml的TextView
                //考虑到在其他机器上的编辑，因此放弃了直接覆写，而选择以下四行代码
                //覆写路径为：sdk\platforms\android-21\data\res\layout
                // 找到simple_spinner_item.xml或simple_spinner_dropdown_item.xml
                TextView tv = (TextView)arg1;
                tv.setTextColor(getResources().getColor(android.R.color.black));    //设置颜色
                tv.setTextSize(18);                                                 //设置大小
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);              //设置居中
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        //=============================== 一级联动spinner ==================================
        String[] stuClasses = {"1班", "2班", "3班", "4班", "5班","6班"};
        ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stuClasses);
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinClass = (Spinner) findViewById(R.id.spin_class);
        spinClass.setAdapter(spinnerAdapter2);
        spinClass.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                display(((TextView) arg1).getText().toString());
                stuClass = ((TextView) arg1).getText().toString();
                //设置spinner内文字居中
                TextView tv = (TextView)arg1;
                tv.setTextColor(getResources().getColor(android.R.color.black));    //设置颜色
                tv.setTextSize(18);                                                 //设置大小
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);              //设置居中
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        //===============================二级联动spinner==================================
        // 设置数据源
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.college,
                android.R.layout.simple_spinner_item);
        // 设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 绑定数据源到spinner控件上
        spinCollege.setAdapter(adapter);
        //spinner选项选择事件
        spinCollege.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stuCollege = (String) spinCollege.getItemAtPosition(position);
                //此处的parent是指的spinCollege的下拉框
                Spinner spinner = (Spinner) parent;
                //通过getItemAtPosition(position)的方法来找到学院的名称
                String college = (String) spinner.getItemAtPosition(position);
                //设置专业的数据适配器
                ArrayAdapter<CharSequence> majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.st,
                        android.R.layout.simple_spinner_item);
                //判断，然后设置适配器
                if (college.equals("数学与统计学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.st, android.R.layout.simple_spinner_item);
                } else if (college.equals("化学与分子工程学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.hg, android.R.layout.simple_spinner_item);
                } else if (college.equals("物理工程学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.wg, android.R.layout.simple_spinner_item);
                }else if (college.equals("信息工程学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.xg, android.R.layout.simple_spinner_item);
                } else if (college.equals("电气工程学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.dg, android.R.layout.simple_spinner_item);
                } else if (college.equals("材料科学与工程学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.cg, android.R.layout.simple_spinner_item);
                } else if (college.equals("机械工程学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.jg, android.R.layout.simple_spinner_item);
                }else if (college.equals("土木工程学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.tg, android.R.layout.simple_spinner_item);
                }else if (college.equals("水利与环境学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.sh, android.R.layout.simple_spinner_item);
                }else if (college.equals("化工与能源学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.hn, android.R.layout.simple_spinner_item);
                }else if (college.equals("建筑学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.jz, android.R.layout.simple_spinner_item);
                }else if (college.equals("管理工程学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.gg, android.R.layout.simple_spinner_item);
                }else if (college.equals("力学与工程科学学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.lg, android.R.layout.simple_spinner_item);
                }else if (college.equals("生命科学学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.sk, android.R.layout.simple_spinner_item);
                }else if (college.equals("商学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.shang, android.R.layout.simple_spinner_item);
                }else if (college.equals("旅游管理学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.lvG, android.R.layout.simple_spinner_item);
                }else if (college.equals("公共管理学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.gongG, android.R.layout.simple_spinner_item);
                }else if (college.equals("法学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.fa, android.R.layout.simple_spinner_item);
                }else if (college.equals("文学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.wen, android.R.layout.simple_spinner_item);
                }else if (college.equals("新闻与传播学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.xc, android.R.layout.simple_spinner_item);
                }else if (college.equals("外语学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.wy, android.R.layout.simple_spinner_item);
                }else if (college.equals("马克思主义学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.mks, android.R.layout.simple_spinner_item);
                }else if (college.equals("教育学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.jy, android.R.layout.simple_spinner_item);
                }else if (college.equals("历史学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.ls, android.R.layout.simple_spinner_item);
                }else if (college.equals("信息管理学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.xinG, android.R.layout.simple_spinner_item);
                }else if (college.equals("体育学院（校本部）")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.ty, android.R.layout.simple_spinner_item);
                }else if (college.equals("音乐学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.yy, android.R.layout.simple_spinner_item);
                }else if (college.equals("美术学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.ms, android.R.layout.simple_spinner_item);
                }else if (college.equals("书法学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.sf, android.R.layout.simple_spinner_item);
                }else if (college.equals("医学科学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.yk, android.R.layout.simple_spinner_item);
                }else if (college.equals("基础医学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.jiY, android.R.layout.simple_spinner_item);
                }else if (college.equals("临床医学系")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.lc, android.R.layout.simple_spinner_item);
                }else if (college.equals("医学检验系")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.yj, android.R.layout.simple_spinner_item);
                }else if (college.equals("医学实验中心")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.ySCenter, android.R.layout.simple_spinner_item);
                }else if (college.equals("实验动物中心")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.shiDWCenter, android.R.layout.simple_spinner_item);
                }else if (college.equals("河南省医药科学研究院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.sResearch, android.R.layout.simple_spinner_item);
                }else if (college.equals("公共卫生学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.gw, android.R.layout.simple_spinner_item);
                }else if (college.equals("护理学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.hl, android.R.layout.simple_spinner_item);
                }else if (college.equals("药物研究院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.yWResearch, android.R.layout.simple_spinner_item);
                }else if (college.equals("药学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.yao, android.R.layout.simple_spinner_item);
                }else if (college.equals("第一临床学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.oneLC, android.R.layout.simple_spinner_item);
                }else if (college.equals("第二临床学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.twoLC, android.R.layout.simple_spinner_item);
                }else if (college.equals("第三临床学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.threeLC, android.R.layout.simple_spinner_item);
                }else if (college.equals("口腔医学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.kq, android.R.layout.simple_spinner_item);
                }else if (college.equals("第五临床学院")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.fiveLC, android.R.layout.simple_spinner_item);
                }else if (college.equals("-理工类院系-")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.lgCollege, android.R.layout.simple_spinner_item);
                }else if (college.equals("-人文类院系-")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.rwCollege, android.R.layout.simple_spinner_item);
                }else if (college.equals("-医学类院系-")) {
                    majorAdapter = ArrayAdapter.createFromResource(LoginActivity.this, R.array.yiCollege, android.R.layout.simple_spinner_item);
                }
                majorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //设置spinner内文字居中
                TextView tv = (TextView)view;
                tv.setTextColor(getResources().getColor(android.R.color.black));    //设置颜色
                tv.setTextSize(18);                                                 //设置大小
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);              //设置居中
                spinMajor.setAdapter(majorAdapter);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //spinner选项选择事件
        spinMajor.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stuMajor = (String) spinMajor.getItemAtPosition(position);
                TextView tv = (TextView)view;
                tv.setTextColor(getResources().getColor(android.R.color.black));    //设置颜色
                tv.setTextSize(18);                                                 //设置大小
                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);              //设置居中
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    //===============================确认Button与教师Button==================================
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                // 过滤掉不合法的用户名
                uname = ((ClearEditText) findViewById(R.id.edt_uname));
                String stuName = uname.getText().toString();
                if (TextUtils.isEmpty(stuName)) {
                    uname.setShakeAnimation();
                    showTip("用户名不能为空");
                    return;
                } else {
                    Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
                    Matcher m = p.matcher(stuName);
                    if (m.find()) {
                        showTip("不支持中文字符");
                        return;
                    } else if (stuName.contains(" ")) {
                        showTip("不能包含空格");
                        return;
                    } else if (!stuName.matches("^[a-zA-Z][a-zA-Z0-9_]{5,17}")) {
                        showTip("6-18个字母、数字或下划线的组合，以字母开头");
                        return;
                    }
                }
                //完成输入，进入识别页面，传送学生ID与上课时间
                Intent intent = new Intent(LoginActivity.this, IsvActivity.class);
                intent.putExtra("uname", stuName);
                intent.putExtra("Class_time", ClassTime);
                String stuFrom = stuMajor + stuClass + " " + stuCollege;
                intent.putExtra("Student_from", stuFrom);
                startActivity(intent);
                break;
            case R.id.btn_confirm2:
                //此为教师界面的入口
                total_stu_number = ((ClearEditText) findViewById(R.id.stu_number));
                String totalStuNumber = total_stu_number.getText().toString();
                if (TextUtils.isEmpty(totalStuNumber)) {
                    total_stu_number.setShakeAnimation();
                    showTip("请填写班级总人数");
                    return;
                } else {
                    Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
                    //layout文件中已经限定只能输入数字，所以这里只需检测小数点就好
                    if (totalStuNumber.contains(".")) {
                        showTip("不能填写数字以外字符");
                        return;
                    }
                }
                Intent intent2 = new Intent(LoginActivity.this, CheckActivityTeacher.class);
                intent2.putExtra("Class_time", ClassTime);
                String stuFrom2 = stuMajor + stuClass + " " + stuCollege;
                intent2.putExtra("Student_from", stuFrom2);
                intent2.putExtra("Totle_stuNumber", totalStuNumber);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
}
