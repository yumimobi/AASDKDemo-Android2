# 1. 概述

## 1.1 面向人群

当前文档面向需要在 Android 产品中接入中宣部防沉迷 SDK 的开发人员。
   
## 1.2 开发环境

OS: Windows, Mac, Linux <br/>
Android SDK: > 4.4(API level 19)<br/>
IDE: Eclipse with ADT (ADT version 23.0.4) OR Android-Studio<br/>
Java: > JDK 7
  

# 2. 开发环境配置

## 2.1 Android-studio 接入

在 app 下的 build.gradle 中添加依赖相关依赖

```groovy
dependencies {
    // AntiAddictionSDK
    implementation "io.github.yumimobi:antiaddiction:2.0.0"
｝
```

在工程的 build.gradle 中添加maven central

```groovy
allprojects {
    repositories {
        mavenCentral()
        maven { url 'https://repo1.maven.org/maven2/' }
    }
}
```


 ## 2.3 防混淆配置
 如果您的工程需要混淆编译， 请在混淆文件内增加以下内容

 ```groovy
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,Synthetic,EnclosingMethod
-keep class com.android.antiaddiction.system.** { *;}
```


# 3. 配置防沉迷SDK需要的参数

## 3.1 下载ZplayConfig.xml文件,并添加到工程的assets目录下

<img src="resources\antiAddiction-ZplayConfig.jpg" alt="antiAddiction-ZplayConfig">

>[ZplayConfig.xml 下载](https://github.com/yumimobi/AntiAddictionSystemDemo-Android/blob/master/app/src/main/assets/ZplayConfig.xml)

## 3.2 修改ZplayConfig.xml文件中的配置
<img src="resources\antiAddiction-ZplayConfig1.png" alt="antiAddiction-ZplayConfig1">

<div style="background-color:rgb(228,244,253);padding:10px;">
<span style="color:rgb(62,113,167);">ZplayConfig.xml文件中的GameID, ChannelID参数，请联系掌游产品获取</span></div>
<br/>


# 4.快速接入 
## 4.1初始化

```java
// ZplayKey 参数请联系掌游产品获取
 AntiAddictionSystemSDK.init(Activity, ZplayKey,new AntiAddictionCallback() {
            @Override
            public void onTouristsModeLoginSuccess(String touristsID) {
                //游客登录成功，游客id:touristsID
            }

            @Override
            public void onTouristsModeLoginFailed() {
                //游客登录失败
            }

             @Override
            public void realNameAuthenticateSuccess() {
                //实名认证成功
            }

            @Override
            public void realNameAuthenticateFailed() {
                //实名认证失败
            }

            @Override
            public void realNameAuthSuccessStatus() {
                //实名认证成功的状态回调，当调用 4.6.3 更新防沉迷数据接口，并且服务端返回已经实名认证后才会返回本回调
            }

            @Override
            public void noTimeLeftWithTouristsMode() {
                // 游客时长已用尽(1h/15 days)
                // 收到此回调 3s 后，会展示游客时长已用尽弹窗
                // 游戏请在收到回调 3s 内处理未尽事宜
            }

            @Override
            public void noTimeLeftWithNonageMode() {
                // 未成年时长已用尽(2h/1 day)
                // 收到此回调 3s 后，会展示未成年时长已用尽弹窗
                // 游戏请在收到回调 3s 内处理未尽事宜
            }

             @Override
            public void onClickExitGameButton() {
                // 用户点击退出游戏按钮，或者是未成年人时长已到，用户点击退出游戏
            }

            @Override
            public void onClickTempLeaveButton() {
                //用户点击实名认证界面暂不认证按钮
            }

            @Override
            public void onCurrentUserInfo(long leftTime, boolean isAuth, AgeGroup ageGroup) {
                 // 此回调每秒执行一次
                 // leftTime: 当前用户剩余时间，-1无限制

                 // isAuth: 是否已认证
                 // true:已认证
                 // false:未认证

                 // ageGroup: 用户年龄段
                 // AgeGroup.unknown: 年龄段位置，用户未认证
                 // AgeGroup.adult: 成年
                 // AgeGroup.nonage: 未成年
                 
            }

             @Override
            public void onCurrentUserCanPay() {
                //当前用户可以购买
            }

            @Override
            public void onCurrentUserBanPay() {
                //当前用户禁止购买，SDK会弹出禁止购买提示

            }

            @Override
            public void onCurrentChannelUserInfo(AgeGroup ageGroup) {
                //当前华为，联想渠道用户的实名认证状态
                // ageGroup: 用户年龄段
                // AgeGroup.unknown: 年龄段位置，用户未认证
                // AgeGroup.adult: 成年
                // AgeGroup.nonage: 未成年
                 
            }

             @Override
            public void onUserGroupSuccessResult(String userGroup) {
                //userGroup(1:新用户,2:老用户)
            }
        });
```  

## 4.2 展示游客和未成年人剩余时长提示（非产品要求可以不接）
每次进入游戏主界面时，展示游客和未成年人用户在线时长提示界面
此界面由游戏在初始化后调用。
需判断SDK已登录，如SDK未登录，则在SDK登录成功的回调中调用。
成年人无需展示此界面。
```java
 if (AntiAddictionSystemSDK.isLogined(this)) {
    AntiAddictionSystemSDK.showAlertInfoDialog(this);
 }
``` 


## 4.3 实名认证接口

### 4.3.1 展示实名认证接口（用户可点击暂不认证）
如果游戏主界面上提供了用户点击实名认证的功能，可调用下面的接口，展示防沉迷SDK的实名认证界面，为用户提供实名认证功能
```java
AntiAddictionSystemSDK.showRealNameDialog(Activity);
```  

### 4.3.2 展示实名认证接口（用户可点击退出游戏)
使用场景:
如果用户点击退出游戏，开发者需要在onClickExitGameButton();此回调中展示实名认证获取奖励界面（此界面由开发者自己实现），此界面提供两个交互按钮。
退出游戏按钮：点击此按钮退出游戏。
实名认证按钮：点击此按钮再次展示SDK提供的实名认证界面。
```java
AntiAddictionSystemSDK.showForceExitRealNameDialog(Activity);
```  


## 4.4 其他接口

### 4.4.1 游戏退到后台接口(必选)
当用户按home键，将游戏退出到后台时，请调用下面的接口

<span style="color:rgb(255,0,0);">
<b>重要提示：</b> 游戏退到后台接口必须调用，不调用会导致防沉迷SDK计算游戏时长错误
</span>

```java
AntiAddictionSystemSDK.onPause();
```

### 4.4.2 游戏恢复前台接口(必选)
当用户将游戏恢复到前台时，请调用下面的接口

<span style="color:rgb(255,0,0);">
<b>重要提示：</b> 游戏恢复前台接口必须调用，不调用会导致防沉迷SDK计算游戏时长错误
</span>

```java
AntiAddictionSystemSDK.onResume();
```


### 4.4.3 获取游客登录状态接口(可选)
```java
//获取游客登录状态
//true:登录成功
//false:登录失败
boolean isLogined = AntiAddictionSystemSDK.isLogined(Activity);
```

### 4.4.4 获取实名认证状态接口(可选)
```java
//获取实名认证状态
//false:未认证
//true:已认证
boolean isAuthenticated = AntiAddictionSystemSDK.isAuthenticated(Activity);
```

### 4.4.5 查询用户是否成年接口(可选)
```java
//查询用户是否成年
// AgeGroup.unknown: 年龄段位置，用户未认证
// AgeGroup.adult: 成年
// AgeGroup.nonage: 未成年
AgeGroup ageGroup = AntiAddictionSystemSDK.isAdult(Activity);
```


### 4.4.6 获取用户剩余可玩时长接口(可选)
```java
//获取用户剩余可玩时长
//-1:表示用户为成年人，不受限制
//大于0的数:用户的剩余可玩时长，单位秒
long leftTimeOfCurrentUser = AntiAddictionSystemSDK.leftTimeOfCurrentUser(Activity)
```


### 4.4.7 展示查看详情界面(可选)
此界面展示中宣部关于防沉迷政策的相关规则
```java
AntiAddictionSystemSDK.showTimeTipsDialog(Activity);
```

### 4.4.8 检测消费限制(可选)
未登录及未成年人无法在游戏中付费，会显示消费限制界面。
成年人无限制
```java
AntiAddictionSystemSDK.checkCurrentUserPay(Activity);
```

## 4.5 用户分组相关接口

### 4.5.1 获取用户标识接口(可选)
```java
AntiAddictionSystemSDK.getUserCode(Activity);
```

### 4.5.2 获取分组id接口(可选)
// -1: 游戏未设置
// 1: 新用户
// 2：老用户
```java
AntiAddictionSystemSDK.getGroupId(Activity);
```
