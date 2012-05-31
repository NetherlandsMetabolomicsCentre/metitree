<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="mimain" />
    
		<script language="JavaScript">		
			<!-- Begin
			function checkAll(field){
				for (i = 0; i < field.length; i++)
					field[i].checked = true ;
			}
			
			function uncheckAll(field){
				for (i = 0; i < field.length; i++)
					field[i].checked = false ;
			}
			//  End -->
		</script>
    
    
  </head>
  <body>

	<div dojoType="dijit.layout.ContentPane" title="File manager" style="padding: 15px;">
	    <h1 class="ptitle">MSn files</h1>
	    
	    <h2 class="subtitle">Upload a mzXML file</h2>
	    <form action="" method="POST" enctype=multipart/form-data>
	    <table width="100%" style="border: thin solid #CDCDCD">
	    	<tr>
	    		<td nowrap width="1" valign="top" align="left"><strong>file</strong></td>
	    		<td valign="top" width="1"></td>
	    		<td nowrap valign="top" align="left"><strong>comments</strong></td>			    		    	
	    	</tr>
	    	<tr>
	    		<td valign="top" width="1"><input style="width: 300px;" type="file" name="fileUpload"></td>
	    		<td valign="top" width="1"><input type="submit" name="Submit" value="submit"></td>    		
	    		<td valign="top"><textarea style="height: 40px; width: 100%;"  name="comments"></textarea></td>			    		
	    	</tr>    	
	    	<tr>
	    		<td nowrap valign="top" align="right"></td>
	    		
	    	</tr>    	                                   
	    </table>
	    </form>     		   		
	    		
		<g:if test="${treefiles.size()}">
		
	    	<form action="reprocess" name="files" method="POST">	
			    
			    <table style="border: thin solid #CDCDCD" width="100%" cellpadding="3px" cellspacing="0px">
			    		<tr><td colspan="7"><h2 class="subtitle">Available mzXML file(s)</h2></td></tr>
			            <tr>
			                    <th width="40px">View</th>            
			                    <th width="40px">Process</th>                   
			                    <th width="140px">
			                    	<g:if test="${treefiles.size() > 1}">
			                    		<input type=button name="CheckAll" value="+" onClick="checkAll(document.files.file)"><input type=button name="UnCheckAll" value="-" onClick="uncheckAll(document.files.file)">
			                    	</g:if>
								</th>                    
			                    <th nowrap>File</th>
			                    <th width="100%">Comments</th>                    
			                    <th nowrap width="150px">Created</th>
			                    <th nowrap width="40px"></th>                    
			            </tr>
			    <g:each in="${treefiles}" var="treefile">
			            <tr>
			                    <td nowrap>
			                    	<g:if test="${treefile.viewable}">
			                    		<g:link action="view" id="${treefile.id}"><img height="25px" class="borderless" src="${resource(dir:'images',file:'view.png')}" alt="View structure" title="View structure" /></g:link>
			                    	</g:if>
			                  		<g:if test="${!treefile.viewable && treefile.parsed}">
			                    		<img height="25px" class="borderless" src="${resource(dir:'images',file:'error.gif')}" alt="Parse error" title="Parse error" />
			                    	</g:if>
			                    </td>            
			                    <td nowrap><g:link action="reprocess" id="${treefile.id}"><img height="25px" class="borderless" src="${resource(dir:'images',file:'refresh.png')}" alt="(Re-)Process" title="(Re-)Process" /></g:link></td>
								<td nowrap width="140px" align="middle"><input type="checkbox" name="file" value="${treefile.id}" /></td>                    
			                    <td nowrap>${treefile.orgname}</td>
			                    <td width="100%">${treefile.comments}</td>                    
			                    <td nowrap>${treefile.dateCreated}</td>
			                    <td nowrap><g:link action="delete" id="${treefile.id}" onclick="return confirm('Are you sure you want to delete?')"><img height="25px" class="borderless" src="${resource(dir:'images',file:'delete.png')}" alt="Delete" title="Delete" /></g:link></td>                    
			            </tr>
			    </g:each>
			        <tr>
			                <td nowrap></td>            
			                <td nowrap></td>
							<td align="middle"></td>                    
			                <td width="200px"></td>
			                <td width="150px"></td>                    
			                <td nowrap></td>
			                <td nowrap></td>                    
			        </tr>    
			    </table>
			    <table style="border: thin solid #CDCDCD" width="100%" cellpadding="3px" cellspacing="5px">
			    	<tr>
			    		<td><input type="Submit" name="submit" value="Process" /></td>
			    		<td><input type="Submit" name="submit" value="Compare" /></td>		    		
			    		<td width="100%"></td>
			    	</tr>
			    </table>		    
	    	</form>		    
		</g:if>
	</div>
  </body>
</html>