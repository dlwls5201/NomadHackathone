package com.example.leejinseong.nomadhackathone.ui.main;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Window;
import android.widget.TextView;

import com.example.leejinseong.nomadhackathone.R;

/**
 * Created by leejinseong on 2018. 3. 18..
 */

public class MainMessageDialog extends Dialog {

    private String message;

    private TextView tvDialogMainMessage;

    public MainMessageDialog(@NonNull Context context) {
        super(context);
    }

    public MainMessageDialog(@NonNull Context context, String message) {
        super(context);

        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.dialog_main_message);

        tvDialogMainMessage = findViewById(R.id.tvDialogMainMessage);

        if(TextUtils.isEmpty(message)) {
            message = "Are u crazy?\n you should save money";
            tvDialogMainMessage.setText(message);
        } else {
            tvDialogMainMessage.setText(message);
        }
    }
}
