package cn.lizonglin.meilan.xunqiandao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;      //提供访问环境变量
import android.os.Bundle;           //Bundle主要用于传递数据；它保存的数据，是以key-value(键值对)的形式存在的
import android.text.TextUtils;      //对字符串进行处理
import android.util.Log;            //开发过程中打印
import android.view.Gravity;        //页面布局
import android.view.View;           //描绘块状视图的基类
import android.view.Window;         //窗口
import android.widget.EditText;     //输入信息
import android.widget.RadioGroup;   //选项，多选一，用于选择密码时
import android.widget.TextView;     //widget包里面是android提供的所有控件
import android.widget.Toast;        //显示信息的一种机制,没有焦点的,且显示时间有限

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;

//org.json是Java中解释json的主流工具
import org.json.JSONArray;      //密集索引的值序列
import org.json.JSONException;  //抛出表示JSON API的问题
import org.json.JSONObject;     //一组可修改的名称/值映射

public class IsvActivity extends Activity implements View.OnClickListener {
    private static final String TAG = IsvActivity.class.getSimpleName();
    //讯飞声纹识别需要使用密码，分别使用1、2、3，分别为文本、自由说和数字密码
    private static final int PWD_TYPE_TEXT = 1;
    private static final int PWD_TYPE_NUM  = 3;
    private int mPwdType = PWD_TYPE_TEXT;// 当前声纹密码类型
    private SpeakerVerifier mVerifier;   // 声纹识别对象
    // 声纹AuthId，用户在云平台的身份标识，也是声纹模型的标识
    // 注册时必须使用英文字母或者字母和数字的组合
    private String mTextPwd = "";        // 文本声纹密码
    private String mNumPwd  = "";         // 数字声纹密码
    private String Class_time = "";
    private String Student_from = "";
    // 数字声纹密码段，默认有5段，小于5段会影响识别
    private String[] mNumPwdSegs;
    private String mAuthId = "";
    private EditText mResultEditText;
    private TextView mAuthIdTextView;
    private RadioGroup mPwdTypeGroup;
    private TextView mShowPwdTextView;
    private TextView mShowMsgTextView;
    private TextView mShowRegFbkTextView;
    private TextView mStuFromTextView;
    private TextView mRecordTimeTextView;
    private TextView mClassTimeTextView;
    private AlertDialog mTextPwdSelectDialog;
    private Toast mToast;
    public static IsvActivity isvflag = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_isv);
        initUi();
        // 将上个页面输入的用户名(即学号)作为AuthId
        // 同时拿到学号，上课时间，学院专业班级
        mAuthId = getIntent().getStringExtra("uname");
        mAuthIdTextView.setText(mAuthId);       //学生学号
        Class_time = getIntent().getStringExtra("Class_time");
        mClassTimeTextView.setText(Class_time); //上课时间
        Student_from = getIntent().getStringExtra("Student_from");
        mStuFromTextView.setText(Student_from); //上课时间
        // 初始化SpeakerVerifier，InitListener为初始化完成后的回调接口
        mVerifier = SpeakerVerifier.createVerifier(IsvActivity.this, new InitListener() {

            @Override
            public void onInit(int errorCode) {
                if (ErrorCode.SUCCESS == errorCode) {
                    showTip("引擎初始化成功");
                } else {
                    showTip("引擎初始化失败，错误码：" + errorCode);
                }
            }
        });
    }

    @SuppressLint("ShowToast")
    private void initUi() {
        mResultEditText     = (EditText) findViewById(R.id.edt_result);      //显示密码
        mAuthIdTextView     = (TextView) findViewById(R.id.txt_authorid);    //用户标识-显示用户的ID
        mShowPwdTextView    = (TextView) findViewById(R.id.showPwd);         //展示要读取的密码
        mShowMsgTextView    = (TextView) findViewById(R.id.showMsg);         //显示次数
        mShowRegFbkTextView = (TextView) findViewById(R.id.showRegFbk);      //显示反馈
        mRecordTimeTextView = (TextView) findViewById(R.id.recordTime);      //这个没用上
        mClassTimeTextView  = (TextView) findViewById(R.id.class_time_auto); //用户标识-显示用户的ID
        mStuFromTextView    = (TextView) findViewById(R.id.stu_from);        //用户标识-显示用户的ID
        findViewById(R.id.isv_register).setOnClickListener(IsvActivity.this);    //注册
        findViewById(R.id.isv_verify).setOnClickListener(IsvActivity.this);      //验证
        findViewById(R.id.isv_stop_record).setOnClickListener(IsvActivity.this); //停止录音
        findViewById(R.id.isv_cancel).setOnClickListener(IsvActivity.this);      //取消
        findViewById(R.id.isv_getpassword).setOnClickListener(IsvActivity.this); //获取密码
        findViewById(R.id.isv_search).setOnClickListener(IsvActivity.this);      //查询模型
        findViewById(R.id.isv_delete).setOnClickListener(IsvActivity.this);      //删除模型

        // 密码选择RadioGroup,有文本与数字两种可选
        mPwdTypeGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mPwdTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                initTextView();
                switch (checkedId) {
                    case R.id.radioNumber:
                        mPwdType = PWD_TYPE_NUM;
                        break;
                    case R.id.radioText:
                        mPwdType = PWD_TYPE_TEXT;
                        break;
                    default:
                        break;
                }
            }
        });
        mToast = Toast.makeText(IsvActivity.this, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //初始化TextView和密码文本
    private void initTextView(){
        mTextPwd = null;
        mNumPwd = null;
        mResultEditText.setText("");
        mShowPwdTextView.setText("");
        mShowMsgTextView.setText("");
        mShowRegFbkTextView.setText("");
        mRecordTimeTextView.setText("");
    }

     //设置radio的状态
    private void setRadioClickable(boolean clickable){
        // 设置RaioGroup状态为非按下状态
        mPwdTypeGroup.setPressed(false);
        findViewById(R.id.radioNumber).setClickable(clickable);
        findViewById(R.id.radioText).setClickable(clickable);
    }

    /**
     * 执行模型操作
     *
     * @param operation 操作命令
     * @param listener  操作结果回调对象
     */
    private void performModelOperation(String operation, SpeechListener listener) {
        // 清空参数
        mVerifier.setParameter(SpeechConstant.PARAMS, null);
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
        if (mPwdType == PWD_TYPE_TEXT) {
            // 文本密码删除需要传入密码
            if (TextUtils.isEmpty(mTextPwd)) {
                showTip("请获取密码后进行操作");
                return;
            }
            mVerifier.setParameter(SpeechConstant.ISV_PWD, mTextPwd);
        } else if (mPwdType == PWD_TYPE_NUM) {
        }
        setRadioClickable(false);
        // 设置auth_id，不能设置为空
        mVerifier.sendRequest(operation, mAuthId, listener);
    }

    @Override
    public void onClick(View v) {
        if( !checkInstance() ){
            return;
        }
        switch (v.getId()) {
            case R.id.isv_getpassword:
                // 获取密码之前先终止之前的注册或验证过程
                mVerifier.cancel();
                initTextView();
                setRadioClickable(false);
                // 清空参数
                mVerifier.setParameter(SpeechConstant.PARAMS, null);
                mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
                mVerifier.getPasswordList(mPwdListenter);
                break;
            case R.id.isv_search:
                performModelOperation("que", mModelOperationListener);
                break;
            case R.id.isv_delete:
                performModelOperation("del", mModelOperationListener);
                break;
            case R.id.isv_register:
                // 清空参数
                mVerifier.setParameter(SpeechConstant.PARAMS, null);
                mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test.pcm");
                // 对于某些麦克风非常灵敏的机器，加上以下设置对录音进行消噪处理
    			//mVerify.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);
                if (mPwdType == PWD_TYPE_TEXT) {
                    // 文本密码注册需要传入密码
                    if (TextUtils.isEmpty(mTextPwd)) {
                        showTip("请获取密码后进行操作");
                        return;
                    }
                    mVerifier.setParameter(SpeechConstant.ISV_PWD, mTextPwd);
                    mShowPwdTextView.setText("请读出：" + mTextPwd);
                    mShowMsgTextView.setText("训练 第" + 1 + "遍，剩余4遍");
                } else if (mPwdType == PWD_TYPE_NUM) {
                    // 数字密码注册需要传入密码
                    if (TextUtils.isEmpty(mNumPwd)) {
                        showTip("请获取密码后进行操作");
                        return;
                    }
                    mVerifier.setParameter(SpeechConstant.ISV_PWD, mNumPwd);
                    ((TextView) findViewById(R.id.showPwd)).setText("请读出："
                            + mNumPwd.substring(0, 8));
                    mShowMsgTextView.setText("训练 第" + 1 + "遍，剩余4遍");
                }
                setRadioClickable(false);
                // 设置auth_id，不能设置为空
                mVerifier.setParameter(SpeechConstant.AUTH_ID, mAuthId);
                // 设置业务类型为注册
                mVerifier.setParameter(SpeechConstant.ISV_SST, "train");
                // 设置声纹密码类型
                mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
                // 开始注册
                mVerifier.startListening(mRegisterListener);
                break;
            case R.id.isv_verify:
                // 清空提示信息
                ((TextView) findViewById(R.id.showMsg)).setText("");
                // 清空参数
                mVerifier.setParameter(SpeechConstant.PARAMS, null);
                mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
                mVerifier = SpeakerVerifier.getVerifier();
                // 设置业务类型为验证
                mVerifier.setParameter(SpeechConstant.ISV_SST, "verify");
                // 对于某些麦克风非常灵敏的机器，加上以下设置对录音进行消噪处理
        		//mVerify.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);
                if (mPwdType == PWD_TYPE_TEXT) {
                    // 文本密码注册需要传入密码
                    if (TextUtils.isEmpty(mTextPwd)) {
                        showTip("请获取密码后进行操作");
                        return;
                    }
                    mVerifier.setParameter(SpeechConstant.ISV_PWD, mTextPwd);
                    ((TextView) findViewById(R.id.showPwd)).setText("请读出："
                            + mTextPwd);
                } else if (mPwdType == PWD_TYPE_NUM) {
                    // 数字密码注册需要传入密码
                    String verifyPwd = mVerifier.generatePassword(8);
                    mVerifier.setParameter(SpeechConstant.ISV_PWD, verifyPwd);
                    ((TextView) findViewById(R.id.showPwd)).setText("请读出："
                            + verifyPwd);
                }
                setRadioClickable(false);
                // 设置auth_id，不能设置为空
                mVerifier.setParameter(SpeechConstant.AUTH_ID, mAuthId);
                mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
                // 开始验证
                mVerifier.startListening(mVerifyListener);
                break;
            case R.id.isv_stop_record:
                mVerifier.stopListening();
                break;
            case R.id.isv_cancel:
                setRadioClickable(true);
                mVerifier.cancel();
                initTextView();
                break;
            default:
                break;
        }
    }

    private String[] items;
    private SpeechListener mPwdListenter = new SpeechListener() {
        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            setRadioClickable(true);
            String result = new String(buffer);
            switch (mPwdType) {
                case PWD_TYPE_TEXT:
                    try {
                        JSONObject object = new JSONObject(result);
                        if (!object.has("txt_pwd")) {
                            initTextView();
                            return;
                        }
                        JSONArray pwdArray = object.optJSONArray("txt_pwd");
                        items = new String[pwdArray.length()];
                        for (int i = 0; i < pwdArray.length(); i++) {
                            items[i] = pwdArray.getString(i);
                        }
                        mTextPwdSelectDialog = new AlertDialog.Builder(
                                IsvActivity.this)
                                .setTitle("请选择密码文本")
                                .setItems(items,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface arg0, int arg1) {
                                                mTextPwd = items[arg1];
                                                mResultEditText.setText("您的密码：" + mTextPwd);
                                            }
                                        }).create();
                        mTextPwdSelectDialog.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case PWD_TYPE_NUM:
                    StringBuffer numberString = new StringBuffer();
                    try {
                        JSONObject object = new JSONObject(result);
                        if (!object.has("num_pwd")) {
                            initTextView();
                            return;
                        }
                        JSONArray pwdArray = object.optJSONArray("num_pwd");
                        numberString.append(pwdArray.get(0));
                        for (int i = 1; i < pwdArray.length(); i++) {
                            numberString.append("-" + pwdArray.get(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mNumPwd = numberString.toString();
                    mNumPwdSegs = mNumPwd.split("-");
                    mResultEditText.setText("您的密码：\n" + mNumPwd);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onCompleted(SpeechError error) {
            setRadioClickable(true);
            if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
                showTip("获取失败：" + error.getErrorCode());
            }
        }
    };

    private SpeechListener mModelOperationListener = new SpeechListener() {

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
        @Override
        public void onBufferReceived(byte[] buffer) {
            setRadioClickable(true);
            String result = new String(buffer);
            try {
                JSONObject object = new JSONObject(result);
                String cmd = object.getString("cmd");
                int ret = object.getInt("ret");

                if ("del".equals(cmd)) {
                    if (ret == ErrorCode.SUCCESS) {
                        showTip("删除成功");
                        mResultEditText.setText("");
                    } else if (ret == ErrorCode.MSP_ERROR_FAIL) {
                        showTip("删除失败，模型不存在");
                    }
                } else if ("que".equals(cmd)) {
                    if (ret == ErrorCode.SUCCESS) {
                        showTip("模型存在");
                    } else if (ret == ErrorCode.MSP_ERROR_FAIL) {
                        showTip("模型不存在");
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted(SpeechError error) {
            setRadioClickable(true);
            if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
                showTip("操作失败：" + error.getPlainDescription(true));
            }
        }
    };

    private VerifierListener mVerifyListener = new VerifierListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onResult(VerifierResult result) {
            setRadioClickable(true);
            mShowMsgTextView.setText(result.source);
            if (result.ret == 0) {
                // 验证通过
                mShowMsgTextView.setText("验证通过");
                //验证通过后，从验证界面前往签到界面，并传送学生ID与上课时间
                Intent changeActivity  = new Intent();
                changeActivity .setClass(IsvActivity.this, CheckActivity.class);
                String a = mAuthId;
                isvflag = IsvActivity.this;
                changeActivity .putExtra("mAuthId", a); //intent.putExtra('key',值)
                changeActivity .putExtra("class_time", Class_time);
                changeActivity .putExtra("student_from", Student_from);
                startActivity(changeActivity );
            }
            else{
                // 验证不通过
                switch (result.err) {
                    case VerifierResult.MSS_ERROR_IVP_GENERAL:
                        mShowMsgTextView.setText("内核异常");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
                        mShowMsgTextView.setText("出现截幅");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
                        mShowMsgTextView.setText("太多噪音");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
                        mShowMsgTextView.setText("录音太短");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
                        mShowMsgTextView.setText("验证不通过，所读文本不一致");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
                        mShowMsgTextView.setText("音量太低");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
                        mShowMsgTextView.setText("音频长达不到自由说的要求");
                        break;
                    default:
                        mShowMsgTextView.setText("验证不通过");
                        break;
                }
            }
        }
        // 保留方法，暂不用
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle arg3) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }

        @Override
        public void onError(SpeechError error) {
            setRadioClickable(true);
            switch (error.getErrorCode()) {
                case ErrorCode.MSP_ERROR_NOT_FOUND:
                    mShowMsgTextView.setText("模型不存在，请先注册");
                    break;
                default:
                    showTip("onError Code："	+ error.getPlainDescription(true));
                    break;
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }
    };

    private VerifierListener mRegisterListener = new VerifierListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onResult(VerifierResult result) {
            ((TextView)findViewById(R.id.showMsg)).setText(result.source);

            if (result.ret == ErrorCode.SUCCESS) {
                switch (result.err) {
                    case VerifierResult.MSS_ERROR_IVP_GENERAL:
                        mShowMsgTextView.setText("内核异常");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_EXTRA_RGN_SOPPORT:
                        mShowRegFbkTextView.setText("训练达到最大次数");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
                        mShowRegFbkTextView.setText("出现截幅");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
                        mShowRegFbkTextView.setText("太多噪音");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
                        mShowRegFbkTextView.setText("录音太短");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
                        mShowRegFbkTextView.setText("训练失败，所读文本不一致");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
                        mShowRegFbkTextView.setText("音量太低");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
                        mShowMsgTextView.setText("音频长达不到自由说的要求");
                    default:
                        mShowRegFbkTextView.setText("");
                        break;
                }
                if (result.suc == result.rgn) {
                    setRadioClickable(true);
                    mShowMsgTextView.setText("注册成功");

                    if (PWD_TYPE_TEXT == mPwdType) {
                        mResultEditText.setText("您的文本密码声纹ID：\n" + result.vid);
                    } else if (PWD_TYPE_NUM == mPwdType) {
                        mResultEditText.setText("您的数字密码声纹ID：\n" + result.vid);
                    }

                } else {
                    int nowTimes = result.suc + 1;
                    int leftTimes = result.rgn - nowTimes;
                    if (PWD_TYPE_TEXT == mPwdType) {
                        mShowPwdTextView.setText("请读出：" + mTextPwd);
                    } else if (PWD_TYPE_NUM == mPwdType) {
                        mShowPwdTextView.setText("请读出：" + mNumPwdSegs[nowTimes - 1]);
                    }
                    mShowMsgTextView.setText("训练 第" + nowTimes + "遍，剩余" + leftTimes + "遍");
                }

            }else {
                setRadioClickable(true);
                mShowMsgTextView.setText("注册失败，请重新开始。");
            }
        }
        // 保留方法，暂不用
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle arg3) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }

        @Override
        public void onError(SpeechError error) {
            setRadioClickable(true);
            if (error.getErrorCode() == ErrorCode.MSP_ERROR_ALREADY_EXIST) {
                showTip("模型已存在，如需重新注册，请先删除");
            } else {
                showTip("onError Code：" + error.getPlainDescription(true));
            }
        }

        @Override
        public void onEndOfSpeech() {
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
        }
    };

    @Override
    public void finish() {
        if (null != mTextPwdSelectDialog) {
            mTextPwdSelectDialog.dismiss();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (null != mVerifier) {
            mVerifier.stopListening();
            mVerifier.destroy();
        }
        super.onDestroy();
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    private boolean checkInstance(){
        if( null == mVerifier ){
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip( "创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化" );
            return false;
        }else{
            return true;
        }
    }
}
