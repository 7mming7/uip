<%--
  Created with IntelliJ IDEA. 
  User: shuiqing
  Date: 2014/12/26 
  Time: 23:50 
  Email: shuiqing301@gmail.com
  GitHub: https://github.com/ShuiQing301
  Blog: http://shuiqing301.github.io/
   _      
  |_)._ _ 
  | o| (_
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/content/include/taglib.jsp" %>
<html>

<head>

  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="description" content="">
  <meta name="author" content="">

  <%@include file="/content/include/jslib.jsp" %>
  <%@include file="/content/include/csslib.jsp" %>
</head>

<body>

<div id="wrapper">

  <%@include file="/content/layout/head.jsp" %>
  <div id="page-wrapper">
    <div class="row">
      <div class="col-lg-12">
        <h1 class="page-header">OPC通信管理</h1>
      </div>
    </div>
    <!-- /.row -->
    <div class="row">
      <div class="col-lg-12">
        <div class="panel panel-default">
          <div class="panel-body">
            <div class="row">
              <form action="${ctx}/opc/addOpcClient.do" enctype="multipart/form-data" class="form-horizontal" role="form" method="post">
                <div class="col-lg-6">
                  <div class="form-group">
                    <label for="connName" class="col-sm-3 control-label">连接名称</label>
                    <div class="input-group text col-sm-5">
                      <input id="connName" name="connName" class="form-control" type="text">
                    </div>
                  </div>

                  <div class="form-group">
                    <label for="username" class="col-sm-3 control-label">连接用户</label>
                    <div class="input-group text col-sm-5">
                      <input id="username" name="username" class="form-control" type="text">
                    </div>
                  </div>

                  <div class="form-group">
                    <label for="clsid" class="col-sm-3 control-label">CLSID</label>
                    <div class="input-group text col-sm-5">
                      <input id="clsid" name="clsid" class="form-control" type="text">
                    </div>
                  </div>

                  <div class="form-group">
                    <div class="col-sm-offset-3">
                      <button type="submit" class="btn btn-success col-sm-3">提交</button>
                    </div>
                  </div>
                </div>

                <div class="col-lg-6">
                  <div class="form-group">
                    <label for="host" class="col-sm-3 control-label">目标IP</label>
                    <div class="input-group text col-sm-5">
                      <input id="host" name="host" class="form-control" type="text">
                    </div>
                  </div>

                  <div class="form-group">
                    <label for="password" class="col-sm-3 control-label">连接密码</label>
                    <div class="input-group text col-sm-5">
                      <input id="password" name="password" class="form-control" type="password">
                    </div>
                  </div>

                  <div class="form-group">
                    <label for="pointFile" class="col-sm-3 control-label">测点文件</label>
                    <div class="input-group text col-sm-5">
                      <input id="pointFile" name="pointFile" class="form-control" type="file">
                    </div>
                  </div>
                </div>
              </form>
              <!-- /.col-lg-6 (nested) -->
            </div>
            <!-- /.row (nested) -->
          </div>
          <!-- /.panel-body -->
        </div>
        <!-- /.panel -->
      </div>
      <!-- /.col-lg-12 -->
    </div>

  </div>
</div>
</body>

</html>