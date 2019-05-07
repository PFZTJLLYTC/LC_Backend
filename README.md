# lcBackstage
the backstage of lc.

## java 11.0.1 + spring boot 2.1.3 + redis 4.0.2 + tomcat 9 + mysql 8.0.15
```
controller两种接口选择 ： 模板渲染 json（偏向）

app使用后，web使用前
```

+ 身份验证使用分布式session与简化版jwt

+ 尽量避免级联更新与删除，保证记录持久性

#### 待解决：

+ lineId 与 line 的对应

+ ~~用户角色token的expire(目前想到2种方式)~~(考虑了生成时间与当前对比)

+ 异常处理不全面，需要依角色拆开，分开捕获

+ 24小时定时进行任务总结

+ ~~消息的定向推送~~
```
order生成时利用websocket传到manager

u&d是否通过ws定向传送消息待商榷

用id区分线路(实现定向推送)，分级区分用户或采取他法

```
+ 前后页面的统一（以及规范化）
+ 路径完善及分页

注：目前较臃肿，后续将进行服务拆分. 


