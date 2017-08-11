package com.webviewtest;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by surge on 2017/7/26.
 */

public class AndroidtoJs extends Object{
    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void hello(String msg) {
        System.out.println("JS调用了Android的hello方法");
        Log.e("js传过来",msg);
    }

    @JavascriptInterface
    public String getData(){
        return Util.getMACAddress()+"///";
    }

    @JavascriptInterface
    public void playVideo(String url){
        EventBus.getDefault().post(url);
    }

    @JavascriptInterface
    public void goneVideo(){
        EventBus.getDefault().post("gone");
    }
}
