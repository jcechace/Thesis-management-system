<%@ page import="com.redhat.theses.auth.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="light">
    <title><g:message code="profile.title" default="Edit profile" /></title>
</head>
<body>
<h2 class="header"><g:message code="profile.header" code="Edit profile" /></h2>
<g:form class="form-inline" method="post" >
    <g:hiddenField name="user.id" value="${userInstance?.id}" />
    <g:hiddenField name="user.version" value="${userInstance?.version}" />
    <g:render template="form"/>
    <div class="control-group">
        <div class="controls">
            <g:actionSubmit class="tms-btn" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" />
            <g:link class="tms-btn tms-danger" action="index" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">${message(code: 'default.button.cancel.label', default: 'Cancel')}</g:link>
        </div>
    </div>
</g:form>
</body>
</html>
