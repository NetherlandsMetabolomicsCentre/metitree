<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="layout" content="metitree" />   
    <resource:tooltip />
  </head>
  <body>
   	<div class="thin_green_border" style="margin-top: 55px;">
		<g:if test="${session.member.usergroup.name == 'NMC (Netherlands Metabolomics Centre)'}">
			<g:if test="${processjob.lastStatus.toInteger() >= 2} ">
		    	<div style="float: right;">    		
		    		<g:link controller="page" action="database" params="[processjob: processjob.id]">
		    			<img class="borderless" src="${resource(dir:'images',file:'add.gif')}" alt="delete" /> add file(s) to a database    			
		    		</g:link>
		    	</div>
	    	</g:if>
	    </g:if>	   	
					
	    <p style="margin-left: 5px; color: red; font-weight: bold;">${flash.message}</p> 					
					
		<g:set var="processJobStatus" value="${g.createLink(controller: 'processJob', action: 'processjobstatus', id: processjob.id, base: resource(dir:''))}" />
		<div id="processjobstatus">
			<img src="${resource(dir:'images',file:'spinner.gif')}" alt="metItreeDB" /> loading...
		</div>			
	</div> 
	
    <script type="text/javascript">
			function AjaxReLoad(){
				var xmlHttp;
					try{ xmlHttp=new XMLHttpRequest(); } // Firefox, Opera 8.0+, Safari
					catch (e){
						try{ xmlHttp=new ActiveXObject("Msxml2.XMLHTTP"); } // Internet Explorer
						catch (e){
						    try{ xmlHttp=new ActiveXObject("Microsoft.XMLHTTP"); }
							catch (e){
								alert("No AjaxReLoad!?");
								return false;
							}
						}
					}
				
				xmlHttp.onreadystatechange=function(){
					if(xmlHttp.readyState==4){
						document.getElementById('processjobstatus').innerHTML=xmlHttp.responseText;
						setTimeout('AjaxReLoad()',2500);
					}
				}
				xmlHttp.open("GET","${processJobStatus}",true);
				xmlHttp.send(null);
			}
			
			window.onload=function(){ setTimeout('AjaxReLoad()',100); }
	</script> 
  </body>
</html>