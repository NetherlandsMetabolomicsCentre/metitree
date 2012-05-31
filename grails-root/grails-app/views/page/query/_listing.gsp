<g:set var="simhigherall" value="${simScoreFileVsDatabase.sort{ a,b -> b.value <=> a.value }.findAll{ it.value > 0 }}" />
<g:if test="${simhigherall.size() > 0}">
	<div class="bordered">					
		<table width="100%">
			<tr>
				<th nowrap><g:message code="common.label.score" /></th>
				<th nowrap><g:message code="common.label.structure" /></th>				
				<th nowrap><g:message code="common.label.name" /></th>
				<th nowrap><g:message code="common.label.file" /></th>										
				<th nowrap><g:message code="common.label.job" /></th>			
				<th width="100%"></th>											
			</tr>
			<g:each in="${simhigherall}" var="score">
				<tr>							
					<td style="text-align:right; border-bottom: thin solid #cdcdcd; padding: 3px 5px;" valign="bottom" nowrap align="right">${(score.value*100).toFloat().trunc(2)}</td>
					<td style="border-bottom: thin solid #cdcdcd; padding: 3px 5px; text-align: center; min-width: 180px;" valign="bottom" nowrap>
						<g:if test="${score?.key?.compound()?.inchi}">
							<g:link controller="page" action="compound" id="${score?.key?.compound()?.id}">
								<img style="border: 0px; max-width: 150px; max-height:100px;" src="${score?.key?.compound()?.imageUrl}" />
							</g:link>
						</g:if>&nbsp;
					</td>
					<td style="border-bottom: thin solid #cdcdcd; padding: 3px 5px; min-width: 250px; width:100%;" valign="bottom" >
						${score?.key?.compound()?.name}<br />
						<g:if test="${score?.key?.compound()?.inchiKey}"><a target="top" href='http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=search&db=pccompound&term="${score?.key?.compound()?.inchiKey}"[InChIKey]'><img border="0" class="borderless" height="25px" src="${resource(dir:'images',file:'pubchemlogob.gif')}" alt="view" /></a></g:if><br />
						<g:if test="${score?.key?.compound()?.chemspiderId}"><a target="top" href='http://www.chemspider.com/Chemical-Structure.${score?.key?.compound()?.chemspiderId}.html'><img border="0" class="borderless" height="25px" src="${resource(dir:'images',file:'chemspider.png')}" alt="${score?.key?.compound()?.inchiKey}" longdesc="${score?.key?.compound()?.inchiKey}" /></a></g:if>
					</td>									
					<td style="border-bottom: thin solid #cdcdcd; padding: 3px 5px;" valign="bottom" nowrap><g:link action="preview" controller="msnfile" id="${score.key.id}">${score.key.name}</g:link></td>
					<td style="border-bottom: thin solid #cdcdcd; padding: 3px 5px;" valign="bottom" nowrap><g:link action="details" controller="processJob" id="${score.key.processjob.id}">${score.key.processjob.id}</g:link></td>
					<td></td>
				</tr>												
			</g:each>
		</table>
	</div>
</g:if><g:else>no results</g:else>