<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="mimain" />
	<script type="text/javascript" src="/metitree/js/raphael.js" ></script>
 	<script type="text/javascript" src="/metitree/js/MSnViewer.js"></script>       
 	<resource:tabView />
  </head>
  <body>
    <h1 class="ptitle">File name: ${treefile.orgname}</h1>
    <h2 class="subtitle">File type: ${treefile.type}</h2>
    <p>${jsontrees.size()} Trees found</p>

	<richui:tabView id="tabView">
		<richui:tabLabels>
			<richui:tabLabel selected="true" title="Metadata" />
			<richui:tabLabel title="Downloads" />
			<richui:tabLabel title="Structure" />			
			<g:each in="${jsontrees}" var="jsontree">
				<richui:tabLabel title="Tree ${jsontree.key}"/>
    		</g:each>
			<g:each in="${jsonmastertrees}" var="jsonmastertree">
				<richui:tabLabel title="Master ${jsonmastertree.key}"/>
    		</g:each>    		
			
		</richui:tabLabels>
		
		<richui:tabContents>

			<richui:tabContent>
		    <!-- TAB 1 -->
			    <h2 class="title">Metadata</h2>
			    <table cellpadding="3" border="0">
				    <g:each in="${treefile.metamap}" var="metatag">
				    	<tr>
				    		<td><strong>${metatag.key}</strong></td>
				    		<td>${metatag.value}</td>
						</tr>    		
				    </g:each>
			    </table>
			</richui:tabContent>		    

		    <richui:tabContent>
		    <!-- TAB 2 -->
				<h2 class="title">Downloads</h2>    
    			<h2 class="subtitle"><a target="_blank" href="${resource(dir: treefile.uri, file: treefile.name)}"><img class="borderless" src="${resource(dir:'images',file:'disk.png')}" alt="Download" /></a> ${treefile.orgname} (mzXML)</h2>			    
    			<h2 class="subtitle"><a target="_blank" href="${resource(dir: treefile.uri, file: treefile.name)}.cml"><img class="borderless" src="${resource(dir:'images',file:'disk.png')}" alt="Download" /></a> ${treefile.orgname} (cml)</h2>
    			<h2 class="subtitle"><a target="_blank" href="${resource(dir: treefile.uri, file: treefile.name)}.tab"><img class="borderless" src="${resource(dir:'images',file:'disk.png')}" alt="Download" /></a> ${treefile.orgname} (tab)</h2>    			
    			<h2 class="subtitle"><a target="_blank" href="${resource(dir: treefile.uri, file: treefile.name)}.csv"><img class="borderless" src="${resource(dir:'images',file:'disk.png')}" alt="Download" /></a> ${treefile.orgname} (csv)</h2>    			
			</richui:tabContent>
		    
		    <richui:tabContent>
		    <!-- TAB 3 -->
				<h2 class="title">Structure as Indent</h2>    
			    <pre>${indent}</pre>
			</richui:tabContent>			    
		  
		    <!-- Tree TAB 4..n -->
		    <g:each in="${jsontrees}" var="jsontree">
		    	<richui:tabContent>
		    		<h2 class="title">Structure of Tree ${jsontree.key}</h2>		    			    		
					<iframe src="/metitree/viewer/view/?name=${jsontree.key}&tree=${jsontree.value.toString().bytes.encodeBase64().toString()}" width="100%" height="700"></iframe>
			</richui:tabContent>
		    </g:each> 
		    
		    <!-- MasterTree TAB 4..n -->
		    <g:each in="${jsonmastertrees}" var="jsonmastertree">
		    	<richui:tabContent>
		    		<h2 class="title">Structure of Master Tree ${jsonmastertree.key}</h2>		    			    				
					<iframe src="/metitree/viewer/view/?name=${jsonmastertree.key}&tree=${jsonmastertree.value.toString().bytes.encodeBase64().toString()}" width="100%" height="700"></iframe>
				</richui:tabContent>
		    </g:each>		     
    
		</richui:tabContents>
	</richui:tabView>
  </body>
</html>
