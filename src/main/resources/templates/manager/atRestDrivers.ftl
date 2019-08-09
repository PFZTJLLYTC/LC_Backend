<!doctype html>
<html class="no-js">
<head>
    <meta charset="utf-8">
    <meta content="IE=edge" http-equiv="X-UA-Compatible">
    <title>连城管理系统</title>
    <meta content="这是一个 index 页面" name="description">
    <meta content="index" name="keywords">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <meta content="webkit" name="renderer">
    <meta content="no-siteapp" http-equiv="Cache-Control"/>
    <link href="../../img/LC.png" rel="icon" type="image/png">
    <link href="../../img/app-icon72x72@2x.png" rel="apple-touch-icon-precomposed">
    <meta content="Amaze UI" name="apple-mobile-web-app-title"/>
    <link href="../../css/amazeui.min.css" rel="stylesheet"/>
    <link href="../../css/admin.css" rel="stylesheet">
    <script src="../../js/jquery.min.js"></script>
    <script src="../../js/app.js"></script>
</head>

<body>
<header class="am-topbar admin-header">
    <div class="am-topbar-brand"><img src="../../img/page.png"></div>

    <div class="am-collapse am-topbar-collapse" id="topbar-collapse">
        <ul class="am-nav am-nav-pills am-topbar-nav admin-header-list">

            <li class="am-dropdown tognzhi" data-am-dropdown>
                <button class="am-btn am-btn-primary am-dropdown-toggle am-btn-xs am-radius am-icon-bell-o"
                        data-am-dropdown-toggle> 消息管理
                    <span class="am-badge am-badge-danger am-round">${allMessages}</span>
                </button>
                <ul class="am-dropdown-content">


                    <li class="am-dropdown-header">所有消息都在这里</li>
                    <#--                    <li><a href="/manager/order/findByStatus?status=0">未处理订单 <span class="am-badge am-badge-danger am-round">6</span></a>-->
                    <#--                    </li>-->
                    <li><a href="/manager/order/findByStatus?status=0">未处理订单 <span class="am-badge am-badge-danger am-round">${orderMessages}</span></a></li>
                    <li><a href="/manager/driver/findByStatus?status=-1">待审核司机申请<span class="am-badge am-badge-danger am-round">${driverMessages}</span></a></li>
                </ul>
            </li>

            <li class="kuanjie">

                <a href="/manager/personalInfo">个人中心</a>
                <a href="/manager/goContactAndHelp">系统设置</a>
            </li>

<#--            <li class="soso">-->

<#--                <p>-->

<#--                    <select data-am-selected="{btnWidth: 70, btnSize: 'sm', btnStyle: 'default'}">-->
<#--                        <option value="b">全部</option>-->
<#--                        <option value="o">订单</option>-->
<#--                        <option value="o">司机</option>-->

<#--                    </select>-->

<#--                </p>-->

<#--                <p class="ycfg"><input class="am-form-field am-input-sm" placeholder="搜索" type="text"/></p>-->
<#--                <p>-->
<#--                    <button class="am-btn am-btn-xs am-btn-default am-xiao"><i class="am-icon-search"></i></button>-->
<#--                </p>-->
<#--            </li>-->


            <li class="am-hide-sm-only" style="float: right;"><a href="javascript:" id="admin-fullscreen"><span
                            class="am-icon-arrows-alt"></span> <span class="admin-fullText">开启全屏</span></a></li>
        </ul>
    </div>
</header>

<div class="am-cf admin-main">

    <div class="nav-navicon admin-main admin-sidebar">


        <div class="sideMenu am-icon-dashboard" style="color:#aeb2b7; margin: 10px 0 0 0;"> 欢迎系统管理员：${name}</div>
        <div class="sideMenu">
            <h3 class="am-icon-flag"><em></em> <a href="#">订单管理</a></h3>
            <ul>
                <li><a href="/manager/order/allOrders">所有订单列表</a></li>
                <li><a href="/manager/order/findByStatus?status=0">待处理订单</a></li>
                <li><a href="/manager/order/findByStatus?status=1">进行中订单</a></li>
                <li><a href="/manager/order/findByStatus?status=2">已完成订单</a></li>
            </ul>
            <h3 class="am-icon-users"><em></em> <a href="#"> 司机管理</a></h3>
            <ul>
                <li><a href="/manager/driver/allDrivers">司机列表</a></li>
                <li><a href="/manager/driver/findByStatus?status=-1">审核中司机</a></li>
                <li><a href="/manager/driver/findByStatus?status=1">在路上司机</a></li>
                <li><a href="/manager/driver/findByStatus?status=0">休息中司机</a></li>
                <li><a href="/manager/driver/findByStatus?status=2">待出行司机</a></li>
                <li><a href="/manager/driver/goToAddDriver">增加司机</a></li>
            </ul>
            <h3 class="am-icon-gears"><em></em> <a href="#">其他</a></h3>
            <ul>
                <li><a href="/manager/goContactAndHelp">推送与设置</a></li>
                <li><a href="/manager/personalInfo">个人中心</a></li>
            </ul>
        </div>
        <!-- sideMenu End -->

        <script type="text/javascript">
            jQuery(".sideMenu").slide({
                titCell: "h3", //鼠标触发对象
                targetCell: "ul", //与titCell一一对应，第n个titCell控制第n个targetCell的显示隐藏
                effect: "slideDown", //targetCell下拉效果
                delayTime: 300, //效果时间
                triggerTime: 150, //鼠标延迟触发时间（默认150）
                defaultPlay: true,//默认是否执行效果（默认true）
                returnDefault: false //鼠标从.sideMen移走后返回默认状态（默认false）
            });
        </script>


    </div>

    <div class=" admin-content">

        <div class="daohang">
            <ul>
                <li>
                    <button class="am-btn am-btn-default am-radius am-btn-xs" type="button"> <a href="/manager/goIndex">首页</a>
                </li>
<#--                <li>-->
<#--                    <button class="am-btn am-btn-default am-radius am-btn-xs" type="button">订单管理<a-->
<#--                                class="am-close am-close-spin" data-am-modal-close="" href="javascript: void(0)">×</a>-->
<#--                    </button>-->
<#--                </li>-->
<#--                <li>-->
<#--                    <button class="am-btn am-btn-default am-radius am-btn-xs" type="button">司机管理<a-->
<#--                                class="am-close am-close-spin" data-am-modal-close="" href="javascript: void(0)">×</a>-->
<#--                    </button>-->
<#--                </li>-->
            </ul>


        </div>


        <div class="admin-biaogelist">

            <div class="listbiaoti am-cf">
                <ul class="am-icon-cart-plus on">司机管理</ul>
                <dl class="am-icon-home" style="float: right;"> 当前位置： <a href="/manager/goIndex">首页</a>>休息中司机</dl>
            </div>

            <div class="am-btn-toolbars am-btn-toolbar am-kg am-cf">
                <ul>

                    <li style="margin-left: -10px;">
                        <div class="am-btn-group am-btn-group-xs">
                            <select data-am-selected="{btnWidth: 90, btnSize: 'sm', btnStyle: 'default'}">
                                <option value="all">全部</option>
                                <option value="dnum">手机号</option>
                                <option value="dname">姓名</option>
                                <option value="carnum">车牌号</option>
                            </select>
                        </div>
                    </li>
                    <li><input class="am-form-field am-input-sm am-input-xm" placeholder="左侧选择查询方式" type="text"/></li>
                    <li>
                        <button class="am-btn am-radius am-btn-xs am-btn-success" style="margin-top: -1px;"
                                type="button">搜索
                        </button>
                    </li>
                </ul>
            </div>


            <form class="am-form am-g">
                <table class="am-table am-table-bordered am-table-radius am-table-striped" width="100%">
                    <thead>
                    <tr class="am-success">
<#--                        <th class="table-check"><input type="checkbox"/></th>-->
                        <th class="table-title">姓名</th>
                        <th class="table-type">可用座位</th>
                        <th class="table-type">车牌号</th>
                        <th class="table-type">联系方式</th>
                        <th class="table-type">完成单数</th>
                        <th class="table-author am-hide-sm-only">状态</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#if drivers??>
                        <#list drivers.content as driver>
                            <tr>
<#--                                <td><input type="checkbox"/></td>-->
                                <td>${driver.name}</td>
                                <td>${driver.availableSeats}</td>
                                <td>${driver.carNum}</td>
                                <td>${driver.dnum}</td>
                                <td>${driver.workTimes}</td>
                                <td>休息中</td>
                                <#--    取消则调用删除-->
                            </tr>
                        </#list>
                    </#if>
                    </tbody>
                </table>


                <ul class="am-pagination am-fr">
                    <#if currentPage lte 1>
                        <li class="am-disabled"><a href="#">«</a></li>
                    <#else>
                        <li class=""><a href="/manager/driver/findByStatus?status=0&page=${currentPage-1}&size=${size}">«</a></li>
                    </#if>
                    <#if drivers.getTotalPages()==0>
                        <li class=""><a href="#">1</a></li>
                    <#else>
                        <#list 1..drivers.getTotalPages() as index>
                            <#if currentPage == index>
                                <li class="am-active"><a href="#">${index}</a></li>
                            <#else>
                                <li class=""><a href="/manager/driver/findByStatus?status=0&page=${index}&size=${size}">${index}</a></li>
                            </#if>
                        </#list>
                    </#if>

                    <#if currentPage gte drivers.getTotalPages()>
                        <li class="am-disabled"><a href="#">»</a></li>
                    <#else>
                        <li class=""><a href="/manager/driver/findByStatus?status=0&page=${currentPage+1}&size=${size}">»</a></li>
                    </#if>
                </ul>

            </form>


            <div class="foods">
                <ul>
                    Copyright © 2018-2019 LC All Rights Reserved
                </ul>
                <dl>
                    <a class="am-icon-btn am-icon-arrow-up" href="" title="返回头部"></a>
                </dl>
            </div>


        </div>

    </div>


</div>

<script type="text/javascript" src="../../js/layui/layui.js"></script>
<#--播放音乐-->
<audio id="notice" loop="loop">
    <#--    待添加-->
    <source src = "/mp3/song.mp3" type="audio/mpeg" />
</audio>

<script>
    var websocket = null;
    if('WebSocket' in window){
        // 第一个试试看
        websocket = new WebSocket("ws://49.234.98.50/webSocket/"+${name});
    }else {
        alert("该浏览器不支持websocket");
    }
    websocket.onopen = function (event) {
        console.log('建立连接');
    }
    websocket.onclose = function (event) {
        console.log('连接关闭');
    }
    websocket.onmessage = function (event) {
        console.log('收到消息：'+ event.data);
        document.getElementById('notice').play();
        layui.use('layer',function () {
            var layer= layui.layer; //获取layer模块
            layer.open({
                type: 1,
                title: '消息',
                // shade: false, //遮罩
                // area: ['300px', '150px'],
                // offset: 'rb', //右下角弹出
                time: 60000, //1分钟后自动关闭
                // anim: 2,
                anim: 1,
                content: '<div style="margin: 10px" >' +
                    '<label style="font-weight: bold;color: red;">'+event.data+'</label>' +'<br>'+
                    // '<p style="display:inline-block ;color: yellow;">'+666+'</p>' +
                    '</div>',
                btn: ['确定'],
                btn1: function (index,layero) {
                    document.getElementById('notice').pause();
                    location.reload();
                }
            });
        });
    }
    websocket.onerror = function (event) {
        alert('websocket通信发生错误！');
    }
    window.onbeforeunload = function (ev) { websocket.close(); }

</script>

<!--[if lt IE 9]>
<script src="http://libs.baidu.com/jquery/1.11.1/jquery.min.js"></script>
<script src="http://cdn.staticfile.org/modernizr/2.8.3/modernizr.js"></script>
<script src="../../js/polyfill/rem.min.js"></script>
<script src="../../js/polyfill/respond.min.js"></script>
<script src="../../js/amazeui.legacy.js"></script>
<![endif]-->

<!--[if (gte IE 9)|!(IE)]><!-->
<script src="../../js/amazeui.min.js"></script>
<!--<![endif]-->


</body>
</html>
