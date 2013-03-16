<%@ page import="com.redhat.theses.auth.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="light">
    <title><g:message code="user.edit.title" /></title>
</head>
<body>
    <h2 class="header"><g:message code="user.edit.header" /></h2>
    <g:form class="form-inline" method="post" >
        <g:hiddenField name="user.id" value="${userInstance?.id}" />
        <g:hiddenField name="user.version" value="${userInstance?.version}" />
        <g:render template="form"/>
        <div class="control-group">
            <div class="controls">
                <g:actionSubmit class="tms-btn" action="update"
                                value="${message(code: 'default.update.button')}" />
                <g:link class="tms-btn tms-danger" action="show" id="${userInstance?.id}"
                        onclick="return confirm('${message(code: 'default.delete.confirm.message')}');">${message(code: 'default.cancel.button')}</g:link>
                <g:actionSubmit class="tms-btn tms-danger"
                                action="delete"
                                value="${message(code: 'default.delete.button')}"
                                onclick="return confirm('${message(code: 'default.delete.confirm.message')}');" />
            </div>
        </div>
    </g:form>
</body>
</html>
