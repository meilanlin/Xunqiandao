package cn.lizonglin.meilan.xunqiandao;

import android.app.Application;
import com.iflytek.cloud.SpeechUtility;



/**
 * Created by Lizl on 2017/5/8.
 */

public class XqdApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
        // 此处的id是在科大讯飞平台上申请的id
        SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
    }
}