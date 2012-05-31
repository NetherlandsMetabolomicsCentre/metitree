<g:if test="${directories}">
	<table style="padding: 5px;" width="100%">	
		<tr>
			<th></th>
			<th></th>		
			<th nowrap><g:message code="common.label.name" /></th>
			<th nowrap><g:message code="common.label.file" /></th>					
			<th nowrap><g:message code="common.label.created" /></th>
			<th></th>		
		</tr>
		<g:each in="${directories.sort { a,b -> a.name <=> b.name }}" var="directory">
			<tr>
				<td align="right" style="padding: 3px;">
					<g:if test="${!directory?.msnfiles?.size()}">
						<g:link controller="directory" action="remove" id="${directory.id}"><img border="0" class="borderless" width="15px" src="${resource(dir:'images',file:'delete.png')}" alt="delete" title="delete" /></g:link>
					</g:if>
				</td>		
				<td align="right" style="padding: 3px;">
					<g:link controller="page" action="directory" id="${directory.id}"><img border="0" class="borderless" width="15px" src="${resource(dir:'images',file:'view.png')}" alt="view" title="view"/></g:link>
				</td>			
				<td nowrap style="padding: 5px;" valign="bottom">
					${directory.name}
				</td> 
				<td nowrap style="padding: 5px;" align="left" valign="bottom">
					${directory?.toplevelmsnfiles?.size() ?: 0}
				</td>
				<td nowrap style="padding: 5px;" valign="bottom">
					<g:formatDate format="yyyy-MM-dd" date="${directory.dateCreated}"/>
				</td>			
				<td width="100%"></td> 					
			</tr>
		</g:each>
	</table>
</g:if>