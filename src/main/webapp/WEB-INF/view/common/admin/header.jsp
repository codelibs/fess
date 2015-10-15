<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
      <!-- Main Header -->
      <header class="main-header">

        <!-- Logo -->
        <la:link href="/admin/" styleClass="logo">
          <span class="logo-mini"><la:message key="labels.header.logo_alt" /></span>
          <span class="logo-lg"><la:message key="labels.admin_dashboard_title" /></span>
        </la:link>

        <!-- Header Navbar -->
        <nav class="navbar navbar-static-top" role="navigation">
          <!-- Sidebar toggle button-->
          <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
            <span class="sr-only"><la:message key="labels.admin_toggle_navi" /></span>
          </a>
          <!-- Navbar Right Menu -->
          <div class="navbar-custom-menu">
            <ul class="nav navbar-nav">
              <li>
                <a href="${helpLink}" target="_olh"><i class="fa fa-question-circle"></i></a>
              </li>
              <li>
                <a href="${contextPath}/logout"><i class="fa fa-sign-out"></i></a>
              </li>
            </ul>
          </div>
        </nav>
      </header>
