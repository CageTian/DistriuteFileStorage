<%--
  Created by IntelliJ IDEA.
  User: caget
  Date: 2017/7/12
  Time: 8:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<!--[if IE 9 ]><html class="ie9"><![endif]-->
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
  <meta name="format-detection" content="telephone=no">
  <meta charset="UTF-8">

  <title>Distribute File Storage System Monitor</title>

  <!-- CSS -->
  <link href="css/bootstrap.min.css" rel="stylesheet">
  <link href="css/animate.min.css" rel="stylesheet">
  <link href="css/font-awesome.min.css" rel="stylesheet">
  <link href="css/form.css" rel="stylesheet">
  <link href="css/calendar.css" rel="stylesheet">
  <link href="css/media-player.css" rel="stylesheet">
  <link href="css/style.css" rel="stylesheet">
  <link href="css/icons.css" rel="stylesheet">
  <link href="css/generics.css" rel="stylesheet">
  <script type="text/javascript">
      function bytesToSize(bytes) {
          if (bytes === 0) return '0 B';

          var k = 1024;

          var sizes = ['B','KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

          i = Math.floor(Math.log(bytes) / Math.log(k));

          return (bytes / Math.pow(k, i)).toPrecision(5) + ' ' + sizes[i];
          //toPrecision(3) 后面保留一位小数，如1.0GB                                                                                                                  //return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
      }
      window.setInterval("getTable();", 1000);
      function getTable() {
          $.ajax({
              cache: false,
              async: true,
              type: "POST",
              dataType: "json",
              url: "/MonitorServlet",
              success: function(result) {
                  var s1=null;
                  var s2=null;
                  if(result) {
                      for (var j = 0; j < result.nodes.length; j++) {
                          s1+="<tr>" +
                              "<td>" + (j + 1) + "</td>" +
                              "<td>" + result.nodes[j].nodeName + "</td>" +
                              "<td>" + result.nodes[j].nodeIP + "</td>" +
                              "<td>" + result.nodes[j].nodePort + "</td>" +
                              "<td>" + bytesToSize(result.nodes[j].volume) + "</td>" +
                              "<td>" + bytesToSize(result.nodes[j].restVolume) + "</td>" +
                              "<td>" + result.nodes[j].file_num + "</td>" +
                              "<td>yes </td>" +
                              "</tr>";
                      }
                      for (var j = 0; j < result.files.length; j++) {
                          s2+="<tr>" +
                              "<td>" + (j + 1) + "</td>" +
                              "<td>" + result.files[j].file_id + "</td>" +
                              "<td>" + result.files[j].file_name + "</td>" +
                              "<td>" + bytesToSize(result.files[j].file_size) + "</td>" +
                              "<td>" + result.files[j].main_node.nodeName + "</td>" +
                              "<td>" + result.files[j].sec_node.nodeName + "</td>" +
                              "<td>" + result.files[j].client_name + "</td>" +
                              "</tr>";
                      }
//                        alert(s1);
                      $("#ajax_nodes").html(s1);
                      $("#ajax_files").html(s2);
//                        $("#ajax_files").text("错误的验证码！");
                  }
              }
          });
      }
  </script>
  <script type="text/javascript">
      var webSocket =
          new WebSocket('ws://localhost:8080/MyWebSocketServlet');

      webSocket.onerror = function (event) {
          onError(event)
      };

      webSocket.onopen = function (event) {
          onOpen(event)
      };

      webSocket.onmessage = function (event) {
          onMessage(event)
      };

      function onMessage(event) {
//          $("#node_title").html(event.data);
      }

      function onOpen(event) {
          document.getElementById('messages').innerHTML
              = 'Connection established';
      }

      function onError(event) {
          alert(event.data);
      }
      window.onload = function () {
          webSocket.send('hello');
          return false;
      };

  </script>
</head>
<body id="skin-blur-blue">
<header id="header" class="media">
  <a href="" id="menu-toggle"></a>
  <a class="logo pull-left" href="index2.html">Distribute File Storage</a>

  <div class="media-body">
    <div class="media" id="top-menu">
      <div class="pull-left tm-icon">
        <a data-drawer="messages" class="drawer-toggle" href="">
          <i class="sa-top-message"></i>
          <span>Messages</span>
        </a>
      </div>
      <div class="pull-left tm-icon">
        <a data-drawer="notifications" class="drawer-toggle" href="">
          <i class="sa-top-updates"></i>

          <span>Updates</span>
        </a>
      </div>



      <div id="time" class="pull-right">
        <span id="hours"></span>
        :
        <span id="min"></span>
        :
        <span id="sec"></span>
      </div>

      <div class="media-body">
        <input type="text" class="main-search">
      </div>
    </div>
  </div>
</header>

<div class="clearfix"></div>

<section id="main" class="p-relative" role="main">

  <!-- Sidebar -->
  <aside id="sidebar">

    <!-- Sidbar Widgets -->
    <div class="side-widgets overflow">
      <!-- Profile Menu -->
      <div class="text-center s-widget m-b-25 dropdown" id="profile-menu">
        <a href="" data-toggle="dropdown">
          <img class="profile-pic animated" src="img/profile-pic.jpg" alt="">
        </a>
        <ul class="dropdown-menu profile-menu">
          <li><a href="">My Profile</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
          <li><a href="">Messages</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
          <li><a href="">Settings</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
          <li><a href="">Sign Out</a> <i class="icon left">&#61903;</i><i class="icon right">&#61815;</i></li>
        </ul>
        <h4 class="m-0">Cage Tian</h4>
        cagetian@outlook.com
      </div>

      <!-- Calendar -->
      <div class="s-widget m-b-25">
        <div id="sidebar-calendar"></div>
      </div>

      <!-- Feeds -->
      <div class="s-widget m-b-25">
        <h2 class="tile-title">
          News Feeds
        </h2>

        <div class="s-widget-body">
          <div id="news-feed"></div>
        </div>
      </div>

    </div>


  </aside>
  <!-- Content -->
  <section id="content" class="container">
    <!-- Breadcrumb -->
    <ol class="breadcrumb hidden-xs">

      <li class="active">Data</li>
    </ol>
    <h4 class="page-title">Monitor</h4>


    <hr class="whiter"/>


    <!-- Responsive Table -->
    <div class="block-area" id="responsiveTable">
      <h3 class="block-title" id="node_title">Node Information</h3>
      <div class="table-responsive overflow">
        <table class="table tile">
          <thead>
          <tr>
            <th>NO.</th>
            <th>Node Name</th>
            <th>Node IP</th>
            <th>Node Port</th>
            <th>Node Volume</th>
            <th>Node Rest Volume</th>
            <th>File Number</th>
            <th>Is Work</th>
          </tr>
          </thead>
          <tbody id="ajax_nodes">

          </tbody>
        </table>
      </div>
    </div>


    <!-- Table Striped -->
    <div class="block-area" id="tableStriped">
        <h3 class="block-title">File Infomation</h3>
      <div class="table-responsive overflow">
        <table class="tile table table-bordered table-striped">
          <thead>
          <tr>
            <th>No.</th>
            <th>File ID</th>
            <th>File Name</th>
            <th>File Size</th>
            <th>Main Storage Node</th>
            <th>Minor Storage Node</th>
            <th>Client Owner</th>
          </tr>
          </thead>
          <tbody id="ajax_files">
          </tbody>
        </table>
      </div>
    </div>


  </section>
  <br/><br/>
</section>

<!-- Javascript Libraries -->

<!-- Charts -->
<script src="js/charts/jquery.flot.js"></script> <!-- Flot Main -->
<script src="js/charts/jquery.flot.time.js"></script> <!-- Flot sub -->
<script src="js/charts/jquery.flot.animator.min.js"></script> <!-- Flot sub -->
<script src="js/charts/jquery.flot.resize.min.js"></script> <!-- Flot sub - for repaint when resizing the screen -->

<script src="js/sparkline.min.js"></script> <!-- Sparkline - Tiny charts -->
<script src="js/easypiechart.js"></script> <!-- EasyPieChart - Animated Pie Charts -->
<script src="js/charts.js"></script> <!-- All the above chart related functions -->

<!-- Map -->
<script src="js/maps/jvectormap.min.js"></script> <!-- jVectorMap main library -->
<script src="js/maps/usa.js"></script> <!-- USA Map for jVectorMap -->

<!--  Form Related -->
<script src="js/icheck.js"></script> <!-- Custom Checkbox + Radio -->

<!-- jQuery -->
<script src="js/jquery.min.js"></script> <!-- jQuery Library -->
<script src="js/jquery-ui.min.js"></script> <!-- jQuery UI -->
<script src="js/jquery.easing.1.3.js"></script> <!-- jQuery Easing - Requirred for Lightbox + Pie Charts-->


<!-- Bootstrap -->
<script src="js/bootstrap.min.js"></script>

<!-- UX -->
<script src="js/scroll.min.js"></script> <!-- Custom Scrollbar -->

<!-- Other -->
<script src="js/calendar.min.js"></script> <!-- Calendar -->
<script src="js/feeds.min.js"></script> <!-- News Feeds -->
<!-- All JS functions -->
<script src="js/functions.js"></script>
</body>
</html>


