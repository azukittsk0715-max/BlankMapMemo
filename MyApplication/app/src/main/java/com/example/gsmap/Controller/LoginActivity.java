package com.example.gsmap.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gsmap.MainActivity;
import com.example.gsmap.R;

public class LoginActivity extends AppCompatActivity {

    private EditText editWalkerId;
    private EditText editPassword;
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

        // E1〜E3：入力チェック
        if (!checkInput(walkerId, password)) {
            return;
        }

        textMessage.setText("ログイン中...");

        // 通信を伴うため別スレッドで実行
        new Thread(() -> {
            int result = authController.AuthenticateUser(walkerId, password);
            runOnUiThread(() -> handleAuthResult(result, false));
        }).start();
    }

    // 新規登録ボタン押下時の処理
    private void onRegisterClicked() {
        String walkerId = editWalkerId.getText().toString();
        String password = editPassword.getText().toString();

        // E1〜E3：入力チェック
        if (!checkInput(walkerId, password)) {
            return;
        }

        textMessage.setText("登録中...");

        new Thread(() -> {
            int result = authController.RegisterUser(walkerId, password);
            runOnUiThread(() -> handleAuthResult(result, true));
        }).start();
    }

    // 入力チェック（E1, E2, E3）
    private boolean checkInput(String walkerId, String password) {
        if (walkerId.isEmpty()) {
            textMessage.setText("ウォーカーIDを入力してください。"); // E1
            return false;
        }
        if (password.isEmpty()) {
            textMessage.setText("パスワードを入力してください。"); // E2
            return false;
        }
        if (password.length() < 8) {
            textMessage.setText("パスワードは8文字以上で入力してください。"); // E3
            return false;
        }
        return true;
    }

    // 認証/登録結果の処理（E4, E5）
    private void handleAuthResult(int result, boolean isRegister) {
        if (result == 1) {
            // 成功
            if (isRegister) {
                textMessage.setText("登録が完了しました。ログインしてください。");
            } else {
                // ログイン成功 → ホーム画面へ遷移
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (result == 0) {
            // 認証/登録失敗
            if (isRegister) {
                textMessage.setText("このウォーカーIDは既に使用されています。");
            } else {
                textMessage.setText("IDまたはパスワードが正しくありません。"); // E4
            }
        } else {
            // result == -1：通信エラー
            textMessage.setText("通信エラーが発生しました。"); // E5
        }
    }
}