<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="user/showLayout"/>
    <title><g:message code="user.theses.title"/></title>
</head>
<body>
<content tag="active-button">theses</content>

<content tag="main-box">
    <g:if test="${thesisInstanceList && thesisInstanceList.size() != 0}">
        <table class="table">
            <thead>
            <tr>
                <g:sortableColumn property="title" title="${message(code: 'thesis.title.label')}"/>
            </tr>
            </thead>
            <tbody>
            <g:each in="${thesisInstanceList}" status="i" var="thesisInstance">
                <tr>
                    <td>
                        <g:link controller="thesis" action="show" id="${thesisInstance.id}"><g:fieldValue field="title" bean="${thesisInstance}"/></g:link>
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </g:if>
    <g:else>
        <p><g:message code="user.no.theses.found"/></p>
    </g:else>
</content>
</body>
</html>
