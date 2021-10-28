package com.android.antiaddiction.system.demo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


import com.android.antiaddiction.callback.AntiAddictionCallback;
import com.android.antiaddiction.demo.R;
import com.android.antiaddiction.dialog.IdCardDialogBuilder;
import com.android.antiaddiction.system.AntiAddictionSystemSDK;
import com.android.antiaddiction.utils.ToastUtils;
import com.android.antiaddiction.utils.enumbean.AgeGroup;


public class MainActivity extends AppCompatActivity {
    TextView showToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showToast = findViewById(R.id.showToast);

        String zplayKey = "100013027";
        AntiAddictionSystemSDK.init(this, zplayKey, new AntiAddictionCallback() {
            @Override
            public void onTouristsModeLoginSuccess(String touristsID) {
                showToast.setText("游客登录成功，游客id:" + touristsID);
            }

            @Override
            public void onTouristsModeLoginFailed() {
                showToast.setText("游客登录失败");
            }

            @Override
            public void realNameAuthenticateSuccess() {
                showToast.setText("实名认证成功");
            }

            @Override
            public void realNameAuthenticateFailed() {
                showToast.setText("实名认证失败：");
            }

            @Override
            public void realNameAuthSuccessStatus() {
                showToast.setText("实名认证成功的状态回调");
            }

            @Override
            public void noTimeLeftWithTouristsMode() {
                // 游客时长已用尽(1h/15 days)
                // 收到此回调 3s 后，会展示游客时长已用尽弹窗
                // 游戏请在收到回调 3s 内处理未尽事宜
                showToast.setText("游客时长已用尽");
            }

            @Override
            public void noTimeLeftWithNonageMode() {
                // 未成年时长已用尽(2h/1 day)
                // 收到此回调 3s 后，会展示未成年时长已用尽弹窗
                // 游戏请在收到回调 3s 内处理未尽事宜
                showToast.setText("未成年时长已用尽");
            }

            @Override
            public void onCurrentUserInfo(long leftTime, boolean isAuth, AgeGroup isAdult) {
                showToast.setText("当前用户剩余可玩时长(-1表示无限制)：" + leftTime + "，是否实名认证：" + isAuth + "，是否成年：" + isAdult);

            }

            @Override
            public void onClickExitGameButton() {
                AntiAddictionSystemSDK.onPause();
                // 当前用户可玩游戏时长已经结束，请进行退出游戏操作
                showToast.setText("当前用户可玩游戏时长已经结束，请进行退出游戏操作");
            }

            @Override
            public void onClickTempLeaveButton() {
                //用户点击实名认证界面继续游戏按钮
                showToast.setText("用户点击实名认证界面继续游戏按钮");
            }

            @Override
            public void onCurrentUserCanPay() {
                //当前用户可以购买
                showToast.setText("当前用户可以购买");
            }

            @Override
            public void onCurrentUserBanPay() {
                //当前用户禁止购买
                showToast.setText("当前用户禁止购买");
            }

            @Override
            public void onCurrentChannelUserInfo(AgeGroup ageGroup) {
                //当前华为，联想渠道用户的实名认证状态
                showToast.setText("当前渠道用户的实名认证状态：" + ageGroup);
            }

            @Override
            public void onUserGroupSuccessResult(String userGroup) {
                //获取UserGroup(1:新用户,2:老用户)
            }
        });

    }

    // 防沉迷提示弹窗界面
    // 游戏可主动调用
    // 游戏进入主界面之后，调用下面的接口，给未实名或者未成年人介绍实名认证规范
    public void showAntiAddictionPromptDialog(View view) {
        if (AntiAddictionSystemSDK.isLogined(this)) {
            AntiAddictionSystemSDK.showAlertInfoDialog(this);
        }
    }

    // 实名认证界面
    // 游戏可主动调用
    // 如果游戏无主动调用，游客游戏时长用尽后由SDK主动触发
    public void showUserIdCard(View view) {
        AntiAddictionSystemSDK.showRealNameDialog(this);
    }

    // 实名认证界面
    // 游戏可主动调用
    // 显示带退出游戏按钮的实名认证界面
    public void showForceExitRealNameDialog(View view) {
        AntiAddictionSystemSDK.showForceExitRealNameDialog(this);
    }

    public void isLogined(View view) {
        ToastUtils.showToast(MainActivity.this, "登录状态：" + AntiAddictionSystemSDK.isLogined(this));
    }

    public void isAuthenticated(View view) {
        ToastUtils.showToast(MainActivity.this, "实名认证状态：" + AntiAddictionSystemSDK.isAuthenticated(this));
    }

    public void isAdult(View view) {
        ToastUtils.showToast(MainActivity.this, "是否成年：" + AntiAddictionSystemSDK.isAdult(this));
    }

    public void leftTimeOfCurrentUser(View view) {
        ToastUtils.showToast(MainActivity.this, "剩余游戏时长（-1为无限制，单位秒）：" + AntiAddictionSystemSDK.leftTimeOfCurrentUser(this));
    }

    //显示剩余游戏时长说明弹窗
    public void showTimeTipsDialog(View view) {
        AntiAddictionSystemSDK.showTimeTipsDialog(this);
    }

    //检测当前用户是否能购买计费
    public void checkPay(View view) {
        AntiAddictionSystemSDK.checkCurrentUserPay(this);
    }

    //华为渠道，当华为登录失败时调用，走游客逻辑，会给游戏返回一个noTimeLeftWithTouristsMode回调，游戏在此回调中引导用户登录华为账号
    public void touristsLogin(View view) {
        AntiAddictionSystemSDK.setGroupId(this, 1);
    }



    @Override
    protected void onPause() {
        super.onPause();
        //游戏退到后台接口必须调用，不调用会导致防沉迷SDK计算游戏时长错误
        AntiAddictionSystemSDK.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //游戏恢复前台接口必须调用，不调用会导致防沉迷SDK计算游戏时长错误
        AntiAddictionSystemSDK.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        IdCardDialogBuilder.setHasShowing();
    }
}