nablarch-test-support
======================

Nablarchモジュールのテストで必要な共通処理をまとめています。

既存のテストで使用しているnablarch-all-test及びnablarch-toolboxの処理を切り出しています。
テストを移行する際には本モジュールを参照するようにし、足りなければ処理を新しく切り出して追加してください。
その場合、切り出し元には処理を残し、本モジュールに追加したクラスのパッケージを適宜変更してください。

    本モジュールに切り出したクラスの一部を挙げます。
    パッケージ名を変更する際に参考にして下さい。
    
    test.support.db.helper.DatabaseTestRunner -> nablarch.test.support.db.helper.DatabaseTestRunner
    nablarch.tool.Hereis -> nablarch.test.support.tool.Hereis

## 各種DBでのテスト

Nablarchのテストを、デフォルトのH2以外のデータベースでテストをしたい場合の手順を記載します。

### DB設定変更

本モジュールの以下のリソースをコピーして、テスト対象モジュールに同じように配置します。

- src/main/resources/datasource.xml
- src/main/resources/db.config

上記ファイルの記載内容を、テストに使用したいDBの情報に書き換えます。

### JDBCドライバのインストール

テストしたいデータベースのJDBCドライバをローカルリポジトリへインストールします。

```
mvn install:install-file -Dfile=<ファイル名> -DgroupId=<グループID> -DartifactId=<アーティファクトID> -Dversion=<バージョン> -Dpackaging=jar
```

### JDBCドライバのdependency設定

pom.xmlのdependenciesセクションに、先にインストールしたJDBCドライバをtestスコープで追記します。