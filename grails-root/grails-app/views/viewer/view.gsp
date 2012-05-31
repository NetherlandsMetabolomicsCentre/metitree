<html>
  <head>
	<meta name="layout" content="msnviewer" />
	<script type="text/javascript" src="${serverUrl}/js/raphael.js" ></script>
 	<script type="text/javascript" src="${serverUrl}/js/MSnViewer.js"></script>
	<script type="text/javascript">
		function run(){   
			var jsonString_${nameTree} = ${jsonTree};
			drawing_${nameTree} = new MSnViewer(jsonString_${nameTree}, "${nameTree}");
		}
	</script> 	 	       
  </head>
<body>
<div>
	
<input type="text" id="min" name="min">
<input type="text" id="max" name="max">
<input type="button" onclick="drawing_${nameTree}.zoom()" value="Zoom">
<input type="button" name="show_all" value="Show All" class="button" onclick="drawing_${nameTree}.DrawFullSpectraOnCurrentView()" />
<input type="button" name="show_cum" value="Show Cumalitive" class="button" onclick="drawing_${nameTree}.DrawCumulativeSpectra()" />
<br/>
</div>
<div id="${nameTree}"></div>
</body>
</html>