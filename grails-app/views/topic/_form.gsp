<div class="control-group ${hasErrors(bean: topicInstance, field: 'title', 'error')} ">
    <label class="control-label" for="topic.title">
        <strong><g:message code="topic.title.label" default="Title" /></strong>
    </label>
    <div class="controls">
        <g:textField name="topic.title" value="${topicInstance?.title}" placeholder="Title" />
    </div>
</div>

<div class="control-group ${hasErrors(bean: topicInstance, field: 'owner', 'error')} required">
    <label class="control-label" for="topic.owner">
        <strong><g:message code="topic.owner.label" default="Owner" /></strong>
    </label>
    <div class="controls">
        <g:hiddenField name="topic.owner.id" value="${topicInstance?.owner?.id}"/>
        <a4g:textField name="topic.owner.fullName" value="${topicInstance?.owner?.fullName}"
                       autocomplete-url="${createLink(controller: 'json', action: 'listUsersByName')}"
                       autocomplete-target="topic.owner.id" placeholder="Owner" />
    </div>
</div>

<div class="control-group ${hasErrors(bean: topicInstance, field: 'universities', 'error')}">
    <label class="control-label" for="topic.universities">
        <strong><g:message code="topic.universities.label" default="Universities" /></strong>
    </label>
    <div class="controls">
        <g:select name="topic.universities" from="${universities}" multiple="multiple"
                  optionKey="id" size="5" value="${topicInstance?.universities*.id}" class="one-to-many"/>
    </div>
</div>

<div class="control-group ${hasErrors(bean: topicInstance, field: 'types', 'error')}">
    <label class="control-label" for="topic.types">
        <strong><g:message code="topic.types.label" default="Types" /></strong>
    </label>
    <div class="controls">
        <g:select name="topic.types" from="${types}" multiple="multiple"
                  size="5" value="${topicInstance?.types}" class="one-to-many"/>
    </div>
</div>

<div class="control-group">
    <label class="control-label" for="supervison-list">
        <strong><g:message code="supervision.label" default="Supervision" /></strong>
    </label>
    <div class="controls">
        <g:render template="supervision"/>
    </div>
</div>

<div class="control-group ${hasErrors(bean: topicInstance, field: 'lead', 'error')}">
    <label class="control-label" for="topic.lead">
        <strong><g:message code="topic.lead.label" default="Lead Paragraph" /></strong>
    </label>
    <div class="controls">
        <g:textArea name="topic.lead" rows="5" value="${topicInstance?.lead}"/>
    </div>
</div>

<div class="control-group ${hasErrors(bean: topicInstance, field: 'description', 'error')}">
	<label class="control-label" for="topic.description">
		<strong><g:message code="topic.description.label" default="Description" /></strong>
	</label>
    <div class="controls">
	   <g:textArea name="topic.description" rows="15" value="${topicInstance?.description}"/>
    </div>
</div>

<div class="control-group ${hasErrors(bean: topicInstance, field: 'tags', 'error')}">
    <label class="control-label" for="topic.tags">
        <strong><g:message code="topic.tags.label" default="Tags" /></strong>
    </label>
    <div class="controls">
        <g:select name="topic.tags" from="${com.redhat.theses.Tag.list()}" multiple="multiple"
                  optionKey="id" size="5" value="${topicInstance?.tags*.id}" class="many-to-many"/>
    </div>
</div>

<div class="control-group ${hasErrors(bean: topicInstance, field: 'enabled', 'error')} ">
    <div class="controls">
        <label class="checkbox" for="topic.enabled">
            <g:checkBox name="topic.enabled" value="${topicInstance?.enabled}" />
            <g:message code="topic.enabled.label" default="Enabled" />
        </label>
    </div>
</div>
