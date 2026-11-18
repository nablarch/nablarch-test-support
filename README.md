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

### テストクラスへのDatabaseTestRunnerの指定

データベースを使用するテストクラスには、`@RunWith`に`nablarch.test.support.db.helper.DatabaseTestRunner`を指定します（JUnit 4のRunnerです）。
`DatabaseTestRunner`はテスト実行前に本モジュールの`db-default.xml`を読み込み、データベース接続の設定を行います。
`db-default.xml`は後述の`datasource.xml`と`db.config`をクラスパスから読み込むため、この2ファイルを差し替えることでテストに使用するデータベースを切り替えられます。
また、`DatabaseTestRunner`を指定したテストクラスでは、`VariousDbTestHelper`を使用してデータベースにアクセスできます。

```java
import nablarch.test.support.db.helper.DatabaseTestRunner;
import nablarch.test.support.db.helper.VariousDbTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DatabaseTestRunner.class)
public class SampleTest {

    @Test
    public void test() throws Exception {
        VariousDbTestHelper.createTable(SampleEntity.class);
        // ...
    }
}
```

### DB設定変更

本モジュールの以下のリソースをコピーして、テスト対象モジュールのテスト用クラスパス上（`src/test/resources`など）に同じファイル名で配置します。

- src/main/resources/datasource.xml
- src/main/resources/db.config

コピーしたファイルの記載内容を、テストに使用したいDBの情報に書き換えます。

- `db.config`: 接続情報（`db.url`、`db.user`、`db.password`、`nablarch.db.schema`）
- `datasource.xml`:
    - `dataSource`コンポーネントのJDBCドライバクラス（`driverClassName`）と初期化処理（`dbInitializer`。H2用にスキーマを作成する処理なので、使用するDBに合わせて変更または削除します）
    - `dialect`コンポーネントのクラス（使用するDBの`Dialect`実装）
    - `xaDataSource`コンポーネントのクラス（XAデータソースを使用するテストを実行する場合）

#### 自前のDialectを使用する場合

Nablarchが標準で提供していないDBなどで自前の`Dialect`を作成した場合は、コピーした`datasource.xml`の`dialect`コンポーネントのクラスを自作の`Dialect`に差し替えます。
`DatabaseTestRunner`が読み込む`db-default.xml`の`connectionFactory`は、この`dialect`コンポーネントを参照しています。

```xml
<!-- datasource.xml（変更前） -->
<component name="dialect" class="nablarch.core.db.dialect.H2Dialect" />

<!-- datasource.xml（変更後） -->
<component name="dialect" class="com.example.MyDialect" />
```

### 特定のDBでのみテストを実行する

`DatabaseTestRunner`を指定したテストクラスやテストメソッドに`nablarch.test.support.db.helper.TargetDb`を付けると、
使用しているDBの種類に応じてテストの実行・スキップを切り替えられます。

- `include`: 指定したDBのときだけ実行します。
- `exclude`: 指定したDBのときは実行しません。`include`と同時に指定した場合は`exclude`が優先されます。

DBの種類は、`db.config`の`db.url`が各DBのJDBC URLの接頭辞（`jdbc:oracle`、`jdbc:postgresql`、`jdbc:db2`、`jdbc:sqlserver`、`jdbc:mysql`、`jdbc:h2`）で始まるかどうかで判定します。
対象外と判定されたテストはスキップ（ignored）扱いになり、テストクラスに付けた場合はそのクラスのテスト全体がスキップされます。

```java
@RunWith(DatabaseTestRunner.class)
@TargetDb(exclude = TargetDb.Db.H2) // H2以外のDBでのみ実行する
public class SampleTest {

    @Test
    @TargetDb(include = {TargetDb.Db.ORACLE, TargetDb.Db.POSTGRE_SQL}) // OracleとPostgreSQLでのみ実行する
    public void test() throws Exception {
        // ...
    }
}
```

### JDBCドライバのインストール

テストしたいデータベースのJDBCドライバをローカルリポジトリへインストールします。

```
mvn install:install-file -Dfile=<ファイル名> -DgroupId=<グループID> -DartifactId=<アーティファクトID> -Dversion=<バージョン> -Dpackaging=jar
```

### JDBCドライバのdependency設定

pom.xmlのdependenciesセクションに、先にインストールしたJDBCドライバをtestスコープで追記します。