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
                    <h1 class="page-header">测点管理</h1>
                </div>
                <!-- /.col-lg-12 -->
            </div>
            <!-- /.row -->
            <div class="row">
                <div class="col-lg-12">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <div class="table-responsive">
                                <table class="table table-striped table-bordered table-hover" id="mepoint-list">
                                    <thead>
                                        <tr>
                                            <th>接口编码</th>
                                            <th>目标编码</th>
                                            <th>测点名称</th>
                                            <th>取数方式</th>
                                            <th>计算公式</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${page.result}" var="item">
                                            <tr>
                                                <td>${item.sourceCode}</td>
                                                <td>${item.targetCode}</td>
                                                <td>${item.pointName}</td>
                                                <td>${item.meaType}</td>
                                                <td>${item.calExpression}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <!-- /.panel-body -->
                    </div>
                    <!-- /.panel -->
                </div>
                <!-- /.col-lg-12 -->
            </div>
</div>

</body>

</html>