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
<%pageContext.setAttribute("currentHeader", "mepoint-list");%>
<html>

<head>

  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <meta name="description" content="">
  <meta name="author" content="">

  <title>mtime</title>

  <%@include file="/content/include/jslib.jsp" %>
  <%@include file="/content/include/csslib.jsp" %>

</head>

<body>

<div id="wrapper">

  <%@include file="/content/layout/head.jsp" %>
  <div id="page-wrapper">
    <div class="row">
      <div class="col-lg-12">
        <h1 class="page-header">接口重计算</h1>
      </div>
      <!-- /.col-lg-12 -->
    </div>
    <div class="form-group">
      <label>开始时间</label>
      <input class="form-control">
    </div>
    <div class="form-group">
      <label>结束时间</label>
      <input class="form-control">
    </div>
  </div>

</body>

</html>