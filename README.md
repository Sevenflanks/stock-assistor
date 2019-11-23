# stock-assistor
- 這是一個用來爬股票行情資訊的工具，是以JavaWebApp為基礎撰寫
- 目前仍在開發中，尚未有完整功能
- TODOs:
    1. [X] 爬取上市公司資訊
    2. [X] 爬取上市股票行情
    3. [X] 爬取上市股票融券餘額
    4. [X] 爬取上櫃公司資訊
    5. [X] 爬取上櫃股票行情
    6. [X] 爬取上櫃股票融資餘額
    7. [X] WebUI-資料爬取控制 `(early release)`
    8. [ ] WebUI-資料視覺化 `pending`
    9. [X] WebUI-資料分析
    10. [ ] WebUI-個人介面(註記, 隱藏, 分類)
    11. [ ] WebUI-資料顯示優化(當日行情)
    12. [X] 訊號-Sng004(篩選價格)

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
