package cn.lizonglin.meilan.xunqiandao;

/**
 * Created by Lizl on 2017/5/26.
 */
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import static android.R.attr.animation;

public class LoadActivity extends Activity {
  //背景图显示的时间,切换的时间
    private static final int LOAD_DISPLAY_TIME = 100;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        setContentView(R.layout.activity_load);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent mainIntent = new Intent(LoadActivity.this, LoginActivity.class);
                LoadActivity.this.startActivity(mainIntent);
                LoadActivity.this.finish();
            }
        }, LOAD_DISPLAY_TIME);
    }
    //===================也可使用以下代码==================================================
   /*private static final int LOAD_DISPLAY_TIME = 3000;
   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.activity_load, null);
        setContentView(view);
        //渐变不起作用
        //渐变背景图片
         //param1 : 0.0f为完全透明
         //param2 :1.0f为完全不透明
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.5f);
        animation.setDuration(50);//设置动画持续时间
        //view.setAnimation(animation);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                toMainActivity();
            }
        });
    }
    //从登陆界面跳转到主界面MainActivity
    private void toMainActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
   }
*/
}