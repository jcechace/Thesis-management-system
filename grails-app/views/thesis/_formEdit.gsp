<g:render template="formCommon"/>

<div class="control-group ${hasErrors(bean: thesisInstance, field: 'status', 'error')}">
    <label class="control-label" for="thesis.status">
        <strong><g:message code="thesis.status.label" default="Status" /></strong>
    </label>
    <div class="controls">
        <g:select name="thesis.status"
                  from="${statusList}"
                  value="${thesisInstance?.status?.toString()}"/>
    </div>
</div>

<div class="control-group ${hasErrors(bean: thesisInstance, field: 'grade', 'error')}">
    <label class="control-label" for="thesis.grade">
        <strong><g:message code="thesis.grade.label" default="Grade" /></strong>
    </label>
    <div class="controls">
        <g:select name="thesis.grade"
                  from="${gradeList}"
                  noSelection="['':'-- no selection --']"
                  value="${thesisInstance?.grade?.toString()}"/>
    </div>
</div>

<div class="control-group ${hasErrors(bean: thesisInstance, field: 'thesisAbstract', 'error')}">
    <label class="control-label" for="thesis.thesisAbstract">
        <strong><g:message code="thesis.abstract.label" default="Abstract" /></strong>
    </label>
    <div class="controls">
        <g:textArea name="thesis.thesisAbstract"
                    cols="80" rows="5"
                    value="${thesisInstance?.thesisAbstract}"/>
    </div>
</div>
