package com.example.shouhutest.Util;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *  日志相关工具类。包含：log, debug, exception ; exception。
 *  日志可以输出到 文件，LogCat，正式发布的时候可以关闭。Debug日志多一些，Info少一些（）
 */
public class LogUtil {

    /**
     * 打印debug信息到Logcat和文件中
     * eg: LogUtil.debug("hello world");
     */
//    public static void debug(String msg) {
//        if (MyApplication.LogUtil_DebugFlag) {
//            Log.d(MyApplication.LogUtil_TAG, msg);
//            writeLogFile("debug", msg);
//        }
//    }


    /**
     * 打印log信息到Logcat
     * eg: LogUtil.log("hello world");
     */
    public static void log(String msg) {
        Log.i("LogUtil_TAG", msg);
        writeLogFile("log", msg);
    }



    /**
     * 保存exception信息
     * eg:LogUtil.exception(e);
     */
    public static void exception(Exception e) {
        exception("", e);
    }



    /**
     * 保存exception信息
     * eg:LogUtil.exception(msg);
     */
    public static void exception(String msg) {
        exception(msg,null);
    }



    /**
     * 保存exception信息
     * eg:LogUtil.exception("磁盘读写异常", e);
     */
    public static void exception(String msg, Exception e) {
        if(e!=null){
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            msg = msg+"\n"+sw.toString();
        }
        Log.e("LogUtil_TAG", msg);
        writeLogFile("exception", msg);
    }



    /**
     * 将日志写入日志文件中
     */
    private static void writeLogFile(String logtype, String msg){
        //开启线程，写入日志文件，防止堵塞
//        if(MyApplication.LogUtil_SaveLogFile){
//            //写log日志
//            String logfilename = getLogDataFileFullName("log");
//            IOUtil.writeTextFile(logfilename, "["+DataUtil.getNowTimeString()+"]" +"["+logtype+"]"+ msg + "\n", true);
//            //写exception日志
//            if(logtype.equals("exception")) {
//                String exceptionfilename = getLogDataFileFullName("exception");
//                IOUtil.writeTextFile(exceptionfilename, "[" + DataUtil.getNowTimeString() + "]" + "[" + logtype + "]" + msg + "\n", true);
//            }
//        }
    }


    /**
     * 获得（创建）内部sd卡的日志存储全文件名 “/data/data/[APP_APPName]/[LogUtil_SaveLogDir]/*.log”。
     * 如：/data/data/com.hbbc.sjk/Log/exception20150801.log
     */
 /*   private static String getLogDataFileFullName(String filename){
        File appDataBaseDir = MyApplication.context.getDir(MyApplication.LogUtil_SaveLogDirName, 0);
        File filePath = new File(appDataBaseDir + File.separator + filename + DataUtil.getNowDateString() + ".log");
        return filePath.toString();
    }*/


    /**
     * 获取日志内容字符串。
     * 若 onlyException=true，则只获取exception信息，否则，获取所有日志信息。
     * 若 lastestLogSize=-1 表示返回所有日志内容，否则，返回 lastestLogSize 指定大小的最新的日志内容字符串。
     */
//    public static String getLogContent(boolean onlyException, int lastestLogSize) {
//        String logContent = "";
//        try {
//            //读取所有日志文件内容
//            if(onlyException)
//                logContent = IOUtil.readTextFile(getLogDataFileFullName("exception"));
//            else
//                logContent = IOUtil.readTextFile(getLogDataFileFullName("log"));
//
//            //返回日志
//            if(lastestLogSize!=-1){
//                int size = logContent.length();
//                if (size > lastestLogSize) logContent = logContent.substring(size - lastestLogSize);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return logContent;
//    }


}
