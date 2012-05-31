<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="metitree" />  
    <r:require modules="jquery-ui"/>	
  </head>
  <body> 	 
	<div class="thin_green_border">
		
		<script>$(function() { function runEffect() { var options = {};	$( "#effect_add" ).toggle( 'blind', options, 300 ); }; $( "#button_add" ).click(function() { runEffect(); return false; }); }); </script>						
		<div id="effect_add" style="display: none; border: thin solid #ffffff;">
			<h2>add file(s)</h2>		
			<div class="bordered"> 
				<g:uploadForm controller="msnfile" action="add" name="myUpload">
		   			<g:message code="page.directory.addfile" /> : <input type="file" name="fileUpload" />
		   			<input type="hidden" name="directory" value="${directory.id}" />
		   			<g:submitButton name="submit" value="submit" /> <small><g:message code="page.directory.multiplefiles" /></small>
				</g:uploadForm>			
			</div>
		</div>
		
		<h2><g:message code="page.directory.files" /> in: <i>${directory.name}</i> (<g:link action="data"><g:message code="common.label.changedirectory" /></g:link> / <a href="#" id="button_add"><g:message code="common.label.addfiles" /></a>)</h2>
		<div class="bordered"> 
			<div style="text-align: center;">
				<g:message code="common.label.sortby" />: <g:link action="directory" id="${directory.id}" params="[sortby:'name', show: params.show]"><g:message code="common.label.name" /></g:link> / <g:link action="directory" id="${directory.id}" params="[sortby:'date', show: params.show]"><g:message code="common.label.date" /></g:link>
			</div>

			<table>
				<tr>
					<th></th>				
					<th><g:message code="common.label.id" /></th>
					<th><g:message code="common.label.file" /></th>
					<th><g:message code="common.label.name" /></th>	
					<th><g:message code="common.label.pubchem" /></th>				
					<th><g:message code="common.label.chemspider" /></th>
					<th><g:message code="common.label.owner" /></th>
					<th><g:message code="common.label.delete" /></th>
				</tr>
					<tr>
						<td colspan="8" valign="top">
							<!-- sort the files in the correct order -->
							<g:if test="${params.sortby == 'name'}"><g:set var="msnfiles" value="${directory.toplevelmsnfiles.sort{ a,b -> a.name <=> b.name }}" /></g:if>
							<g:else><g:set var="msnfiles" value="${directory.toplevelmsnfiles.sort{ a,b -> b.id <=> a.id }}" /></g:else>
						</td>
					</tr>				
				<g:each in="${msnfiles}" status="i" var="msnfile">				
					<tr>
						<td valign="bottom">
							<a href="#" id="button_${i}"><img class="borderless" width="15px" src="${resource(dir:'images',file:'view.png')}" alt="view" />	</a>
						</td><td valign="bottom">							
							${msnfile.id}
						</td><td valign="bottom">							
							${msnfile.name}							
						</td><td valign="bottom" width="100%">
							${msnfile.compound()?.name}
						</td><td valign="bottom">
							<g:if test="${msnfile.compound()?.inchiKey}">
								<a href='http://www.ncbi.nlm.nih.gov/sites/entrez?cmd=search&db=pccompound&term="${msnfile.compound()?.inchiKey}"[InChIKey]' target="_blank">
									<img class="borderless" height="25px" src="${resource(dir:'images',file:'pubchemlogob.gif')}" alt="${msnfile.compound()?.inchiKey}" longdesc="${msnfile.compound()?.inchiKey}" />
								</a>
							</g:if>
						</td><td valign="bottom">							
							<g:if test="${msnfile.compound()?.chemspiderId}">
								<a href='http://www.chemspider.com/Chemical-Structure.${msnfile.compound()?.chemspiderId}.html' target="_blank">
									<img class="borderless" height="25px" src="${resource(dir:'images',file:'chemspider.png')}" alt="${msnfile.compound()?.chemspiderId}" longdesc="${msnfile.compound()?.inchi}" />
								</a><br />										
							</g:if>	
						</td><td valign="bottom">																
							${(msnfile.member.id == session.member.id) ? '<i>you</i>' : msnfile.member.name}									
						</td><td valign="bottom">										
							<g:if test="${msnfile.member.id == session.member.id}">
								<g:link controller="msnfile" action="delete" id="${msnfile.id}">
									<img class="borderless" width="15px" src="${resource(dir:'images',file:'delete.png')}" alt="delete" />							
								</g:link>
							</g:if>
						</td>
					</tr>	
					<tr>
						<td colspan="8" valign="top" class="line">
							<script>$(function() { function runEffect() { var options = {};	$( "#effect_${i}" ).toggle( 'blind', options, 300 ); }; $( "#button_${i}" ).click(function() { runEffect(); return false; }); }); </script>						
							<div style="width: 100%;">
								<div id="effect_${i}" style="display: none; border: thin solid #ffffff;">
									<p>
										<table width="100%">
											<tr>
												<th></th>
												<th class="sub"><g:message code="common.label.2d" /></th>
												<th class="sub"><g:message code="common.label.inchi" /></th>
												<th class="sub"><g:message code="common.label.comments" /></th>
											</tr>
											<tr>
												<td width="1%"></td>
												<td width="32%" valign="top">
													<g:if test="${msnfile.compound()}">
														<img style="border: 0px; max-width: 250px; max-height:250px; width: expression(this.width > 250 ? 250: true);" src="${msnfile.compound().imageUrl}" />
													</g:if>
												</td>
												<td width="33%" valign="top">
													<g:if test="${msnfile.member.id == session.member.id}">
														<g:form id="frm_${msnfile.id}" name="update_inchi" action="setinchi" controller="msnfile">
															<g:textArea name="inchi" style="height: 120px; width: 250px;">${msnfile.compound()?.inchi}</g:textArea>
															<g:hiddenField name="msnfile" value="${msnfile.id}"></g:hiddenField>
															<g:hiddenField name="show" value="${params.show}"></g:hiddenField>
															<g:hiddenField name="sortby" value="${params.sortby}"></g:hiddenField>						
															<br /><g:submitButton name="update/save"></g:submitButton>
														</g:form>
													</g:if>
													<g:else>
														${msnfile.compound()?.inchi}
													</g:else>												
												</td>
												<td width="33%" valign="top">
													<g:if test="${msnfile.member.id == session.member.id}">
														<g:form id="frm_${msnfile.id}" name="update_comments" action="setcomments" controller="msnfile">
															<g:textArea name="comments" style="height: 120px; width: 250px;">${msnfile.comments ?: ''}</g:textArea>
															<g:hiddenField name="msnfile" value="${msnfile.id}"></g:hiddenField>
															<g:hiddenField name="show" value="${params.show}"></g:hiddenField>
															<g:hiddenField name="sortby" value="${params.sortby}"></g:hiddenField>
															<br /><g:submitButton name="update/save"></g:submitButton>
														</g:form>
													</g:if>
													<g:else>
														${msnfile.comments}
													</g:else>
												</td>												
											</tr>
										</table>																		
									</p>
								</div>
							</div>
						</td>
					</tr>
				</g:each>
			</table>
		</div>			
	</div>
  </body>
</html>