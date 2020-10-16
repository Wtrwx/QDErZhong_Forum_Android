package com.wtrwx.discuz;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //webview初始化
        initWebview();
        FloatingActionButton fab = findViewById(R.id.fab);
        //fab初始化
        fab.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        webView.reload();
                    }
                }
        );
        fab.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                MainActivity.this
                        );
                        builder.setTitle("关于");
                        builder.setMessage("这是官方的青岛二中讨论区Android程序\n版本号:0.2\nby 维他入我心 qq:1021595605");
                        builder.setPositiveButton("OK", null);
                        builder.show();
                        return false;
                    }
                }
        );
    }

    private void initWebview() {

        webView = findViewById(R.id.WebView);
        //初始化webview
        webView.loadUrl("http://qdezlt.lexuewang.cn/");
        webView.setWebViewClient(
                new WebViewClient() {

                    @Override
                    // 判断要跳转的url
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // 判断url链接中是否含有某个字段
                        return !url.contains("qdezlt.lexuewang.cn");
                    }
                }
        );
        //返回后退
        webView.setOnKeyListener(
                new View.OnKeyListener() {

                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            //按返回键操作并且能回退网页
                            if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                                //后退
                                webView.goBack();
                                return true;
                            }
                        }
                        return false;
                    }
                }
        );
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebChromeClient(new WebChromeClient() {
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                Log.i("test", "openFileChooser 1");
                MainActivity.this.uploadFile = uploadMsg;
                openFileChooseProcess();
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsgs) {
                Log.i("test", "openFileChooser 2");
                MainActivity.this.uploadFile = uploadMsgs;
                openFileChooseProcess();
            }

            // For Android  > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                Log.i("test", "openFileChooser 3");
                MainActivity.this.uploadFile = uploadMsg;
                openFileChooseProcess();
            }

            // For Android  >= 5.0
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                Log.i("test", "openFileChooser 4:" + filePathCallback.toString());
                MainActivity.this.uploadFiles = filePathCallback;
                openFileChooseProcess();
                return true;
            }

        });

    }


    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click(); //调用双击退出函数
        }
        return false;
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true; // 准备退出
            View Coordinator = findViewById(R.id.coordinator);
            Snackbar
                    .make(Coordinator, "再按一次退出程序", Snackbar.LENGTH_SHORT)
                    .show();
            tExit = new Timer();
            tExit.schedule(
                    new TimerTask() {

                        @Override
                        public void run() {
                            isExit = false; // 取消退出
                        }
                    },
                    2000
            ); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish();
            System.exit(0);
        }
    }

    private void openFileChooseProcess() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, "上传文件"), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                if (null != uploadFile) {
                    Uri result = data == null ? null
                            : data.getData();
                    uploadFile.onReceiveValue(result);
                    uploadFile = null;
                }
                if (null != uploadFiles) {
                    Uri result = data == null ? null
                            : data.getData();
                    uploadFiles.onReceiveValue(new Uri[]{result});
                    uploadFiles = null;
                }
            } else if (resultCode == RESULT_CANCELED) {
                if (null != uploadFile) {
                    uploadFile.onReceiveValue(null);
                    uploadFile = null;
                }
            }
        }
    }
}
