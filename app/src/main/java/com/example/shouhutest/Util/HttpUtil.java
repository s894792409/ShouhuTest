package com.example.shouhutest.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 发起网页调用，获取网页调用返回值（OKHttp再封装）。调用可以是doPost方式也可以是doGet方式。
 * 也可以使用同步调用方式 doGetSync、doPostSync，此种调用方式简单方便，更好理解，但只适合在非主线程中使用。
 * 常用方法示例：
 * 1) HttpUtil.doGet("http://pa.handbbc.com/askbo", (response) -> { System.out.println(response); });
 * 2) HttpUtil.doPost("http://pa.handbbc.com/askbo", "", (response) -> { System.out.println(response); });
 */
public class HttpUtil {


    /** HttpUtil执行结果返回接口 */
    public interface OnHttpResultListener {
        void onHttpResult(String result);
    }


    /**
     * Get方式发起网页调用，主线程安全的方式，调用返回结果字符串，结果字符串在 response 里面。失败返回NULL。
     * eg: HttpUtil.doGet("http://pa.handbbc.com/askbo", (response) -> { System.out.println(response); });
     */
    public static void doGet(String url, OnHttpResultListener httpResultListener) {
        HttpUtil httpObj = new HttpUtil();
        Handler myHandler = (new Handler(new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                String response = (String) msg.obj;
                httpResultListener.onHttpResult(response);
                return false;
            }
        }));
        httpObj.doHttpAction(url, null, myHandler);
    }


    /**
     * Get同步方式发起网页调用，获取网页调用返回值。失败返回NULL。
     * eg:new Thread(() -> { System.out.println(HttpUtil.doGetSync("http://pa.handbbc.com/askbo")); }).start();
     */
    public static String doGetSync(String url) {
        return (new HttpUtil()).doHttpAction(url, null, null);
    }


    /**
     * Post方式发起网页调用，主线程安全的方式，调用返回结果字符串，结果字符串在 response 里面。失败返回NULL。
     * eg: HttpUtil.doPost("http://pa.handbbc.com/askbo", "", (response) -> { System.out.println(response); });
     */
    public static void doPost(String url, String postdata, OnHttpResultListener httpResultListener) {
        HttpUtil httpObj = new HttpUtil();
        Handler myHandler = new Handler(new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                String response = (String) msg.obj;
                httpResultListener.onHttpResult(response);
                return false;
            }
        });
        httpObj.doHttpAction(url, postdata, myHandler);
    }


    /**
     * Post同步方式发起网页调用，获取网页调用返回值。失败返回NULL。
     * eg: new Thread(() -> { System.out.println(HttpUtil.doPostSync("http://pa.handbbc.com/askbo", "")); }).start();
     */
    public static String doPostSync(String url, String postdata) {
        return (new HttpUtil()).doHttpAction(url, postdata, null);
    }



    // ================================================================================================


    /** HttpUtil内部消息交换的句柄（避开主线程安全不允许更新UI的问题） */
    private Handler myHandler;


    /**
     * HTTP请求主过程，采用OKHttp方式
     */
    private String doHttpAction(String url, String postdata, Handler oMyHandler) {
        String responseStr = null;
        myHandler = oMyHandler;
        try {
            LogUtil.log("[HttpUtil][Request] URL:" + url + "  POSTDATA:" + postdata);
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(600, TimeUnit.SECONDS).readTimeout(600, TimeUnit.SECONDS).build();                                                                //1、
            Request request = null;
            if (postdata == null) {
                //Get（PostData为空）
                request = new Request.Builder().addHeader("Connection","close").url(url).build();
            } else {
                //Post方式
                RequestBody body = FormBody.create(postdata, MediaType.parse("application/json; charset=utf-8"));
                request = new Request.Builder().url(url).post(body).build();
            }

            if (myHandler == null) {
                //同步方式
                Call call = client.newCall(request);
                Response response = call.execute();
                responseStr = response.body().string();
                LogUtil.log("[HttpUtil][Response] " + responseStr);
                response.close();
            } else {
                //异步方式
                Call call = client.newCall(request);                                                                //3、
                call.enqueue(new Callback() {                                                                            //4、
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.exception("[HttpUtil][Response] ", e);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = null;
                        myHandler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String resStr = response.body().string();
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = resStr;
                        myHandler.sendMessage(msg);
                        LogUtil.log("[HttpUtil][Response] " + resStr);
                        response.close();
                    }
                });
            }

        }catch (Exception e){
            e.printStackTrace();
            LogUtil.exception(e);
        }
        return responseStr;
    }


    /**
     * Get同步方式发起网页调用，获取网页调用返回值。失败返回NULL。
     * eg:new Thread(() -> { System.out.println(HttpUtil.doGetSync("http://pa.handbbc.com/askbo")); }).start();
     * @return
     */
    public static Bitmap doGetBitmapSync(String url) {
        return (new HttpUtil()).doGetHttpBitmap(url, null, null);
    }



    /**
     * Get异步方式发起网页调用，获取网页调用返回值。失败返回NULL。
     * @return
     */
    public static Bitmap doGetBitmap(String url,Handler handler) {
        return (new HttpUtil()).doGetHttpBitmap(url, null, handler);
    }



    /**
     * HTTP请求主过程，采用OKHttp方式返回流
     */
    private Bitmap doGetHttpBitmap(String url, String postdata, Handler oMyHandler) {
        InputStream inputStream = null;
        myHandler = oMyHandler;
        Bitmap img_data = null;
        try {
            LogUtil.log("[HttpUtil][Request] URL:" + url + "  POSTDATA:" + postdata);
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(600, TimeUnit.SECONDS).readTimeout(600, TimeUnit.SECONDS).build();                                                                //1、
            Request request = null;
            if (postdata == null) {
                //Get（PostData为空）
                request = new Request.Builder().url(url).build();
            } else {
                //Post方式
                RequestBody body = FormBody.create(postdata, MediaType.parse("application/json; charset=utf-8"));
                request = new Request.Builder().url(url).post(body).build();
            }

            if (myHandler == null) {
                //同步方式
                Call call = client.newCall(request);
                Response response = call.execute();
                inputStream = response.body().byteStream();
                img_data = BitmapFactory.decodeStream(inputStream);
                LogUtil.log("[HttpUtil][Response] " + inputStream);
                response.close();
            } else {
                //异步方式
                Call call = client.newCall(request);                                                                //3、
                call.enqueue(new Callback() {                                                                            //4、
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LogUtil.exception("[HttpUtil][Response] ", e);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = null;
                        myHandler.sendMessage(msg);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        InputStream stream = response.body().byteStream();
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = BitmapFactory.decodeStream(stream);
                        myHandler.sendMessage(msg);
                        LogUtil.log("[HttpUtil][Response] " + stream);
                        response.close();
                    }
                });
            }

        }catch (Exception e){
            LogUtil.exception(e);
        }
        return img_data;
    }

    /**
     *
     * @description: 从服务器获得一个输入流(本例是指从服务器获得一个image输入流)
     * @return
     */
    public static InputStream getInputStream(String urlPath) {
        System.out.println("开始执行操作"+urlPath);
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(urlPath);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            // 设置网络连接超时时间
            httpURLConnection.setConnectTimeout(3000);
            // 设置应用程序要从网络连接读取数据
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            int responseCode = httpURLConnection.getResponseCode();
            LogUtil.log("responseCode返回值"+responseCode);
            if (responseCode == 200) {
                // 从服务器返回一个输入流
                inputStream = httpURLConnection.getInputStream();
                System.out.println("=====图片"+inputStream);
            }else if(responseCode == 302) {
                String location = httpURLConnection.getHeaderField("Location");
                System.out.println(location);
                httpURLConnection.disconnect();
                urlPath = location;
                httpURLConnection = (HttpURLConnection) url.openConnection();
                // 设置网络连接超时时间
                httpURLConnection.setConnectTimeout(3000);
                // 设置应用程序要从网络连接读取数据
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                inputStream = httpURLConnection.getInputStream();
                System.out.println("=====1图片"+inputStream);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.log("inputStream IS Null? "+(inputStream == null));
        return inputStream;
    }

}
