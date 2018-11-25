# stock-assistor
- 這是一個用來爬股票行情資訊的工具，是以JavaWebApp為基礎撰寫
- 目前仍在開發中，尚未有完整功能
- TODOs:
    1. <s>爬取上市公司資訊</s>`(done)`
    2. <s>爬取上市股票行情</s>`(done)`
    3. 爬取上市股票融資餘額
    4. 爬取上櫃公司資訊
    5. 爬取上櫃股票行情
    6. 爬取上櫃股票融資餘額
    7. WebUI-資料爬取控制
    8. WebUI-資料視覺化
    9. WebUI-資料分析

## Env
- 基本需求環境
    1. Adopt OpenJDK 11
    2. Maven
- 本系統嵌入環境
    1. Tomcat
    2. H2 Database(in memory mode)

## QuickStart
```
# 啟動
$ mvn spring-boot:run

# 爬取資料(你可以更改日期參數來改變要爬取的資料日期)
$ http://localhost:8080/api/stock/init/2018-11-21

# 觀看資料(目前尚未有UI，因此請直接利用H2的WebUI訪問DB)
$ http://localhost:8080/h2/
    JDBC URL: jdbc:h2:mem:test
    User Name: sa
```
