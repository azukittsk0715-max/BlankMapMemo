package com.example.gsmap.Model.PinModel;

import android.content.Context;
import android.widget.Toast;

public class PinErrorHandler {

    public static void showError(Context context, int code) {

        String message;

        switch (code) {

            case 1:
                message = "ピン位置を選択してください。";
                break;

            case 2:
                message = "メモは100文字以内で入力してください。";
                break;

            case 3:
                message = "ユーザー情報が取得できません。";
                break;

            case 4:
                message = "通信エラーが発生しました。";
                break;

            case 5:
                message = "ピンの登録に失敗しました。";
                break;

            default:
                message = "不明なエラーです。";
                break;
        }

        Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}