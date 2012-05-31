<table style="padding: 5px;" width="100%">	
	<tr>
		<th></th>	
		<th></th>		
		<th nowrap><b><g:message code="common.label.name" /></b></th>					
		<th nowrap><b><g:message code="common.label.created" /></b></th>
		<th></th>		
	</tr>
	<g:each in="${databases.sort { a,b -> a.name <=> b.name }}" var="database">
		<tr>
			<td align="right" style="padding: 3px;">
				<g:if test="${!database.msnfiles.size()}">
					<g:link controller="database" action="remove" id="${database.id}"><img class="borderless" width="25px" src="${resource(dir:'images',file:'delete.png')}" alt="delete" /></g:link>
				</g:if>				
			</td>
			<td align="right" style="padding: 3px;">
				<g:link controller="database" action="details" id="${database.id}" params="[jobSelected: jobSelected]"><img class="borderless" width="25px" src="${resource(dir:'images',file:'view.png')}" alt="view" /></g:link>
			</td>
			<td nowrap style="padding: 5px;" valign="bottom">
				${database.name}
			</td> 
			<td nowrap style="padding: 5px;" valign="bottom">
				${database.dateCreated}
			</td>			
			<td width="100%"></td> 					
		</tr>
	</g:each>
</table>