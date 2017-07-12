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

          return (bytes / Math.pow(k, i)).toPrecision(6) + ' ' + sizes[i];
          //toPrecision(3) 后面保留一位小数，如1.0GB                                                                                                                  //return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
      }
      window.setInterval("getTable();",5000);
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
                      for(var i=0;i<result.nodes.length;i++){
                          s1+="<tr>" +
                              "<td>" + (i+1)+ "</td>" +
                              "<td>" + result.nodes[i].nodeName+ "</td>" +
                              "<td>" + result.nodes[i].nodeIP+ "</td>" +
                              "<td>" + result.nodes[i].nodePort+ "</td>" +
                              "<td>" + bytesToSize(result.nodes[i].volume)+ "</td>" +
                              "<td>" + bytesToSize(result.nodes[i].restVolume)+ "</td>" +
                              "<td>" + result.nodes[i].file_num+ "</td>" +
                              "<td>yes </td>" +
                              "</tr>";
                      }
                      for(var i=0;i<result.files.length;i++){
                          s2+="<tr>" +
                              "<td>" + (i+1)+ "</td>" +
                              "<td>" + result.files[i].file_id+ "</td>" +
                              "<td>" + result.files[i].file_name+ "</td>" +
                              "<td>" + bytesToSize(result.files[i].file_size)+ "</td>" +
                              "<td>" + result.files[i].main_node.nodeName+ "</td>" +
                              "<td>" + result.files[i].sec_node.nodeName+ "</td>" +
                              "<td>" + result.files[i].client_name+ "</td>" +
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
</head>
<body id="skin-blur-blue">
<header id="header" class="media">
  <a href="" id="menu-toggle"></a>
  <a class="logo pull-left" href="index.html">Distribute File Storage</a>

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

      <!-- Projects -->
      <!--div class="s-widget m-b-25">
        <h2 class="tile-title">
          Projects on going
        </h2>

        <div class="s-widget-body">
          <div class="side-border">
            <small>Joomla Website</small>
            <div class="progress progress-small">
              <a href="#" data-toggle="tooltip" title="" class="progress-bar tooltips progress-bar-danger" style="width: 60%;" data-original-title="60%">
                <span class="sr-only">60% Complete</span>
              </a>
            </div>
          </div>
          <div class="side-border">
            <small>Opencart E-Commerce Website</small>
            <div class="progress progress-small">
              <a href="#" data-toggle="tooltip" title="" class="tooltips progress-bar progress-bar-info" style="width: 43%;" data-original-title="43%">
                <span class="sr-only">43% Complete</span>
              </a>
            </div>
          </div>
          <div class="side-border">
            <small>Social Media API</small>
            <div class="progress progress-small">
              <a href="#" data-toggle="tooltip" title="" class="tooltips progress-bar progress-bar-warning" style="width: 81%;" data-original-title="81%">
                <span class="sr-only">81% Complete</span>
              </a>
            </div>
          </div>
          <div class="side-border">
            <small>VB.Net Software Package</small>
            <div class="progress progress-small">
              <a href="#" data-toggle="tooltip" title="" class="tooltips progress-bar progress-bar-success" style="width: 10%;" data-original-title="10%">
                <span class="sr-only">10% Complete</span>
              </a>
            </div>
          </div>
          <div class="side-border">
            <small>Chrome Extension</small>
            <div class="progress progress-small">
              <a href="#" data-toggle="tooltip" title="" class="tooltips progress-bar progress-bar-success" style="width: 95%;" data-original-title="95%">
                <span class="sr-only">95% Complete</span>
              </a>
            </div>
          </div>
        </div>
      </div-->
    </div>

    <!-- Side Menu -->
    <!--ul class="list-unstyled side-menu">
        <li>
            <a class="sa-side-home" href="index.html">
                <span class="menu-item">Dashboard</span>
            </a>
        </li>
        <li>
            <a class="sa-side-typography" href="typography.html">
                <span class="menu-item">Typography</span>
            </a>
        </li>
        <li>
            <a class="sa-side-widget" href="content-widgets.html">
                <span class="menu-item">Widgets</span>
            </a>
        </li>
        <li class="active">
            <a class="sa-side-table" href="tables.html">
                <span class="menu-item">Tables</span>
            </a>
        </li>
        <li class="dropdown">
            <a class="sa-side-form" href="">
                <span class="menu-item">Form</span>
            </a>
            <ul class="list-unstyled menu-item">
                <li><a href="form-elements.html">Basic Form Elements</a></li>
                <li><a href="form-components.html">Form Components</a></li>
                <li><a href="form-examples.html">Form Examples</a></li>
                <li><a href="form-validation.html">Form Validation</a></li>
            </ul>
        </li>
        <li class="dropdown">
            <a class="sa-side-ui" href="">
                <span class="menu-item">User Interface</span>
            </a>
            <ul class="list-unstyled menu-item">
                <li><a href="buttons.html">Buttons</a></li>
                <li><a href="labels.html">Labels</a></li>
                <li><a href="images-icons.html">Images &amp; Icons</a></li>
                <li><a href="alerts.html">Alerts</a></li>
                <li><a href="media.html">Media</a></li>
                <li><a href="components.html">Components</a></li>
                <li><a href="other-components.html">Others</a></li>
            </ul>
        </li>
        <li>
            <a class="sa-side-chart" href="charts.html">
                <span class="menu-item">Charts</span>
            </a>
        </li>
        <li>
            <a class="sa-side-folder" href="file-manager.html">
                <span class="menu-item">File Manager</span>
            </a>
        </li>
        <li>
            <a class="sa-side-calendar" href="calendar.html">
                <span class="menu-item">Calendar</span>
            </a>
        </li>
        <li class="dropdown">
            <a class="sa-side-page" href="">
                <span class="menu-item">Pages</span>
            </a>
            <ul class="list-unstyled menu-item">
                <li><a href="list-view.html">List View</a></li>
                <li><a href="profile-page.html">Profile Page</a></li>
                <li><a href="messages.html">Messages</a></li>
                <li><a href="login.html">Login</a></li>
                <li><a href="404.html">404 Error</a></li>
            </ul>
        </li>
    </ul-->

  </aside>
  <!-- Content -->
  <section id="content" class="container">
    <!-- Breadcrumb -->
    <ol class="breadcrumb hidden-xs">
      <li><a href="#">Home</a></li>
      <li><a href="#">Library</a></li>
      <li class="active">Data</li>
    </ol>
    <h4 class="page-title">Monitor</h4>

    <!-- Responsive Table -->
    <div class="block-area" id="responsiveTable">
      <h3 class="block-title">Node Information</h3>
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
    <!-- Table Hover -->
    <!--div class="block-area" id="tableHover">
      <h3 class="block-title">Table with Hover Style</h3>
      <div class="table-responsive overflow">
        <table class="table table-bordered table-hover tile">
          <thead>
          <tr>
            <th>No.</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Username</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>1</td>
            <td>Jhon </td>
            <td>Makinton </td>
            <td>@makinton</td>
          </tr>
          <tr>
            <td>2</td>
            <td>Malinda</td>
            <td>Hollaway</td>
            <td>@hollway</td>
          </tr>
          <tr>
            <td>3</td>
            <td>Wayn</td>
            <td>Parnel</td>
            <td>@wayne123</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Table Striped -->
    <div class="block-area" id="tableStriped">
      <h3 class="block-title">Table Striped</h3>
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
<!-- jQuery -->
<script src="js/jquery.min.js"></script> <!-- jQuery Library -->

<!-- Bootstrap -->
<script src="js/bootstrap.min.js"></script>

<!-- UX -->
<script src="js/scroll.min.js"></script> <!-- Custom Scrollbar -->

<!-- Other -->
<script src="js/calendar.min.js"></script> <!-- Calendar -->
<!-- All JS functions -->
<script src="js/functions.js"></script>
</body>
</html>


