// 6/25榎本
// C2 認証処理部
// ログインと新規登録の司令塔。WalkerModel(C6)を呼び出して結果を返す。

package com.example.gsmap.Controller;

import com.example.gsmap.Model.WalkerModel;

public class AuthController {

    private WalkerModel walkerModel;

    // コンストラクタ
    public AuthController() {
        this.walkerModel = new WalkerModel();
    }

    // ログイン認証
    // 戻り値：1=成功、0=失敗、-1=エラー
    public int AuthenticateUser(String walkerId, String password) {
        try {
            boolean result = walkerModel.verifyPassword(walkerId, password);
            return result ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // 新規登録
    // 戻り値：1=成功、0=失敗、-1=エラー
    public int RegisterUser(String walkerId, String password) {
        try {
            boolean result = walkerModel.registerWalker(walkerId, password);
            return result ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}