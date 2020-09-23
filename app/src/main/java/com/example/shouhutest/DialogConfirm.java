package com.example.shouhutest;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

/*
确认对话框，包括两种用法：

1）静态方式，eg：
DialogConfirm.show(this, "确认执行", "本操作需要耗费数分钟时间", "立即执行", "我再想想", ()->{  LogUtil.log("========= 执行中 ========= ");  });
DialogConfirm.show(this, "确认执行", "本操作需要耗费数分钟时间", "立即执行", "我再想想", ()->{  LogUtil.log("========= 执行中 ========= ");  } , ()->{  LogUtil.log("========= 取消了 ========= ");  });

2）实例方式，eg：
DialogConfirm confirm = new DialogConfirm(this, "确认执行", "本操作需要耗费数分钟事件", "立即执行", "我再想想");
confirm.setYesBtnClickListener(()->{
    LogUtil.log("========= 执行中 ========= ");
    confirm.dismiss();
});
confirm.setNoBtnClickListener(()->{
    LogUtil.log("========= 取消了 ========= ");
    confirm.dismiss();
});
confirm.show();
 */
public class DialogConfirm extends Dialog {

    private TextView title;
    private TextView message;
    private Button noBtn, yesBtn;
    private String[] texts = new String[4];
    private YesBtnClickListener ybcListener;
    private NoBtnClickListener nbcListener;
    private boolean autoCloseDialog = false;

    /**
     * 默认构造函数。确认退出，确认，取消。
     */
    public DialogConfirm(@NonNull Context context) {
        super(context);
        this.texts[0] = "确认操作";
        this.texts[1] = "";
        this.texts[2] = "确认";
        this.texts[3] = "取消";
    }


    /**
     * 自定义显示内容构造函数，标题，提示语，确认语，取消语
     */
    public DialogConfirm(@NonNull Context context, String title, String message, String yes, String no) {
        super(context);
        this.texts[0] = title;
        this.texts[1] = message;
        this.texts[2] = yes;
        this.texts[3] = no;
    }


    /**
     * 绘制对话框界面
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.util_dialogconfirm);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);

        //初始化界面
        noBtn = findViewById(R.id.util_dialogconfirm_no);
        yesBtn = findViewById(R.id.util_dialogconfirm_yes);
        title = findViewById(R.id.util_dialogconfirm_title);
        message = findViewById(R.id.util_dialogconfirm_message);

        //设置内容
        title.setText(texts[0]);
        message.setText(texts[1]);
        yesBtn.setText(texts[2]);
        noBtn.setText(texts[3]);

        //绑定点击确定按钮事件
        yesBtn.setOnClickListener((v) -> {
            if (ybcListener != null) {
                ybcListener.onClickYesBtn();
                if (autoCloseDialog) this.dismiss();
            }
        });

        //绑定点击取消按钮事件
        noBtn.setOnClickListener((v) -> {
            if (nbcListener != null) {
                nbcListener.onClickNoBtn();
                if (autoCloseDialog) this.dismiss();
            }
        });
    }


    /**
     * 附加确认点击事件
     */
    public void setYesBtnClickListener(YesBtnClickListener yesBtnClickListener) {
        this.ybcListener = yesBtnClickListener;
    }


    /**
     * 确认点击事件接口
     */
    public interface YesBtnClickListener {
        //接口中确认按钮点击的回调入口
        void onClickYesBtn();
    }


    /**
     * 附加取消点击事件
     */
    public void setNoBtnClickListener(NoBtnClickListener noBtnClickListener) {
        this.nbcListener = noBtnClickListener;
    }


    /**
     * 取消点击事件接口
     */
    public interface NoBtnClickListener {
        //接口中确认按钮点击的回调入口
        void onClickNoBtn();
    }


    /**
     * 直接显示确认对话框。参数包括：上下文，标题，提示语，确认语，取消语，确认事件，取消事件
     * eg: DialogConfirm.show(this, "确认执行", "本操作需要耗费数分钟时间", "立即执行", "我再想想", ()->{  LogUtil.log("========= 执行中 ========= ");  } , ()->{  LogUtil.log("========= 取消了 ========= ");  });
     */
    public static void show(Context context, String title, String message, String yes, String no, DialogConfirm.YesBtnClickListener yesBtnClickListener, DialogConfirm.NoBtnClickListener noBtnClickListener) {
        DialogConfirm dialog = new DialogConfirm(context, title, message, yes, no);
        dialog.setYesBtnClickListener(yesBtnClickListener);
        if (noBtnClickListener == null)
            dialog.setNoBtnClickListener(() -> dialog.dismiss());
        else
            dialog.setNoBtnClickListener(noBtnClickListener);
        dialog.autoCloseDialog = true;
        dialog.show();
    }

    /**
     * 直接显示确认对话框。参数包括：上下文，标题，提示语，确认语，取消语，确认事件
     * eg: DialogConfirm.show(this, "确认执行", "本操作需要耗费数分钟时间", "立即执行", "我再想想", ()->{  LogUtil.log("========= 执行中 ========= ");  });
     */
    public static void show(Context context, String title, String message, String yes, String no, DialogConfirm.YesBtnClickListener yesBtnClickListener) {
        show(context, title, message, yes, no, yesBtnClickListener, null);
    }
}
