<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="mimain" />   
    <resource:tooltip />
  </head>
  <body>
    <h1 class="ptitle">Process</h1>

	<div style="padding: 10px; margin: 10px auto 10px auto; border: thin dotted #CDCDCD;">
	
	  <p style="font-size:20px; color: red; font-weight: bold; text-align:center;">${flash.message}</p>

	  <table cellpadding="5px">
	  	<tr>
	  		<td valign="top">
	  		<h3 class="title">Define settings</h3>
    		<p>Please change the settings or continue with the defaults</p>
    		  <g:form name="process" url="[action:'process',controller:'file']"> 		
			  	<g:each in="${treefiles}" var="treefile">
			  		<input type="hidden" name="treefiles" value="${treefile}" />
			  	</g:each>
			  	
				<table cellpadding="3px">
							
				<tr>
					<td colspan="2"><b>Peak picking settings</b></td>
				</tr>					
		
		          <tr>
		            <td nowrap valign="top">MZ gap (bin size)</td>
		            <td valign="top">
						<input type="text" name="settings_mzgap" value="${settings.mzgap ? settings.mzgap : 5}"><br /><small>(5 e.g 0.5 m/z)</small>
		            </td>
		          </tr>
		
		          <tr>
		            <td nowrap valign="top">Signal to noise threshold</td>
		            <td valign="top">
		              <select name="settings_snthresh">
		                <g:each in="${snthreshs}" var="snthresh"><option ${(settings.snthresh.toString() == snthresh.toString()) ? "selected=selected" : ""} value="${snthresh}">${snthresh}</option></g:each>
		              </select>
		            </td>
		          </tr>          
		
					<tr>
						<td colspan="2"> - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - </td>
					</tr>
		
					<tr>
						<td colspan="2"><b>Filter settings</b></td>
					</tr>	
		          
		          <tr>
		            <td nowrap valign="top">Elements</td>
		            <td nowrap>
						<input type="text" name="settings_elements" value="${settings.elements ? settings.elements : "C1..50,H1..100,N0..30,O1..30"}"><br /><small>('C1..50,H1..100,N0..10,O0..10' or 'C10,N4,H2')</small>
		            </td>
		          </tr>
		          
		          <tr>
		            <td nowrap valign="top">Rules</td>
		            <td>
						<input type="checkbox" name="settings_rules" ${(!settings || settings?.rules?.contains('nitrogenR')) ? "checked=checked" : ""} value="nitrogenR"> nitrogenR<br />
						<input type="checkbox" name="settings_rules" ${(!settings || settings?.rules?.contains('RDBER')) ? "checked=checked" : ""} value="RDBER"> RDBER<br />
		            </td>
		          </tr>                    
		          
					<tr>
						<td colspan="2"> - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - </td>
					</tr>          
		
					<tr>
						<td colspan="2"><b>Mass accuracy settings</b> - <i>default accuracy is set to 5</i></td>
					</tr>
			        <g:each in="${levels}" var="level">     
						<tr>
						  <td valign="top">MS Level ${level}</td>
						  <td>
						    <select name="settings_accuracy-L${level}">			    
						      <option value=""> - - - - default - - - - </option>				      
								<g:each in="${accuracy}" var="acc">		      																		
									<option <% if (settings."accuracy-L${level}" == "${acc}") { print "selected=selected" } %> value="${acc}">${acc}</option>
								</g:each>
						    </select>
						  </td>
						</tr>
					</g:each>
					<tr>
						<td colspan="2"> - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - </td>
					</tr>	  	
			         <tr>
			           <td valign="top">			           
			           	<b>save as... (optionally)</b><br />           	
			           	<input type="text" name="save_as"><br />
			           	<small>enter a name to store these settings permanently</small>
			           </td>
			           <td valign="top"><input type="submit" name="Submit" value="Process"></td>
			         </tr>							          
			    </table>			    
				</g:form>  		
	  		</td>
	  		<td valign="top">	
			<h3 class="title">Recent settings</h3>
			<p>settings used for recent processing jobs</p>
			  	<g:each in="${processsettings}" var="processsetting">
		  			<g:link action="process" params="[load: processsetting.id, treefiles: treefiles]" onclick="return confirm('Are you sure you want to load these settings?')">				  			  	
				  		<div class="setting_box">			  		
							<font style="float: right;" color="#000">Load...</font>
				  			<g:each in="${processsetting.settings}" var="setting">
				  				<g:if test="${setting.value}">
				  					<small> ${setting.key}</small> : <small>${setting.value}</small><br />
				  				</g:if>
				  			</g:each>			
							<font style="float: right;" color="grey">${processsetting.dateCreated}</font><br/>					  			  			
				  		</div>			  		 
					</g:link>					  						  		
			  	</g:each>	  			  		
	  		</td>
	  		<td valign="top">	
			<h3 class="title">Saved settings</h3>
			<p>settings saved from recent processing jobs</p>			
			  	<g:each in="${processsettingsSaved}" var="processsettingSaved">
		  			<g:link action="process" params="[load: processsettingSaved.id, treefiles: treefiles]" onclick="return confirm('Are you sure you want to load these settings?')">
				  		<div class="setting_box">
							<font style="float: right;" color="#000">Load...</font>					  		
				  			<font color="#4D4D4D"><strong>${processsettingSaved.label}</strong></font><br/>
				  			<g:each in="${processsettingSaved.settings}" var="setting">
				  				<g:if test="${setting.value}">
				  					<small> ${setting.key}</small> : <small>${setting.value}</small><br />
				  				</g:if>
				  			</g:each>	
							<font style="float: right;" color="grey">${processsettingSaved.dateCreated}</font><br/>					  					  			
				  		</div>			  		 
					</g:link>					  					  		
			  	</g:each>	  			  		
	  		</td>	  		
	  	</tr>			  	
	  </table>
	  
	</div>  

  </body>
</html>