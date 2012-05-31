<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="metitree" />
		<script type="text/javascript">
          function showSpinner() {
              document.getElementById('spinner').style.display = 'inline';
              document.getElementById('error').style.display = 'none';
          }

          function hideSpinner() {
              document.getElementById('spinner').style.display = 'none';
              document.getElementById('error').style.display = 'none';
          }

          function showError(e) {
              var errorDiv = document.getElementById('error')
              errorDiv.innerHTML = '<ul><li>'
                       + e.responseText + '</li></ul>';
              errorDiv.style.display = 'block';
          }
        </script>    
  </head>
  <body>	
	<div class="thin_green_border">
	
		<g:if test="${databases?.findAll{ it.msnfiles.size() >= 1}}">
			<table width="100%" border="0">
				<tr>
					<td valign="top" style="padding: 0px 10px;">
						<h2>1: <g:message code="page.query.file.selectdatabase" />:</h2>
						<div class="bordered" style="text-align: right; width:350px; height: 100%;">
							<g:form method="POST" controller="page" action="query" name="database_selector">
								<g:message code="common.label.database" /> : <g:select style="width: 150px; background: #fff;" name="database" from="${databases}" optionKey="id" optionValue="name" value="${session?.selectedQueryDatabase?.id}" noSelection="${['0':'please select one...']}" />
								<input type="submit" name="submit" value="<g:message code="common.label.select" />" id="submit" />
							</g:form>
						</div>
	
						<h2>2: <g:message code="page.query.file.selectdirectoryfile" />:</h2>					
						<div class="bordered" style="text-align: right; width:350px; height: 100%;">						
							<g:form method="POST" controller="page" action="query" name="directory_selector">
								<g:message code="common.label.directory" /> : <g:select style="width: 150px; background: #fff;" name="directory" from="${directories}" optionKey="id" optionValue="name" value="${session?.selectedQueryDirectory?.id}" noSelection="${['0':'please select one...']}"/>
								<input type="submit" name="submit" value="<g:message code="common.label.select" />" id="submit" />
							</g:form>
						
							<g:if test="${session.selectedQueryDirectory}">
								<g:if test="${session.selectedQueryDirectory.cmlfiles}">															
									<g:form method="POST" controller="page" action="query" name="file_selector">
										<g:message code="common.label.file" /> : <g:select style="width: 150px; background: #fff;" name="file" from="${session.selectedQueryDirectory.cmlfiles.sort { a,b -> a.name <=> b.name }}" optionKey="id" optionValue="displayname" value="${session?.selectedQueryFile?.id}" noSelection="${['0':'please select one...']}"/>
										<input type="submit" name="submit" value="<g:message code="common.label.select" />" id="submit" />
									</g:form>				
								</g:if>
								<g:if test="${session?.selectedQueryFile}">
									<br />
									<center>
										<g:form method="POST" controller="page" action="query" name="search">
											<g:hiddenField name="search" value="true"/>
											<g:actionSubmit action="query" value="search / refresh" onClick="showSpinner();" /> 
										</g:form>	
										<img id="spinner" style="display:none;" src="${createLinkTo(dir: 'images', file: 'spinner_loading.gif')}" alt="Spinner"/>
																	
										<!--<g:link action="query" params="['search': true]" class="thin_green_border"><font size="+1"><b>search</b></font></g:link>-->
									</center>
									<br />
								</g:if>													
							</g:if>						
						</div>
					</td>
					<td width="100%" valign="top" style="padding: 0px 10px;">
						<table>
							<tr>
								<td align="right" valign="top" nowrap><b><g:message code="page.query.file.databaseselected" /> :</b></td>
								<td valign="top">${session?.selectedQueryDatabase?.name}</td>
							</tr><tr>
								<td align="right" valign="top" nowrap><b><g:message code="page.query.file.directoryselected" /> :</b></td>
								<td valign="top">${session?.selectedQueryDirectory?.name}</td>
							</tr>						
								<g:if test="${session?.selectedQueryFile}">
									<tr>
										<td align="right" valign="top" nowrap><b><g:message code="page.query.file.fileselected" /> :</b></td>
										<td valign="top"><g:link controller="msnfile" action="preview" id= "${session?.selectedQueryFile?.id}">${session?.selectedQueryFile?.name}</g:link></td>
									</tr>
									<g:if test="${session?.selectedQueryFile?.compound()?.name}">
										<tr>
											<td align="right" valign="top" nowrap><b><g:message code="common.label.compound" /> :</b></td>
											<td valign="top">${session?.selectedQueryFile?.compound()?.name}</td>
										</tr>
									</g:if>									
									<g:if test="${session?.selectedQueryFile?.compound()?.inchiKey?.size() > 1}">
										<tr>
											<td align="right" valign="top" nowrap><b><g:message code="common.label.inchikey" /> :</b></td>
											<td valign="top">${session?.selectedQueryFile?.compound()?.inchiKey}</td>
										</tr>
									</g:if>														
									<g:if test="${session?.selectedQueryFile?.compound()?.inchi?.size() > 1}">
										<tr>
											<td align="right" valign="top" nowrap><b><g:message code="common.label.inchi" /> :</b></td>
											<td valign="top">${session?.selectedQueryFile?.compound()?.inchi}</td>
										</tr>
										<tr>
											<td valign="top" colspan="2">
												<div>
													<g:if test="${session?.selectedQueryFile?.compound()?.inchi}">
														<tt:img imglabel="${session?.selectedQueryFile?.compound().name}" imgurl="${session?.selectedQueryFile?.compound()?.imageUrl}" imgthumbsize="160px" imgsize="300px" />															
													</g:if>																							
												</div>
											</td>
										</tr>
									</g:if>							
								</g:if>
						</table>						
					</td>
				</tr><tr>				
					<td colspan="2" valign="top" width="100%" style="padding: 0px 10px;">
						<g:if test="${simScoreFileVsDatabase}">									
							<g:render template="/page/query/listing"/>						
						</g:if>
						<g:else>
							<g:if test="${session?.selectedQueryFile?.id && params.search}">
								<font color="red"><g:message code="page.query.file.unabletocompare" /></font>
							</g:if>
						</g:else>
					</td>
				</tr>		
			</table>
		</g:if>
		<g:else>
			<font color="red">
				<g:if test="${!databases.size()}">
					No database(s) found. Please setup a database first.
				</g:if>
				<g:else>
					No files found in any of the databases... please add files to a database first!
				</g:else>
			</font>
		</g:else>			
		<br /><br />	
	</div>
  </body>
</html>	    