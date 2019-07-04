# lcBackstage
the backstage of lc.

## java 11.0.1 + spring boot 2.1.3 + redis 4.0.2 + tomcat 9 + mysql 8.0.15
```
controller两种接口选择 ： 模板渲染 json（偏向）

app使用后，web使用前

man界面前全原生+模板渲染
root界面框架选择。
```

+ 身份验证使用分布式session与简化版jwt

+ 尽量避免级联更新与删除，保证记录持久性

+ ws即时通讯，目前id三合一{id}

+ order人性化排序

#### 待解决：

+ ~~lineId 与 line 的对应~~

+ ~~用户角色token的expire(目前想到2种方式)~~(考虑了生成时间与当前对比)

+ ~~密码加密，注意别再日志打印~~

+ 依角色拆开，全面捕获异常

+ ~~年月日定时报表任务~~

+ ~~消息的定向推送~~

+ ~~消息~~、公告、订阅分类推送与存储

+ ~~manager分配订单及取消时的界面操作与流程~~

+ ~~manager设置与info界面(暂放)~~

+ ~~返回顶部与全屏等一些小功能~~

+ ~~个人中心相关布局~~

+ ~~弹出框~~
```
order生成时利用websocket传到manager

u&d是否通过ws定向传送消息待商榷

用id区分线路(实现定向推送)，分级区分用户或采取他法

```
+ ~~前后页面的统一（以及规范化）~~

+ ~~局部分页~~

+ ~~路径完善（包括switch页面的方法函数）~~

+ ~~区分出行与返程~~

+ 并发性支持改良

+ ~~使用说明书写与格式~~(更多的写在文档或其他 eg:home)

+ ~~sidemenu bug...~~

+ ~~弹出框与原框数据整合与传送~~(可以更美观一点)

+ ~~删除的二次确认~~(order  only)

+ ~~搜索功能？~~

+ 短信验证

+ 线路添加入口

注：目前较臃肿，后续将进行服务拆分. 


