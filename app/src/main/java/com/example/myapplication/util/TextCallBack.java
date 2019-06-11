package com.example.myapplication.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/8/17.
 */
public abstract class TextCallBack<T> implements Callback {

    public static final int CODE_SUCCESS = 1;
    public static final int CODE_FAILURE = 0;
    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case CODE_FAILURE:
                    onFailure((ResponseException) msg.obj);
                    break;
                case CODE_SUCCESS:

                    onSuccess((String) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 主线程调用
     * @param text
     */
    protected abstract void onSuccess(String text);

    /**
     * 主线程调用
     * @param e
     */
    protected abstract void onFailure(ResponseException e);

    /**
     * 子线程调用
     * @param call
     * @param e
     */
    @Override
    public void onFailure(Call call, IOException e) {
        ResponseException exception = null;
        if(e instanceof ResponseException == false)
        {
            exception = new ResponseException(e);
        }else
        {
            exception = (ResponseException) e;
        }
        Message message = mHandler.obtainMessage();
        message.what = CODE_FAILURE;
        message.obj = exception;
        mHandler.sendMessage(message);
    }

    /**
     * 子线程调用
     * @param call
     * @param response
     * @throws IOException
     */
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if(response.code() == 200)
        {
            //成功
            String text = response.body().string();
            Message message = mHandler.obtainMessage();
            message.what = CODE_SUCCESS;
            message.obj = text;
            mHandler.sendMessage(message);
        }else{
            onFailure(call, new ResponseException(response.code()));
        }
    }

    public static class ResponseException extends IOException
    {
        int code = -1;
        ResponseException(IOException e)
        {
            super(e);
        }

        ResponseException(int code)
        {
            super("返回错误");
            code = code;
        }
    }

}
