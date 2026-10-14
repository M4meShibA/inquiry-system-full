# 問い合わせ対応システム

Java / Spring Boot / Thymeleaf / JPA(H2)で作った、問い合わせ管理アプリです。
AI(Gemini API)による返信文の下書き生成ボタン付き。

## 必要なもの

- JDK 17 以上
- Maven 3.9 以上(IntelliJ IDEA / Eclipse / VS Code の Maven 機能でも可)

## 起動方法

```
mvn spring-boot:run
```

起動後、ブラウザで http://localhost:8080/ を開きます。

IDEから動かす場合は、`InquiryApplication` の `main` メソッドを実行してください。

## AI下書きを使うには

APIキーを環境変数 `GEMINI_API_KEY` に設定してから起動します。
キーはコードや properties に書かず、GitHub にも上げないでください。

- Windows (PowerShell): `$env:GEMINI_API_KEY="あなたのキー"`
- Windows (cmd): `set GEMINI_API_KEY=あなたのキー`
- macOS / Linux: `export GEMINI_API_KEY=あなたのキー`

キーが無くても、登録・一覧・詳細・ステータス変更・返信保存は動きます
(下書きボタンだけエラーメッセージが出ます)。
使うモデルは `application.properties` の `ai.model` で変更できます。

## 画面のURL

| URL | 対象 | 内容 |
|---|---|---|
| http://localhost:8080/ | お客様 | トップページ(問い合わせの案内) |
| http://localhost:8080/contact | お客様 | 問い合わせフォーム |
| http://localhost:8080/admin | 担当者(要ログイン) | 問い合わせ一覧(入口) |
| http://localhost:8080/admin/inquiries/{id} | 担当者(要ログイン) | 詳細・返信・削除 |

お客様にはトップページか問い合わせフォームのURLを伝えます。
動作確認では、お客様側は**シークレットウィンドウ**で開くと、ログイン状態と混ざらずに試せます。

## 起動スクリプト(Windows / PowerShell)

環境変数が5つあるので、毎回打たなくて済むように、ひな形 `start.example.ps1` を用意しています。

1. `start.example.ps1` を `start.ps1` という名前でコピーする
2. `start.ps1` を開いて、5つの値(管理者のユーザー名とパスワード、Gemini APIキー、Gmailアドレスとアプリパスワード)を書き換える
3. PowerShell でこのフォルダを開き、`.\start.ps1` を実行する

`start.ps1` はキーやパスワードを含むため、`.gitignore` でGitHubに上がらないようにしてあります。
ひな形のまま(未設定のまま)だと、チェックに引っかかって起動しません。

## ログイン(管理者)

担当者用の画面(一覧・詳細・返信・削除)はログインが必要です。
お客様向けのトップページ・問い合わせフォーム・受付完了ページは、ログイン不要で使えます。

**管理者のユーザー名とパスワードは環境変数で設定します。未設定だとアプリは起動しません。**

- PowerShell: `$env:ADMIN_USERNAME="好きなユーザー名"` と `$env:ADMIN_PASSWORD="長めのパスワード"`
- cmd: `set ADMIN_USERNAME=...` と `set ADMIN_PASSWORD=...`
- macOS / Linux: `export ADMIN_USERNAME=...` と `export ADMIN_PASSWORD=...`

パスワードは推測されにくい長いものにしてください(ほかのサービスと使い回さない)。
担当者は http://localhost:8080/admin を開くと、ログイン画面に移ります。

## 問い合わせの削除

詳細画面の一番下の「削除する」で、その問い合わせと対応履歴をまとめて削除できます(元に戻せません)。

## AI下書きのリトライ

AI APIが混雑している(HTTP 429 / 500 / 502 / 503 / 504)ときは、2秒・4秒と間隔を空けて、最大3回まで自動でやり直します。

## メール送信を使うには(Gmail)

詳細画面の「保存してメール送信」で、お客様のメールアドレス宛てに返信が届きます。
(「返信を保存(送信しない)」は、履歴に残すだけでメールは送りません)

1. Googleアカウントで **2段階認証** をオンにする
2. Googleアカウントの設定(セキュリティ → 2段階認証プロセス)から **アプリパスワード** を作る(16文字。空白は取り除いてOK)
3. 環境変数を設定してから起動する
   - PowerShell: `$env:MAIL_USERNAME="あなたのGmailアドレス"` と `$env:MAIL_PASSWORD="アプリパスワード"`
   - cmd: `set MAIL_USERNAME=...` と `set MAIL_PASSWORD=...`
   - macOS / Linux: `export MAIL_USERNAME=...` と `export MAIL_PASSWORD=...`

注意:
- 送信元は、ログインに使うGmailアドレスになります
- 最初のテストは、**自分のメールアドレスで問い合わせを登録して**、自分宛てに届くか確認してください
- アプリパスワードもAPIキーと同じ扱いです。コードに書かず、GitHubにも上げないでください
- 会社や学校のGoogle Workspaceアカウントは、管理者の設定でアプリパスワードが使えないことがあります
- メール送信に失敗したときは、返信は保存されず、入力した文章は画面に残ります

## データについて

H2 のファイルDBを使うため、データは `./data/` に保存され、再起動しても残ります。
セキュリティのため、H2コンソール(DBを直接見る画面)は無効にしています。

## フォルダ構成

```
src/main/java/com/example/inquiry/
  model/       Inquiry, Reply, InquiryStatus
  form/        InquiryForm(入力チェック付き)
  repository/  InquiryRepository
  service/     InquiryService, DraftService(AI呼び出し)
  controller/  InquiryController, HomeController
src/main/resources/
  templates/inquiries/  list.html, form.html, detail.html
  static/style.css
docs/design.md  設計メモ(構成・ER図・画面一覧)
```
