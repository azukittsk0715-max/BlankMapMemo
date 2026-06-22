// 6/22榎本
// 【PostgreSQL版（枠のみ）】ウォーカー情報管理部（C6）
// サーバAPI経由でPostgreSQLにアクセスする版。
// サーバ側のAPIが未完成のため、現在は枠（メソッドの形）だけ用意している。
// APIが完成し次第、各メソッドの中身（HTTP通信処理）を実装する。
package com.example.gsmap.Model;

public class WalkerModel {

    // ① 新規登録：新しいウォーカーをサーバ経由でDBに保存する（RegisterWalkerInfo）
    // 成功したら true、失敗したら false を返す（予定）
    public boolean registerWalker(String walkerId, String password) {
        // TODO: サーバAPIに登録リクエストを送る処理を実装する
        return false;
    }

    // ② 情報取得：walker_idでサーバに問い合わせ、パスワードを取得する（GetWalkerInfo）
    // 見つかればパスワード文字列、見つからなければ null を返す（予定）
    public String getWalkerPassword(String walkerId) {
        // TODO: サーバAPIに問い合わせリクエストを送る処理を実装する
        return null;
    }

    // ③ 照合：入力されたIDとパスワードが正しいか確かめる（VerifyPassword）
    // 一致すれば true、間違っていれば false を返す（予定）
    public boolean verifyPassword(String walkerId, String inputPassword) {
        // TODO: サーバAPIに認証リクエストを送る処理を実装する
        return false;
    }
}