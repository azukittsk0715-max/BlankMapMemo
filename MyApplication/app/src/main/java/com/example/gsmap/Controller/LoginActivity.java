package com.example.gsmap.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.gsmap.Controller.MainActivity;
import com.example.gsmap.R;

public class LoginActivity extends AppCompatActivity {

    private EditText editWalkerId;
    private EditText editPassword;
    private EditText editPasswordConfirm;
    private Button btnLogin;
    private Button btnRegister;
    private TextView textMessage;

    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editWalkerId = findViewById(R.id.editWalkerId);
        editPassword = findViewById(R.id.editPassword);
        editPasswordConfirm = findViewById(R.id.editPasswordConfirm);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        textMessage = findViewById(R.id.textMessage);

        authController = new AuthController();

        btnLogin.setOnClickListener(v -> onLoginClicked());
        btnRegister.setOnClickListener(v -> onRegisterClicked());
    }

    // ログインボタン押下時の処理
    private void onLoginClicked() {
        String walkerId = editWalkerId.getText().toString();
        String password = editPassword.getText().toString();

        // E1〜E3：入力チェック（ログイン時は確認欄をチェックしない）
        if (!checkInput(walkerId, password)) {
            return;
        }

        showMessage("ログイン中...", false);
        setButtonsEnabled(false);

        new Thread(() -> {
            int result = authController.AuthenticateUser(walkerId, password);
            runOnUiThread(() -> {
                setButtonsEnabled(true);
                handleAuthResult(result, false);
            });
        }).start();
    }

    // 新規登録ボタン押下時の処理
    private void onRegisterClicked() {
        String walkerId = editWalkerId.getText().toString();
        String password = editPassword.getText().toString();
        String passwordConfirm = editPasswordConfirm.getText().toString();

        // E1〜E3：入力チェック
        if (!checkInput(walkerId, password)) {
            return;
        }

        // 新規登録時のみ：パスワード確認欄のチェック
        if (!password.equals(passwordConfirm)) {
            showMessage("パスワードが一致しません。", true);
            return;
        }

        showMessage("登録中...", false);
        setButtonsEnabled(false);

        new Thread(() -> {
            int result = authController.RegisterUser(walkerId, password);
            runOnUiThread(() -> {
                setButtonsEnabled(true);
                handleAuthResult(result, true);
            });
        }).start();
    }

    // 入力チェック（E1, E2, E3）
    private boolean checkInput(String walkerId, String password) {
        if (walkerId.isEmpty()) {
            showMessage("ウォーカーIDを入力してください。", true); // E1
            return false;
        }
        if (password.isEmpty()) {
            showMessage("パスワードを入力してください。", true); // E2
            return false;
        }
        if (password.length() < 8) {
            showMessage("パスワードは8文字以上で入力してください。", true); // E3
            return false;
        }
        return true;
    }

    // 認証/登録結果の処理（E4, E5）
    private void handleAuthResult(int result, boolean isRegister) {
        if (result == 1) {
            // 成功
            if (isRegister) {
                showMessage("登録が完了しました。ログインしてください。", false);
                editPassword.setText("");
                editPasswordConfirm.setText("");
            } else {
                // ログイン成功 → ホーム画面へ遷移
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (result == 0) {
            // 認証/登録失敗
            if (isRegister) {
                showMessage("このウォーカーIDは既に使用されています。", true);
            } else {
                showMessage("IDまたはパスワードが正しくありません。", true); // E4
            }
        } else {
            // result == -1：通信エラー
            showMessage("通信エラーが発生しました。", true); // E5
        }
    }

    // メッセージ表示（isError: true=赤色, false=緑色）
    private void showMessage(String message, boolean isError) {
        textMessage.setText(message);
        int color = isError
                ? ContextCompat.getColor(this, android.R.color.holo_red_dark)
                : ContextCompat.getColor(this, android.R.color.holo_green_dark);
        textMessage.setTextColor(color);
    }

    // ボタンの有効/無効切り替え（連打防止）
    private void setButtonsEnabled(boolean enabled) {
        btnLogin.setEnabled(enabled);
        btnRegister.setEnabled(enabled);
    }
}