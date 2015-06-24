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
    </div>
    <!-- /.row -->
    <div class="row">
      <div class="col-lg-12">
        <div class="panel panel-default">
          <div class="panel-body">
            <div class="row">
              <div class="col-lg-6">
                <form action="" class="form-inhine" role="form">
                  <div class="form-group">
                    <label for="dtp_input1" class="col-md-3 control-label">开始时间</label>
                    <div class="input-group date form_date col-md-5" data-date="" data-date-format="yyyy-mm-dd" data-link-field="dtp_input1" data-link-format="yyyy-mm-dd">
                      <input class="form-control col-md-5" size="16" type="text" value="" readonly>
                      <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                    </div>
                    <input type="hidden" id="dtp_input1" value="" /><br/>
                  </div>
                  <div class="form-group">
                    <label for="dtp_input3" class="col-md-3 control-label">指标编码</label>
                    <div class="input-group text col-md-5">
                      <input class="form-control" type="text">
                    </div>
                    <input type="hidden" id="dtp_input3" value="" /><br/>
                  </div>
                </form>
              </div>
              <!-- /.col-lg-6 (nested) -->
              <div class="col-lg-6">
                <div class="form-group">
                  <label for="dtp_input2" class="col-md-3 control-label">结束时间</label>
                  <div class="input-group date form_date col-md-5" data-date="" data-date-format="yyyy-mm-dd" data-link-field="dtp_input1" data-link-format="yyyy-mm-dd">
                    <input class="form-control" size="16" type="text" value="" readonly>
                    <span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
                  </div>
                  <input type="hidden" id="dtp_input2" value="" /><br/>
                </div>
              </div>
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

    <script type="text/javascript">
      $('.form_date').datetimepicker({
        language:  'zh-CN',
        weekStart: 1,
        todayBtn:  1,
        autoclose: 1,
        todayHighlight: 1,
        startView: 2,
        minView: 2,
        forceParse: 0
      });
    </script>
</body>

</html>