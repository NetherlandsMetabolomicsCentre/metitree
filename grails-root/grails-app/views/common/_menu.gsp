<div class="topmenu">
	<g:if test="${session.member}">
			<g:form name="msnfiles" controller="page" action="data"><button type="submit" name="msnfiles" value="submit" class="cupid-green"><g:message code="menu.msnfiles" /></button></g:form>
			<g:form name="process" controller="page" action="process"><button type="submit" name="process" value="submit" class="cupid-green"><g:message code="menu.process" /></button></g:form>
			<g:form name="database" controller="page" action="database"><button type="submit" name="database" value="submit" class="cupid-green"><g:message code="menu.databases" /></button></g:form>
			<g:form name="query" controller="page" action="query"><button type="submit" name="query" value="submit" class="cupid-green"><g:message code="menu.query.file" /></button></g:form>
			<g:form name="help" controller="page" action="help"><button type="submit" name="help" value="submit" class="cupid-grey"><g:message code="menu.help" /></button></g:form>
    </g:if>        
</div>
<div class="adminmenu">
    <g:if test="${session.member?.admin}">
    		<g:form name="import" controller="import" action="select"><button type="submit" name="import" value="submit" class="cupid-green"><g:message code="menu.import" /></button></g:form>
   			<g:form name="member" controller="member" action="list"><button type="submit" name="member" value="submit" class="cupid-green-small"><g:message code="menu.member" /></button></g:form>
   			<g:form name="usergroup" controller="usergroup" action="list"><button type="submit" name="usergroup" value="submit" class="cupid-green-small"><g:message code="menu.usergroup" /></button></g:form>      	   
    </g:if>
</div>