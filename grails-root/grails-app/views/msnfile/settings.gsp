<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="metitree" />   
    <resource:tooltip />
  </head>
  <body>  				
	<div class="thin_green_border">
	
	<h2>Define settings</h2>	
	
	<table width="100%" border="0">
		<tr>
			<td valign="top" style="padding: 0px 10px;" nowrap>				
		    	<p>Please change the settings or continue with the defaults</p>
				<g:form name="process" url="[action:'settings',controller:'msnfile']"> 				  	
		
					<div class="bordered">				
						<b>Peak picking settings</b><tt:simple code="page.settings.peakpicking" /><br />
				        MZ gap (bin size as m/z) : <input type="text" name="settings_mzgap" value="${settings.mzgap ? settings.mzgap : 5}"> m/z <small>(5 e.g 0.5 m/z)</small><br />	        
				        Signal to noise threshold : 
				        <select name="settings_snthresh">
							<g:each in="${snthreshs}" var="snthresh"><option ${(settings.snthresh.toString() == snthresh.toString()) ? "selected=selected" : ""} value="${snthresh}">${snthresh}</option></g:each>
						</select> %<br />
				    </div>
			        
			        <div class="bordered">
				        <b>Filter settings</b><tt:simple code="page.settings.filter" /><br />					
						Elements : <input type="text" name="settings_elements" value="${settings.elements ? settings.elements : "C1..50,H1..100,N0..30,O1..30"}"><small>('C1..50,H1..100,N0..10,O0..10' or 'C10,N4,H2')</small><br />
						Rules :<br /> 
							<input type="checkbox" name="settings_rulenitrogenr" ${(!settings || settings?.rulenitrogenr) ? "checked=checked" : ""} value="nitrogenR"> nitrogenR<br /> 
							<input type="checkbox" name="settings_rulerdber" ${(!settings || settings?.rulerdber) ? "checked=checked" : ""} value="RDBER"> RDBER<br />
					</div>
					
					<div class="bordered">
						<b>Mass accuracy settings</b> (ppm)<tt:simple code="page.settings.accuracy" /><br />
				        <g:each in="${levels}" var="level">     
							MS Level ${level} : 
							<select name="settings_accuracy-L${level}">			    
						    	<option value="5"> - - - - default - - - - </option>				      
								<g:each in="${accuracy}" var="acc"><option <% if (settings."accuracy-L${level}" == "${acc}") { print "selected=selected" } %> value="${acc}">${acc}</option></g:each>
						    </select><br />							  
						</g:each>
					</div>
								
					<input type="submit" name="Submit" value="Process"></td>
					
				</g:form>  			  
			</td>
			<!--
			<g:if test="${availableSettings}">
				<td valign="top" style="padding: 0px 10px;">
					<p>Use settings from previous processing jobs.</p>
					
					<g:form name="process" url="[action:'settings',controller:'msnfile']">				
						<select name="availableSetting">			    			      
							<g:each in="${availableSettings}" var="aSettings"><option value="${aSettings.id}">${aSettings.shortname}</option></g:each>
						</select><br />
						<input type="submit" name="Submit" value="Load">
					</g:form>  	
				</td>
			</g:if>		
			-->		
		</tr>
	</table>
	</div>
  </body>
</html>