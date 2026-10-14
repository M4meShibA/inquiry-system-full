# 起動用スクリプトのひな形
#
# 使い方
#   1. このファイルを "start.ps1" という名前でコピーする
#   2. start.ps1 を開いて、下の5つの値を自分のものに書き換える
#   3. PowerShell でこのフォルダを開いて  .\start.ps1  を実行する
#
# 注意
#   - start.ps1 にはキーやパスワードが入ります。GitHubには上げないでください
#     (.gitignore で start.ps1 は除外済み。この start.example.ps1 は上げてOK)
#   - 「スクリプトの実行が無効」と出たときは、次のどちらかを実行します
#       Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
#       powershell -ExecutionPolicy Bypass -File .\start.ps1

Set-Location $PSScriptRoot

$env:ADMIN_USERNAME = "好きなユーザー名"
$env:ADMIN_PASSWORD = "長めのパスワード"
$env:GEMINI_API_KEY = "GeminiのAPIキー"
$env:MAIL_USERNAME  = "あなたのGmailアドレス"
$env:MAIL_PASSWORD  = "アプリパスワード(空白なし16文字)"

# モデル名を変えたいときだけ、次の行の先頭の # を消して書き換える
# $env:AI_MODEL = "gemini-3.7-flash"

# ひな形のまま起動してしまわないためのチェック
$placeholders = @(
    "好きなユーザー名",
    "長めのパスワード",
    "GeminiのAPIキー",
    "あなたのGmailアドレス",
    "アプリパスワード(空白なし16文字)"
)
foreach ($name in "ADMIN_USERNAME", "ADMIN_PASSWORD", "GEMINI_API_KEY", "MAIL_USERNAME", "MAIL_PASSWORD") {
    $value = (Get-Item "Env:$name").Value
    if ([string]::IsNullOrWhiteSpace($value) -or ($placeholders -contains $value)) {
        Write-Host "$name がまだ設定されていません。start.ps1 を開いて書き換えてください。" -ForegroundColor Red
        exit 1
    }
}

mvn spring-boot:run
