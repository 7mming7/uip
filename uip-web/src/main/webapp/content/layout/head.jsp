<%--
  Created with IntelliJ IDEA. 
  User: shuiqing
  Date: 2014/12/17 
  Time: 5:20 
  Email: shuiqing301@gmail.com
  GitHub: https://github.com/ShuiQing301
  Blog: http://shuiqing301.github.io/
   _      
  |_)._ _ 
  | o| (_
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Ocean</title>
</head>
<body>
<!-- Navigation -->
<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
  <div class="navbar-header">
    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
      <span class="sr-only">Toggle navigation</span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
      <span class="icon-bar"></span>
    </button>
    <a class="navbar-brand" href="${ctx}/">Ocean --- P.nc_shuiqing</a>
  </div>
  <!-- /.navbar-header -->

  <ul class="nav navbar-top-links navbar-right">
    <!-- /.dropdown -->
    <li class="dropdown">
      <a class="dropdown-toggle" data-toggle="dropdown" href="#">
        <i class="fa fa-user fa-fw"></i>  <i class="fa fa-caret-down"></i>
      </a>
      <ul class="dropdown-menu dropdown-user">
        <li><a href="#"><i class="fa fa-user fa-fw"></i> 用户信息</a>
        </li>
        <li><a href="#"><i class="fa fa-gear fa-fw"></i> 设置</a>
        </li>
        <li class="divider"></li>
        <li><a href="${ctx}/toDashBord.do"><i class="fa fa-sign-out fa-fw"></i> 注销</a>
        </li>
      </ul>
      <!-- /.dropdown-user -->
    </li>
    <!-- /.dropdown -->
  </ul>
  <!-- /.navbar-top-links -->

  <div class="navbar-default sidebar" role="navigation">
    <div class="sidebar-nav navbar-collapse">
      <ul class="nav" id="side-menu">
        <li>
          <a href="${ctx}/dashbord/toDashBord.do"><i class="fa fa-dashboard fa-fw"></i> 控制面板</a>
        </li>
        <li>
          <a href="toTables.do"><i class="fa fa-bar-chart-o fa-fw"></i> 接口监视<span class="fa arrow"></span></a>
          <ul class="nav nav-second-level">
            <li>
              <a class="${currentHeader == 'interface-config' ? 'active' : ''}" href="${ctx}/content/protocal/monitor/opc-realtime-disp.jsp">实时数据</a>
            </li>
          </ul>
        </li>
        <li>
          <a href="../account/tables.html"><i class="fa fa-bar-chart-o fa-fw"></i> 接口配置<span class="fa arrow"></span></a>
          <ul class="nav nav-second-level">
            <li>
              <a class="${currentHeader == 'interface-config' ? 'active' : ''}" href="${ctx}/content/mepoint/mepoint-list.do">测点管理</a>
            </li>
          </ul>
        </li>
        <li>
          <a href="../account/tables.html"><i class="fa fa-bar-chart-o fa-fw"></i>  通信协议<span class="fa arrow"></span></a>
          <ul class="nav nav-second-level">
            <li>
              <a class="${currentHeader == 'interface-config' ? 'active' : ''}" href="${ctx}/content/protocal/opc/opc-client-add.jsp">OPC通信新增</a>
            </li>
            <li>
              <a class="${currentHeader == 'interface-config' ? 'active' : ''}" href="${ctx} /opc/listOpcConnection.do">OPC通信列表</a>
            </li>
          </ul>
        </li>
        <li>
          <a href="forms.html"><i class="fa fa-edit fa-fw"></i> 系统设置</a>
        </li>
      </ul>
    </div>
  </div>
</nav>
</body>
</html>
