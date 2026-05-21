<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%><!DOCTYPE html>
${fe:html(true)}
<head>
    <meta charset="UTF-8">
    <title><la:message key="labels.admin_brand_title"/> | <la:message key="labels.theme_upload_title"/></title>
    <jsp:include page="/WEB-INF/view/common/admin/head.jsp"></jsp:include>
</head>
<body class="hold-transition sidebar-mini">
<div class="wrapper">
    <jsp:include page="/WEB-INF/view/common/admin/header.jsp"></jsp:include>
    <jsp:include page="/WEB-INF/view/common/admin/sidebar.jsp">
        <jsp:param name="menuCategoryType" value="system"/>
        <jsp:param name="menuType" value="theme"/>
    </jsp:include>
    <main class="content-wrapper">
        <div class="content-header">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1><la:message key="labels.theme_upload_title"/></h1>
                    </div>
                </div>
            </div>
        </div>
        <section class="content">
            <div class="row">
                <div class="col-md-12">
                    <la:info id="msg" message="true">
                        <div class="alert alert-success">${msg}</div>
                    </la:info>
                    <la:errors property="_global"/>
                </div>
                <div class="col-md-12">
                    <div class="card card-outline card-success">
                        <div class="card-header">
                            <h3 class="card-title">
                                <la:message key="labels.theme_upload"/>
                            </h3>
                        </div>
                        <la:form action="/admin/theme/upload" enctype="multipart/form-data">
                            <div class="card-body">
                                <div class="form-group row">
                                    <label for="themeFile" class="col-md-3 text-sm-right col-form-label">
                                        <la:message key="labels.theme_zip_file"/>
                                    </label>
                                    <div class="col-md-9 text-sm-right col-form-label">
                                        <input id="themeFile" type="file" name="themeFile" accept=".zip" class="form-control-file"/>
                                    </div>
                                </div>
                                <button type="submit" class="btn btn-success" name="upload"
                                        value="<la:message key="labels.theme_upload"/>">
                                    <i class="fa fa-upload" aria-hidden="true"></i>
                                    <la:message key="labels.theme_upload"/>
                                </button>
                            </div>
                        </la:form>
                        <div class="card-footer">
                            <la:form action="/admin/theme/">
                                <button type="submit" class="btn btn-default" name="back"
                                        value="<la:message key="labels.crud_button_back"/>">
                                    <i class="fa fa-arrow-circle-left" aria-hidden="true"></i>
                                    <la:message key="labels.crud_button_back"/>
                                </button>
                            </la:form>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </main>
    <jsp:include page="/WEB-INF/view/common/admin/footer.jsp"></jsp:include>
</div>
<jsp:include page="/WEB-INF/view/common/admin/foot.jsp"></jsp:include>
<script>
    (function () {
        var form = document.querySelector('form[action$="/upload"]');
        if (!form) {
            return;
        }
        form.addEventListener('submit', function (e) {
            var fileInput = document.getElementById('themeFile');
            if (fileInput && fileInput.files && fileInput.files[0]) {
                var max = 10485760; // 10 MB default per fess_config.properties — server-side enforces actual limit
                if (fileInput.files[0].size > max) {
                    alert('File too large');
                    e.preventDefault();
                }
            }
        });
    })();
</script>
</body>
${fe:html(false)}
