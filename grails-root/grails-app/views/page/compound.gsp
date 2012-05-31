<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="metitree" />
  </head>
  <body>					
	<g:if test="${compound}">
		<div class="thin_green_border">	
			<table cellspacing="10px">
				<tr>
					<td></td>
					<td valign="top"><h2 style="color: #5a8f00;">${compound.name}</h2></td>
				</tr>				
				<tr>
					<td valign="top" nowrap><b><g:message code="common.label.id" /></b></td>
					<td valign="top">${compound.id}</td>
				</tr>
				<tr>
					<td valign="top"></td>
					<td valign="top"><img style="border: thin solid #cdcdcd; padding:15px; max-height: 200px;" src="${compound.imageUrl}"></td>
				</tr>			
				<tr>
					<td valign="top" nowrap><b><g:message code="common.label.inchikey" /></b></td>
					<td valign="top">${compound.inchiKey}</td>
				</tr>
				<tr>
					<td valign="top" nowrap><b><g:message code="common.label.inchi" /></b></td>
					<td valign="top">${compound.inchi}</td>
				</tr>
				<tr>
					<td valign="top" nowrap><!--<g:message code="common.label.externaldbs" />--></td>
					<td valign="top">
						<g:if test="${compound.inchiKey}"><a target="_blank" href='http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=search&db=pccompound&term="${compound.inchiKey}"[InChIKey]'><img class="borderless" src="${resource(dir:'images',file:'pubchemlogob.gif')}" alt="view" /></a><br /></g:if>
						<g:if test="${compound.chemspiderId}"><a target="_blank" href='http://www.chemspider.com/Chemical-Structure.${compound.chemspiderId}.html'><img class="borderless" src="${resource(dir:'images',file:'chemspider.png')}" alt="view" /></a></g:if>
					</td>
				</tr>	
				<g:if test="${msnfiles}">
					<tr>
						<td></td>
						<td>
							<h2><g:message code="common.label.msnfiles" /></h2>
							<g:each in="${msnfiles}" var="msnfile">
								<g:link controller="msnfile" action="preview" id="${msnfile.id}">${msnfile.name}</g:link><br />
							</g:each>
						</td>
					</tr>																
				</g:if>
			</table>
			<br /><br /><br /><br /><br /><br /><br /><br />
		</g:if><g:else>no compound found!</g:else>
	</div>		
  </body>
</html>	    