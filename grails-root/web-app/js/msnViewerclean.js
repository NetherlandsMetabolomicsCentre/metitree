function MSnViewer(jsonString) {
	json = AddNumberOfChildrenToTree(jsonString);
	AddLevelOfNode(json);
	addOrderOfWholeTree([""]);                                       
		
	// create paper as a global variable so all functions have access to draw
	paper = Raphael(10, 50, 720, 900);
	// a bookkeeping array for drawn objects
	nodesTracker ={};
	spectralTracker =[];
	lines=[];
	neutralLoss=[];
	//drawPartTreeWholeTreeSwitch();
	// draw spectra for upper level
	SPECTRAL_WIDTH = 500;
	SPECTRAL_HEIGTH = 120;
	
    	
   
	//drawWholeTree();
	var absoluteMother="";
	var subset = selectjson(json, "parent", absoluteMother); 
	
	UpdateSpectra(absoluteMother);
	// set y distance for all drown nodes
    drawWholeTree();

	//drawTree(subset); 



}

function drawPartTreeWholeTreeSwitch(){
	var button=paper.text(620,50,"Whole tree").click(function() {
	if(this.attr().text=="Whole tree"){
		removePartialTree()	
		drawWholeTree();
		 this.attr({text: "partial tree"});
	}else{
	this.attr({text: "Whole tree"});
	//remove whole node tree
	removeWholeNodeTree();	
	var absoluteMother="";
	var subset = selectjson(json, "parent", absoluteMother);
	UpdateSpectra(absoluteMother);

	drawTree(subset);
	whenClickedOnRect(11);
	}	
});
	
	return (button);

}
function removeWholeNodeTree(){
	for (node in json){


		json[node].wholetreenode.animate( {
			opacity : 0
		}, 250, function() {

			this.remove();
			

		});	
	}
	for (spec in lines ){


		
		     lines[spec].remove();
	}

}
function removePartialTree(){
 for (node in nodesTracker){


	
	deletenode(node);	
	}

}
function roundWithDecimals(number,decimals){
	var decimalMultilpy=Math.pow(10,decimals);
	var rounded= Math.round(number*decimalMultilpy)/(decimalMultilpy);
	return rounded;
	}

function addOrderOfWholeTree(parentids){
			var newparents=[];
			var orderOfElement=0;
		  for(var t =0; t<parentids.length;t++) {
	      	var subset = selectjson(json, "parent", parentids[t]);
		  	subset=subset.sort(sortByMass);
			

		  	for(var i=0;i<subset.length;i++){
			   json[subset[i].id].order= orderOfElement;
			   orderOfElement++;
				if (json[subset[i].id].children>0){
					  newparents.push(subset[i].id);
				}//end if there are children
		       }//end sorted loop
		      }//end parentsid loop
		if (newparents.length!==0){ 
			

			
			  addOrderOfWholeTree(newparents);
		}
}
function AddNumberOfChildrenToTree(json) {
	// c.show();
	for ( var key in json) {
		json[key].children = selectjson(json, "parent", json[key].id).length;
	}
	return (json);

}




function AddLevelOfNode(json) {
	// find first level
	var levelcounter=0;            
	var parentArray=[];
	parentArray[levelcounter] = getArrayFrom2DArrayAsKey(selectjson(json, "parent", ""),"id");


	//last itteration must contain a item
	  
	while(parentArray[levelcounter]!==undefined){
		
	    var ids=[]; 
		for ( var i = 0; i < parentArray[levelcounter].length; i++) {
			var foundbyparents = selectjson(json, "parent", parentArray[levelcounter][i]);
			
				for (var p=0;p<foundbyparents.length;p++){
			          ids.push(foundbyparents[p].id);
				}
			}
			//update level to new setting 
			levelcounter++;
			if(ids.length>=1){
				parentArray[levelcounter]=ids;
			}
	}  
	//loop throught all levels
	for(var l=0;l<parentArray.length;l++){
		//loop through all ids of level
		  for(var k=0;k<parentArray[l].length;k++){
			      //find the record 
					  //parentArray[l][k]
					var found=false;
					//var jsonArrayPointer=0;
					//while(!found){
						  //
						//if(parentArray[l][k]==json[jsonArrayPointer].id){
								json[parentArray[l][k] ].level=l;
						 //   	found=true;	
					   // }
					 //jsonArrayPointer++;

				   // }
		   } 
	}
	return (json);
}

function inchi2ElemenalComposition(inchi){
		var ElemenalComposition="";
		if(inchi.length>0){
		ElemenalComposition=inchi.match("\/([0-9A-z]+)\/");
		//check there are some results		
		if (ElemenalComposition==null){
			//probably not a valid last part of inchi
			ElemenalComposition=inchi.match("\/([0-9A-z]+)");
			}
			var cleanElemetalComposition="";
		if(ElemenalComposition.length>=1){
			cleanElemetalComposition=ElemenalComposition[1];
		}
		}
		return(cleanElemetalComposition);

}

function sortByParentThenMass(a, b) {
	var x = a.parent;
	var y = b.parent;
	return ((x < y) ? -1 : ((x > y) ? 1 : sortByMass(a, b)));
}
function sortByMass(a, b) {
	var x = a.mass;
	var y = b.mass;
	return x-y;
}
function sortByOrder(a, b) {
	var x = parseInt(a.order,10);
	var y = parseInt(b.order,10);
	 return x-y;
}

function drawWholeTree(){
	var canvaswidth=600;
	var spacing=10;
	for (var item in json){
		json[item].wholetreenode=createWholeTreeNode(item);
	}
	var maxnumberoflevels=findmax(getArrayFrom2DArrayAsKey(json,"level"));


	var longestlevel=FindLongestLevel(spacing,maxnumberoflevels);
	var longestLevelJSON=selectjson(json,"level",longestlevel);
	var sortedlevel=longestLevelJSON.sort(sortByOrder);
	var newXposition=100;
	for (var i=0;i<sortedlevel.length;i++){

		var currentnode=selectjson(json,"id",sortedlevel[i].id)[0];
		currentnode.wholetreenode.attr({x:newXposition,y:(longestlevel*75)+200});
		newXposition=newXposition+currentnode.wholetreenode.getBBox().width+spacing;
	
	}
	var currentlevel=longestlevel-1;
	while (currentlevel>=0){
		var rightEndLastBox=0;
		var currentlevels=selectjson(json,"level",currentlevel);
		sortedlevel=currentlevels.sort(sortByOrder);
		for (var i=0;i<sortedlevel.length;i++){

			var currentnode=selectjson(json,"id",sortedlevel[i].id)[0];
			var children=selectjson(json,"parent",currentnode.id);
			var totalwidth=0;

			if(children.length===0){
				newXposition=currentnode.wholetreenode.getBBox().width/2+rightEndLastBox+5;
			}
			else{
		
				for(c=0;c<children.length;c++){
					totalwidth=totalwidth+(children[c].wholetreenode.getBBox().width/2)+children[c].wholetreenode.getBBox().x;
				}
				newXposition=Math.round(totalwidth/children.length);
			}
			 
	var overlap=rightEndLastBox-(newXposition-(currentnode.wholetreenode.getBBox().width/2));
	if (overlap>0){
		//box is overlaping with last drawn shift, it to the right


		 newXposition=newXposition+overlap;

	}


	currentnode.wholetreenode.attr({x: newXposition,y: currentlevel*75+200});
   //   attr({fill: "#000", stroke: "#f00", opacity: 0.5});
   rightEndLastBox=currentnode.wholetreenode.getBBox().width+currentnode.wholetreenode.getBBox().x;
	
	}
	currentlevel--;
  
	
	

}
  //draw levels under (in y direction)
currentlevel=longestlevel+1;   
   while(currentlevel<=maxnumberoflevels){ 
		rightEndLastBox=0;
		

	
	  	var currentlevels=selectjson(json,"level",currentlevel);
		sortedlevels=currentlevels.sort(sortByOrder);


		for (var i=0;i<sortedlevels.length;i++){

			var currentnode=selectjson(json,"id",sortedlevels[i].id)[0]; 



			var parent=selectjson(json,"id",currentnode.parent);
			var children=selectjson(json,"parent",currentnode.parent);
			if (children.length===1){
						var parentArrayIndex=parent[0].id;


						newXposition=(json[parentArrayIndex].wholetreenode.getBBox().width/2)+json[parentArrayIndex].wholetreenode.getBBox().x;


				
						var overlap=rightEndLastBox-(newXposition-(currentnode.wholetreenode.getBBox().width/2));
						if (overlap>0){
							//box is overlaping with last drawn shift, it to the right


		 					newXposition=newXposition+overlap;
						}
		
					//contains only on parent
				
					  currentnode.wholetreenode.attr({x: newXposition,y: currentlevel*75+200});
					  rightEndLastBox=newXposition+(currentnode.wholetreenode.getBBox().width/2);
						                                                                                           
			}else{  


			 	//TODO calculate the best postion with multiple children
			 	var totalWidth=0;
				 var spacing=4;
				for (var t=0;t<children.length;t++){

					totalWidth=totalWidth+children[t].wholetreenode.getBBox().width+spacing;		
				}
				
				var centerOfChildren=(totalWidth/2);


				newXposition=(parent[0].wholetreenode.getBBox().width/2)+parent[0].wholetreenode.getBBox().x;
				newXposition=newXposition-centerOfChildren;
		
			  overlap=rightEndLastBox-(newXposition-(currentnode.wholetreenode.getBBox().width/2));
					if (overlap>0){
						//box is overlaping with last drawn shift, it to the right


		 				newXposition=newXposition+overlap+1;
					}
		
			   currentnode.wholetreenode.attr({x: newXposition,y: currentlevel*75+200});
				rightEndLastBox=newXposition+(currentnode.whC2H5NSoletreenode.getBBox().width/2);
				//multiple layers
				}//end else 
			} 		//end for (var i=0;i<sortedlevels.length;i++){
	currentlevel++; 


	
	}

drawConectionsWholeTree();


}
function drawConectionsWholeTree(){


for (var p in json){
	var parentLocation=json[p].parent;
	
	if (parentLocation!==""){


					     
		lines.push(drawConectionWhole(p,parentLocation));	
}	

}
}
	function drawConectionWhole(var1,var2){
		
	var z;
	var obj1=json[var1].wholetreenode;
	var obj2=json[var2].wholetreenode;     
	
	if (obj2!==undefined){
	 var dimObj1=obj1.getBBox();
	 var dimObj2=obj2.getBBox();
	 var x1=dimObj1.x+dimObj1.width/2;
	 var x2=dimObj2.x+dimObj2.width/2;
	 var y2=dimObj2.y+dimObj2.height;
	 var y1=dimObj1.y;
	 z = paper.path("M"+x1+" "+y1+"L"+x2+" "+y2 );
	 z.attr({"stroke-width":"0.5","stroke-linecap":"round"});
	}
	 return (z);
}

function FindLongestLevel(spacing,maxnumberoflevels){
	
	var longestlevel=-1;
	var maxLength=0;
	for(var l=0;l<maxnumberoflevels;l++){

		//get all nodes from json with level
		nodesOfLevel=selectjson(json,"level",l);
		////determ legth of all the images of above
		var totallength=0;
		for (var i=0;i<nodesOfLevel.length;i++){

				totallength=totallength+nodesOfLevel[i].wholetreenode.getBBox().width+spacing;		
		}
		
		if(totallength>maxLength){
			maxLenght=totallength;
			longestlevel=l;
		
		}
		
	} 
	return longestlevel;
}

function createWholeTreeNode(id){
	var treeNode=paper.text(100,100,roundWithDecimals(json[id].mass,2)+"\n"+inchi2ElemenalComposition(json[id].inchi)+"\n"+json[id].id).attr({"font-size":"8"});
	treeNode.node.id=json[id].id;
	treeNode.click(function() {
		drawNeutralLossReations(treeNode.node.id);
		
		var toDrawnSpectrum;		
		if(json[treeNode.node.id].children===0){
			toDrawnSpectrum=json[treeNode.node.id].parent;
			
			}else{
					toDrawnSpectrum=treeNode.node.id;
				}
		UpdateSpectra(toDrawnSpectrum);
		}
		);
	 
	return(treeNode);	
	} 
	
function drawNeutralLossReations(id){ 


	                             
	//remove old neutral lose drawing
	 for (var nl=0;nl<neutralLoss.length;nl++){
		   neutralLoss[nl].remove();
	}
	neutralLoss=[];
	// find parentids
	parents=findParents(id);
	//add the question id to parents
	parents.push(id);


	
	//set x y and spacing
	var x=600;
	var y=250;
   	var spacing=80;
	//draw information


    for (var p=0;p<(parents.length-1);p++){
		   parent = json[parents[p]];
		   var child  = json[parents[p+1]];
		   var reactionText= inchi2ElemenalComposition(parent.inchi) + "-->"+ inchi2ElemenalComposition(child.inchi)+" + "   +inchi2ElemenalComposition(child.inchiloss) ;
	       var reaction=paper.text(x,y+(parent.level*spacing),reactionText);   
		   neutralLoss.push(reaction); 
		
	}	
}
	
	
   
function findParents(id){
var parent=id;
var parents=[];
 while (parent!==""){ 


		parent = selectjson(json, "id", parent)[0].parent;
		if(parent!==""){
			parents.push(parent);
		}



	}   
	return(parents.reverse());
}

function drawTree(partofjson, oldnode) {
	var x = 100;
	var w = 80;

	var padding = 20;   
	var oldNodeid=oldnode;
    partofjson=partofjson.sort(sortByMass);
	for ( var i = 0; i < partofjson.length; i++) {
		var y=(partofjson[i].level*(w+30))+200;
		(function() {
			if (nodesTracker[partofjson[i].id] === undefined) {
				var rect = paper.rect(x + ((i -1)* (w + padding)), y, w + 10,
						w + 10, 5).attr( {
					opacity : 0,
					fill : "red"
				});
				rect.node.id = partofjson[i].id;
				if (partofjson[i].children !== 0) {
					rect.attr( {
						fill : "green"
					});
				}

				rect.animate( {
					opacity : 1
				}, 200);
				var img = paper.image("http://cactus.nci.nih.gov/chemical/structure/InChI=InChI="+partofjson[i].inchi+"/gif", x + ((i -1) * (w + padding)) + 2,
						y + 2, w, w);
				
				
				var blabla = paper.text(x + ((i -1) * (w + padding)) + w / 2, y + w
						/ 2 + (0.4 * w), "mass: "+ roundWithDecimals(partofjson[i].mass,2)+"\n"+inchi2ElemenalComposition(partofjson[i].inchi)); 
				      
				var connection=conectnodes(rect,oldNodeid);
				
				nodesTracker[partofjson[i].id] = Array(rect, img, blabla,connection);
				rect.mouseover(function () {
			   
				
				rect.node.style.cursor = "move";
			   }); 
				
				rect.click(function() {
					
					whenClickedOnRect(rect.node.id);
					});
			}
		})();

	}

}
//
function whenClickedOnRect(recordid){

	var record = selectjson(json, "id", recordid);
	// should only get one
		if (record.length != 1) {
			alert("Waring record with id" + rect.node.id +
			  	 " not found");
		} else {
			record = record[0];
		}

		if(nodesTracker[recordid]===undefined){
			alert("//draw parents and node");
			//TODO implenent function taht draws parents and node
			//collect all id of all parents
			var parent=record.parent;
			var parents=[];
			while (parent!==""){
				parent = selectjson(json, "id", parent)[0].parent;
				parents.push(parent);


		
			}

		parents.reverse();



		for (var i =1;i<parents.length;i++){
			var subset_null = selectjson(json, "parent",parents[i]);
			record = selectjson(json, "id", parents[i]);


			
			showChildrenHideBrothers(record);

				
				
		}
		//UpdateSpectra(record.id);
		//
		}
		else if (record.children === 0 && nodesTracker[recordid]!==undefined) {
			alert("No children, nothing to drill down");
		}
		
		 else {
			
			// load the all information of this peak
			var childrenDrawn = checkChildrenDrawn(recordid);
			if (childrenDrawn.length > 0) {
				//Delete the children of the node and show brohers
				deleteChildrenAndShowBrothers(record,childrenDrawn);
				
			} else {
				
				//expand a node and show children and hide brothers
				showChildrenHideBrothers(record);

			}
			
		}

	
	
}

function deleteChildrenAndShowBrothers(record,childrenDrawn){
	//Delete the children of the node and show brohers
	deleteChildren(childrenDrawn);
  
	var subset_null = selectjson(json, "parent",
		record.parent);

		UpdateSpectra(record.parent);
				
		var parentNode;
		if(record.parent!==""){
			parentNode=nodesTracker[record.parent][0].node.id;
		}
				
		drawTree(subset_null , nodesTracker[record.id][0].getBBox().y ,parentNode);

}

function showChildrenHideBrothers(record){
				var brothers = selectjson(json, "parent",
						record.parent);
				if (brothers.length != 1) {
									
					var brotherids = getArrayFrom2DArrayAsKey(
							brothers, "id");
					for ( var a = 0; a < brotherids.length; a++) {
						
						if (brotherids[a] != record.id) {
							deletenode(brotherids[a]);
							
						}// end if
							

					}// end for for amount of brothers
				}// end else if (brothers.length > 1){

				var subset_null = selectjson(json, "parent",
						record.id);

				drawTree(subset_null,record.id);
				UpdateSpectra(record.id);
}
function UpdateSpectra(parentid) {
	
	var subset = selectjson(json, "parent", parentid); 




	
	var scalledsub = spectrascale(subset);
	drawSpectral(scalledsub);

}

function deleteChildren(ids) {
	for ( var i = 0; i < ids.length; i++) {
		deleteChildren(checkChildrenDrawn(ids[i]));
		deletenode(ids[i]);
	}

}
function conectnodes(obj1,oldNodeid){ 
	var z;
	var obj2;     
	if( oldNodeid!==undefined){
	   if( nodesTracker[oldNodeid]!==undefined){  
	   	obj2=nodesTracker[oldNodeid][0]; 
		}
	}
	if (obj2!==undefined){
	 var dimObj1=obj1.getBBox();
	 var dimObj2=obj2.getBBox();
	 var x1=dimObj1.x+dimObj1.width/2;
	 var x2=dimObj2.x+dimObj2.width/2;
	 var y2=dimObj2.y+dimObj2.height;
	 var y1=dimObj1.y;
	 z = paper.path("M"+x1+" "+y1+"L"+x2+" "+y2 );
	}
	 return (z);
}
function deletenode(id) {

	var currentpaintings = nodesTracker[id];


    if (currentpaintings!==undefined){
	for ( var i = 0; i < currentpaintings.length; i++) {
		if(currentpaintings[i]!==undefined){
		currentpaintings[i].animate( {
			opacity : 0
		}, 250, function() {

			this.remove();

		});
		}
		nodesTracker[id] = undefined;
		// currentpaintings[i].remove();
	}// end for remove objects
 }
}

function checkChildrenDrawn(id) {
	var children = selectjson(json, "parent", id);
	drawnChildrenIds = [];
	if (children.length > 0) {

		childrenids = getArrayFrom2DArrayAsKey(children, "id");
		for ( var i = 0; i < childrenids.length; i++) {
			var drawnImage = nodesTracker[childrenids[i]];
			if (drawnImage !== undefined) {
				drawnChildrenIds.push(childrenids[i]);
			}
		}
	}

	return (drawnChildrenIds);

}
function drawSpectral(poj) {
	partofjson=poj.child;
	//delete old peak

	//nodesTracker.Spectral=[];
	
		for( var k = 0; k < spectralTracker.length; k++) {
			for( var e = 0; e < spectralTracker[k].length; e++) {
				spectralTracker[k][e].remove();
		}
		}
	
	//draw baseline
	var svgBaseLIne = "M0 " + SPECTRAL_HEIGTH+ "L" + SPECTRAL_WIDTH + " " + SPECTRAL_HEIGTH + "z";
	paper.path(svgBaseLIne);

	//draw peaks
	var peakNonParent;
	var text;
	
	for ( var i = 0; i < poj.child.length; i++) {
	(function() {
	
		var svgPeaks="M" + partofjson[i].X + " " + SPECTRAL_HEIGTH + "L"+partofjson[i].X + " " + partofjson[i].Y+ "z";
		//draw labels
		peakNonParent=paper.path(svgPeaks);
		peakNonParent.node.id=partofjson[i].id;
		peakNonParent.click(function() {
					
					whenClickedOnRect(peakNonParent.node.id);
					});
					   
					peakNonParent.mouseover(function () {
                       
						peakNonParent.node.style.cursor = "move";
					   });
		text=paper.text(partofjson[i].X,partofjson[i].Y-8,roundWithDecimals(partofjson[i].mass,2)+"\n"+inchi2ElemenalComposition(partofjson[i].inchi)).attr({"font-size":"9"});
		spectralTracker[i]=Array(peakNonParent,text);
})();	
}
	//draw old line
	if (poj.parent!==undefined){
		var svgPeaks="M" + poj.parent.X + " " + SPECTRAL_HEIGTH + "L"+poj.parent.X + " " + poj.parent.Y+ "z";
		var peakParent=paper.path(svgPeaks).attr({stroke: "gray"});
		var textParent=paper.text(poj.parent.X,poj.parent.Y-8,roundWithDecimals(poj.parent.mass,2));
		spectralTracker[poj.child.length]=Array(peakParent,textParent);
		
	}



}
function spectrascale(partofjson) {
	var MININIMALPEAKHEIGHT = 5;
	var MAXIMALPEAKHEIGHT = 107;
	var MINIMALXSPACING = 13;

	//get parent peak
    
	var parentpeak = json[partofjson[0].parent];
	if (parentpeak!==undefined){
		partofjson.push(parentpeak);
		}
	if (partofjson.length>1){
		subset = partofjson.sort(sortByMass);
	//get all masses including parent
	var masses = getArrayFrom2DArrayAsKey(subset, "mass");
	masses.push(parentpeak.mass);
	//get all intensities including parent
	var intensities = getArrayFrom2DArrayAsKey(subset, "intensity");
	intensities.push(parentpeak.intensity);
	var maxintensity = (findmax(intensities));
	var maxmass = (findmax(masses));
	var minmass = (findmin(masses));
	for ( var i = 0; i < partofjson.length; i++) {
		partofjson[i].X = parseInt((((partofjson[i].mass - minmass) / (maxmass - minmass)) * (SPECTRAL_WIDTH - 2 * MINIMALXSPACING))
				+ MINIMALXSPACING,10);
		partofjson[i].Y = parseInt(SPECTRAL_HEIGTH
				- ((partofjson[i].intensity / maxintensity) * MAXIMALPEAKHEIGHT),10);
		if (partofjson[i].Y > MAXIMALPEAKHEIGHT - MININIMALPEAKHEIGHT) {
			partofjson[i].Y = MAXIMALPEAKHEIGHT - MININIMALPEAKHEIGHT;
		}

	}
	}
	else{

		partofjson[0].Y = (SPECTRAL_HEIGTH-MAXIMALPEAKHEIGHT);
		partofjson[0].X = parseInt(SPECTRAL_WIDTH/2,10);
	
		
	}
	var parent;
	var child
	if (parentpeak!==undefined){
		parent=partofjson[(partofjson.length-1)];
		partofjson.pop();
		child=partofjson;
	}else{
		parent=undefined;
		child=partofjson;

	} 
	
	var allpeaks={"child":child,"parent":parent}; 
	
	return (allpeaks);

}
function selectjson(partofjson, field, value) {
	var results = []; 
	
	for ( var key in partofjson) {  
		if (partofjson[key][field] == value) {
			results.push(partofjson[key]);
		}
	}
	return results;
}
function findmax(numericArray) {
	var max = Number.MIN_VALUE;
	for ( var i = 0; i < numericArray.length; i++) {
		var number = Number(numericArray[i]);
		if (max < number) {
			max = number;
		}
	}
	return max;

}

function findmin(numericArray) {
	var min = Number.MAX_VALUE;
	for ( var i = 0; i < numericArray.length; i++) {
		var number = Number(numericArray[i]);
		if (min > number) {
			min = number;
		}
	}
	return min;

}
function getArrayFrom2DArrayAsPosition(arrayOfArrays, position) {
	var subArray = [];
	for ( var i = 0; i < arrayOfArrays.length; i++) {
		if (arrayOfArrays[i].length < position) {
			subArray.push(arrayOfArrays[i][position]);
		}
	}
	return (subArray);

}
function getArrayFrom2DArrayAsKey(arrayOfArrays, key) {
	var subArray = [];
	for ( var item in arrayOfArrays) {
		if (arrayOfArrays[item][key] !== null) {
			subArray.push(arrayOfArrays[item][key]);
		}
	}
	return (subArray);

}
function getArrayPostionBasedOnId(id){
	var arrayLocation=undefined;
	for ( var i = 0; i <json.length; i++) {
		if(parseInt(json[i].id,10)==parseInt(id,10)){ 
		     arrayLocation=i;
		 }
	}
	return arrayLocation;
}
