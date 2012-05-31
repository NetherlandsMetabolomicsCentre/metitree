<g:if test="${session.member}">
	<g:set var="runningjobs"
		value="${g.createLink(controller: 'processJob', action: 'runningjobs', base: resource(dir:''))}" />

	<script type="text/javascript">
	function AjaxReLoadMain(){
		var xmlHttp;
			try{ xmlHttp=new XMLHttpRequest(); } // Firefox, Opera 8.0+, Safari
			catch (e){ try{ xmlHttp=new ActiveXObject("Msxml2.XMLHTTP"); } // Internet Explorer
				catch (e){ try{ xmlHttp=new ActiveXObject("Microsoft.XMLHTTP"); }
					catch (e){ alert("No AjaxReLoad!?"); return false;
					}
				}
			}
		
		xmlHttp.onreadystatechange=function(){ if(xmlHttp.readyState==4){ document.getElementById('rjobs').innerHTML=xmlHttp.responseText; setTimeout('AjaxReLoadMain()',10000); } }
		xmlHttp.open("GET","${runningjobs}",true);
		xmlHttp.send(null);
	}
	
	window.onload=function(){ setTimeout('AjaxReLoadMain()',10); }
	</script>

	<div id="rjobs"></div>
</g:if>