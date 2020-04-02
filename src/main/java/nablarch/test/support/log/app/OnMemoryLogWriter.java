package nablarch.test.support.log.app;

import nablarch.core.log.basic.LogWriterSupport;
import nablarch.core.util.Builder;

import java.util.*;

public class OnMemoryLogWriter extends LogWriterSupport {
    
    private static final Map<String, List<String>> messagesMap = new HashMap<String, List<String>>();
    
    public static void clear() {
        messagesMap.clear();
    }

    /**
     * メモリ中のログを取得する。
     *
     *  <p>
     *  補足：
     *  本メソッドは同期化している。<br />
     *  テスト中で別プロセスを生成してテストする場合、プロセス生成側がログを取得するタイミングが悪いと、
     *  新しく空のログを作成してしまうため。
     *  </p>
     *
     * @param name ログ名
     * @return ログ
     */
    public static synchronized List<String> getMessages(String name) {
        if (!messagesMap.containsKey(name)) {
            messagesMap.put(name, Collections.synchronizedList(new ArrayList<String>()));
        }
        return messagesMap.get(name);
    }


    public static void assertLogContains(String name, String... expected) {
        List<String> origExpected = Arrays.asList(expected);

        // まだ発見されていない期待ログ
        Set<String> expectedRest = new HashSet<String>(origExpected);

        List<String> actualLogs = OnMemoryLogWriter.getMessages(name);

        for (String actualLog : actualLogs) {
            Set<String> ok = new HashSet<String>();
            for (String expectedLog : expectedRest) {
                if (actualLog.contains(expectedLog)) {
                    ok.add(expectedLog);
                }
            }
            // 出現した文言を消し込み
            expectedRest.removeAll(ok);
            ok.clear();
        }
        // 期待したログ全てが、実際のログに含まれていること
        if (!expectedRest.isEmpty()) {
            throw new AssertionError(Builder.concat(
                    "expected log not found. \n",
                    "expected = ", origExpected.toString(), "\n",
                    "actual   = ", actualLogs.toString())
            );
        }
    }


    protected void onWrite(String formattedMessage) {
        if (formattedMessage.contains("initialized.")) {
            return;
        }
        getMessages(getName()).add(formattedMessage);
    }

    protected void onTerminate() {
        getMessages(getName()).add("@@@END@@@");
    }
}
