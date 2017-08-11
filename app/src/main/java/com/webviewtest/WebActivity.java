package com.webviewtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * web页面
 * Created by surge on 2017/4/10.
 */

public class WebActivity extends Activity implements SurfaceHolder.Callback{

    private RelativeLayout videoRel;
    /*video*/
    private SurfaceView videoView;
    /*视频管理*/
    private MediaPlayerManage mediaPlayerManage;
    private WebView webView;
    private WebView webViewList;
    private Button btn;
//    private WebSettings webSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        // Register
        EventBus.getDefault().register(this);
        initView();
        webView= (WebView) findViewById(R.id.web_webview);
        webViewList= (WebView) findViewById(R.id.web_list_webview);
        webViewList.setBackgroundColor(0); // 设置背景色
        webViewList.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255
        setWebView(webView);
        setWebView(webViewList);

    }

    /**
     *
     * @param webView
     */
    private void setWebView(WebView webView){
        // 获取webview的相关配置
        WebSettings webSettings = webView.getSettings();
        //webview的缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBuiltInZoomControls(true);
        //自动缩放
        webSettings.setSupportZoom(true);
        //设置webview的插件转状态
        webSettings.setPluginState(WebSettings.PluginState.ON);
        //允许与js交互
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        webView.addJavascriptInterface(new AndroidtoJs(), "test");//AndroidtoJS类对象映射到js的test对象
        //设置默认的字符编码
        webSettings.setDefaultTextEncodingName("utf-8");
        webView.setWebChromeClient(new WebChromeClient());
//        webView.loadUrl("http://www.techua.com/focus/");
        webView.loadUrl("file:///android_asset/javascript.html");
        //为了防止和过滤掉一些其他的网页地址我们可以重写shouldOverrideUrlLoading
        //来覆盖掉之前的url加载路径
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return  true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("加载完成","ok");
            }

            /** 你可以在出话之前加载一些资源*/
            @Override
            public void onLoadResource(WebView view, String url) {

            }
        });
    }
    private void initView(){
        mediaPlayerManage=new MediaPlayerManage();
        videoRel= (RelativeLayout) findViewById(R.id.video_rel);
        videoView= (SurfaceView) findViewById(R.id.live_tv_video);
        videoView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置surfaceview不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前
        videoView.getHolder().addCallback(this);//对surface对象的状态进行监听

        btn= (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webViewList.setVisibility(View.VISIBLE);
                Toast.makeText(WebActivity.this,"ok",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String videoUrl;
    /**
     * 订阅接收到的消息
     * 这里的Event类型可以根据需要自定义, 这里只做基础的演示
     *
     * @param str
     */
    @Subscribe
    public void onEventMainThread(String str) {
        Log.e("传递到main内", str);
        if("gone".equals(str)){
            handler.sendEmptyMessage(1);
        }else{
            videoUrl=str;
            handler.sendEmptyMessage(0);
        }


    }

    Handler handler=new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case 0:
                    videoRel.setVisibility(View.VISIBLE);
                    mediaPlayerManage.playVideo(videoUrl,videoView.getHolder(),new Handler());
                    break;
                case 1:
//                    mediaPlayerManage.destroy();
                    videoRel.setVisibility(View.GONE);
                    break;
            }
        }
    };
    private boolean isBack;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(webViewList.isShown()){
                webViewList.setVisibility(View.GONE);
                isBack= false;
            }else if (webView.canGoBack()) {
                webView.goBack();//返回上一页面
                isBack= true;
            }
        }
        return isBack;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mediaPlayerManage.destroy();
    }

    @Override
    protected void onDestroy() {
        // Unregister
        EventBus.getDefault().unregister(this);
        mediaPlayerManage.destroy();
        super.onDestroy();
    }
}
