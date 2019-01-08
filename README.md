# stock-assistor
- 這是一個用來爬股票行情資訊的工具，是以JavaWebApp為基礎撰寫
- 目前仍在開發中，尚未有完整功能
- TODOs:
    1. <s>爬取上市公司資訊</s>`(done)`
    2. <s>爬取上市股票行情</s>`(done)`
    3. <s>爬取上市股票融券餘額</s>`(done)`
    4. <s>爬取上櫃公司資訊</s>`(done)`
    5. <s>爬取上櫃股票行情</s>`(done)`
    6. <s>爬取上櫃股票融資餘額</s>`(done)`
    7. <s>WebUI-資料爬取控制</s>`(early release)`
    8. WebUI-資料視覺化 `pending`
    9. WebUI-資料分析
        - Sng001 `(testing)`
        - Sng002 `(testing)`
        - Sng003 `(request checking)`

## Env
- 基本需求環境
    1. Adopt OpenJDK 11
    2. Maven
- 本系統嵌入環境
    1. Tomcat
    2. H2 Database(in memory mode)

## QuickStart
```
# 環境
# 必須: JDK8+, 建議: JDK11
$ https://adoptopenjdk.net/
# 必須: 環境變數: JAVA_HOME & Java runtime
# 必須: Maven3+, 建議: Maven3.6.0
$ https://maven.apache.org/download.cgi
# 必須: 環境變數: MAVEN_HOME & Maven runtime

# 啟動
$ mvn spring-boot:run

# 爬取資料WebUI
# 若是同一個日期多次爬取，則會優先使用本地端的檔案(詳見data/{date})
$ http://localhost:8080/static/data_init.html

```
