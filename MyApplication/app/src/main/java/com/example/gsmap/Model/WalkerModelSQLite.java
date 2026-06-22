// 6/22榎本
// 【簡易版】ウォーカー情報管理部（C6）
// 本来はサーバ＋DBに保存する設計だが、まずは動く形を優先し、
// 端末内のSQLiteに保存する簡易版として実装している。
// サーバ環境（DB・API）が整い次第、この保存部分をサーバ通信に差し替える予定です

//簡易版！！使うかはわからない

package com.example.gsmap.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WalkerModelSQLite extends SQLiteOpenHelper {

    // データベースの名前
    private static final String DB_NAME = "walker.db";
    // データベースのバージョン
    private static final int DB_VERSION = 1;

    // テーブル名と列名（定数としてまとめておく）
    private static final String TABLE_NAME = "walkers";
    private static final String COL_ID = "id";
    private static final String COL_WALKER_ID = "walker_id";
    private static final String COL_PASSWORD = "password";

    // コンストラクタ（このクラスが作られるとき最初に呼ばれる）
    public WalkerModel(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // DBが初めて作られるとき、自動で1回だけ呼ばれる（テーブルを作る場所）
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_WALKER_ID + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL"
                + ")";
        db.execSQL(createTable);
    }

    // DBのバージョンが上がったとき呼ばれる（今は作り直すだけ）
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    // ① 新規登録：新しいウォーカーをDBに保存する（RegisterWalkerInfo）
    // 成功したら true、失敗（IDが重複など）したら false を返す
    public boolean registerWalker(String walkerId, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_WALKER_ID, walkerId);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        // insertが失敗すると -1 が返ってくる。それ以外なら成功
        return result != -1;
    }


    // ② 情報取得：walker_idで検索し、保存されているパスワードを返す（GetWalkerInfo）
    // 見つかればパスワード文字列を返す。見つからなければ null を返す
    public String getWalkerPassword(String walkerId) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,                       // どのテーブルから
                new String[]{COL_PASSWORD},       // どの列がほしいか（password列）
                COL_WALKER_ID + " = ?",           // 検索条件（walker_id が）
                new String[]{walkerId},           // 条件の「?」に入れる値
                null, null, null
        );

        String password = null;
        if (cursor.moveToFirst()) {
            password = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return password;
    }


    // ③ 照合：入力されたIDとパスワードが正しいか確かめる（VerifyPassword）
    // 一致すれば true、間違っていれば false を返す
    public boolean verifyPassword(String walkerId, String inputPassword) {
        // ②の関数を使って、保存されているパスワードを取り出す
        String savedPassword = getWalkerPassword(walkerId);

        // そのIDが存在しなかった場合（null）は、照合失敗
        if (savedPassword == null) {
            return false;
        }

        // 保存されているパスワードと、入力されたパスワードを比べる
        return savedPassword.equals(inputPassword);
    }


}