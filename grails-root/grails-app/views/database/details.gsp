<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="metitree" />   
    <resource:tooltip />
  </head>
  <body>
	<div class="thin_green_border">
		<h2>database: ${database.name}</h2>	

		<div class="bordered">
			<b>filter on job</b><br />
			<g:form controller="database" action="details" id="${database.id}">
				<g:select style="width: 350px; background: #fff;" noSelection="${['null':'All jobs']}" value="${jobSelected?.id}" name="jobSelected" from="${jobs}" optionKey="id" optionValue="name" onchange="this.form.submit()" />
				<g:submitButton name="submit" value="filter" />
			</g:form>	
		</div>
		
		<h2>Add files to database</h2>
		<div class="bordered">		
			<table>
				<tr>
					<td style="padding: 10px;" valign="top" width="500px">
						
						<h2>Available files</h2>
						<g:link style="float: right" controller="database" action="details" id="${database.id}" params="['jobSelected':jobSelected?.id, 'addCandidates':true]"><img class="borderless" width="15px" src="${resource(dir:'images',file:'add.gif')}" alt="view" /> add all</g:link>
						<p>Files that can be added to this database.</p>
					    <table style="padding: 5px;" width="100%">
					            <tr>
				                    <th></th>                    
				                    <th nowrap>File</th> 
				                    <th nowrap>Job</th>                  
				                    <th nowrap>Created</th>                   
					            </tr>			
							<g:each in="${candidates.findAll{ it.processjob?.active == true }.sort { a,b -> a.name <=> b.name }}" var="msnfile">
								<g:if test="${!jobSelected || (jobSelected?.id == msnfile.processjob.id)}">
						            <tr>           
										<td nowrap align="middle" width="40px"><g:link controller="database" action="addmsnfile" id="${database.id}" params="[msnfile: msnfile.id, 'removeListed':false]"><img class="borderless" width="15px" src="${resource(dir:'images',file:'add.png')}" alt="add" /></g:link></td>                    
					                    <td nowrap>${msnfile.name}</td>  
										<td nowrap><g:link controller="processJob" action="details" id="${msnfile.processjob.id}">${msnfile.processjob.id}</g:link> <g:link controller="processJob" action="details" id="${msnfile.processjob.id}"><img class="borderless" width="15px" src="${resource(dir:'images',file:'view.png')}" alt="view" /></g:link></td>                 
					                    <td nowrap>${msnfile.dateCreated}</td>                   
						            </tr>
					            </g:if>
					    	</g:each>   
					    </table>				    
					    					
					</td><td style="padding: 10px;" valign="top" width="500px">
									
						<h2>Files in database</h2>
						<g:link style="float: right" controller="database" action="details" id="${database.id}" params="['jobSelected':jobSelected?.id, 'removeListed':true]"><img class="borderless" width="15px" src="${resource(dir:'images',file:'delete.gif')}" alt="view" /> remove all</g:link>
						<p>The data in this database originates from the files below.</p>
					    <table style="padding: 5px;" width="100%">
					            <tr>          
					            	<th></th>         
				                    <th nowrap>File</th>                   
				                    <th nowrap>Job</th>
				                    <th nowrap>Created</th>                   
					            </tr>			
							<g:each in="${database.msnfiles.findAll{ it.processjob?.active == true }.sort { a,b -> a.name <=> b.name }}" var="msnfile">
								<g:if test="${!jobSelected || (jobSelected?.id == msnfile.processjob.id)}">
						            <tr>     
	                                    <td nowrap align="middle" width="40px"><g:link controller="database" action="removemsnfile" id="${database.id}" params="[msnfile: msnfile.id, 'removeListed':false]"><img class="borderless" width="15px" src="${resource(dir:'images',file:'delete.png')}" alt="add" /></g:link></td>
						            	<td nowrap>${msnfile.name}</td>
					                    <td nowrap><g:link controller="processJob" action="details" id="${msnfile.processjob.id}">${msnfile.processjob.id}</g:link> <g:link controller="processJob" action="details" id="${msnfile.processjob.id}"><img class="borderless" width="15px" src="${resource(dir:'images',file:'view.png')}" alt="view" /></g:link></td>                   
					                    <td nowrap>${msnfile.dateCreated}</td>                   
						            </tr>
						        </g:if>
					    	</g:each>   
					    </table>								
											
					</td>
				</tr>
			</table>
		</div>					
	</div>  

  </body>
</html>