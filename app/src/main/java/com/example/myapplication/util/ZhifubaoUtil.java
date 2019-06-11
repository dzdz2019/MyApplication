package com.example.myapplication.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/13.
 */

public class ZhifubaoUtil {
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    static String resultInfo;
    private static Activity context;
    static ZhiFuBao zhiFuBao;
    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String memo = payResult.getMemo();

                    Log.e("resultInfo============", "resultInfo=" + resultInfo);
                    Log.e("memo=============", "memo=" + memo);
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        //                        Toast.makeText(BendizhifuActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        zhiFuBao.success(resultInfo);


                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        //                        Toast.makeText(BendizhifuActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        zhiFuBao.faild(resultInfo);
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(context,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(context,
                                "授权失败" + String.format("请检查", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();


                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public static void pays(final String dingdaninfo, Activity activity, ZhiFuBao zfb) {
        /*if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }*/
        /**
         * 沙箱环境需要这句话
         */


        zhiFuBao = zfb;
        context = activity;
        if (checkAliPayInstalled(context)) {
//                    EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
            /**
             * 调用支付方法
             */
            Runnable authRunnable = new Runnable() {

                @Override
                public void run() {
                    // 构造AuthTask 对象
                    AuthTask authTask = new AuthTask(context);
                    // 调用授权接口，获取授权结果
                    Map<String, String> result = authTask.authV2(dingdaninfo, true);

                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };

            // 必须异步调用
            Thread authThread = new Thread(authRunnable);
            authThread.start();

        } else {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(context).create();
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("未安装支付宝，请下载");
            // 添加选择按钮并注册监听
            isExit.setButton("确定", listener);
            isExit.setButton2("取消", listener);
            // 显示对话框
            isExit.show();
            // return true;
        }


    }

    /**
     * 监听对话框里面的button点击事件
     */
    static DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    //                    moveTaskToBack(true);
                    //  finish();
                    /*Intent intent=new Intent(context,WangyeActivity.class);
                    startActivity(intent);*/
                    Intent intent = new Intent();
                    //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("https://mobile.alipay.com/index.htm");
                    intent.setData(content_url);
                    context.startActivity(intent);
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

    public static interface ZhiFuBao {
        void success(String info);

        void faild(String info);
    }
    /***
     * 支付宝检测
     *
     * @param context
     * @return
     */
    public static boolean checkAliPayInstalled(Context context) {

        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }



}
