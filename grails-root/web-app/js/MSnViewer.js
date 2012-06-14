/*@license
 * Copyright (c) 2010 Maarten Kooyman, NBIC/TUdelft

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */


/**
 * Returns dictionary sorted by mass.has to be called with subset.sort(sortByMass)
 *
 */

function sortByMass(a, b) {
	var x = a.mass;
	var y = b.mass;
	return x - y;
}


/**
 * Returns dictionary sorted by order.has to be called with subset.sort(sortByOrder)
 *
 */
function sortByOrder(a, b) {
	var x = parseInt(a.order, 10);
	var y = parseInt(b.order, 10);
	return x - y;
}


/**
 * Returns a rounded value of a number
 *  @param {number} number number that has to be rounded.
 *   @param {integer} decimals number of decimals.
 *   @return {number} rounded value.
 */
function roundWithDecimals(number, decimals) {
	var decimalMultilpy = Math.pow(10, decimals);
	var rounded = Math.round(number * decimalMultilpy) / (decimalMultilpy);
	// add a 1 zero no dot is selected
	if ((rounded + '').indexOf('.') == -1) {
		rounded = rounded + '.0';
	}
	return rounded;
}


/**
 * Returns a number in scientific notation
 *  @param {number} number number that has to be rounded.
 *   @param {integer} digits number of digits to to be visible.
 *   @return {string} scientific notation (example 3E6).
 */
function NumberAsEvalue(number, digits) {
	var scientific = '0';
	if (number != 0) {
		var exp = Math.floor(Math.log(Math.abs(number)) / Math.LN10);
		if (number == 0) {
			exponent = 0;
		}

		var remainer = number / Math.pow(10, exp);
		scientific = roundWithDecimals(remainer, digits) + 'e' + exp;
	}
	return (scientific);
}


/**
 * return SVG format of an arrow pointing down with a length 20 pixels
 *  @param {number} x x coordinate of start of arrow.
  *  @param {number} y y coordinate of start of arrow.
 *  @return {string} SVG format of arrow.
 */
function arrowAsString(x,y) {
	//("M5 0L5 20L0 15L5 20L10 15L5 20")
	return ('M' + (5 + x) + ' ' + (0 + y) + 'L' + (5 + x) + ' ' + (20 + y) + 'L' + (0 + x) + ' ' + (15 + y) + 'L' + (5 + x) + ' ' + (20 + y) + 'L' + (10 + x) + ' ' + (15 + y) + 'L' + (5 + x) + ' ' + (20 + y) + '');



}

/**
 * extracts elemental composition out of a InChi string., if not found this function returns a "?"(question mark)
 *  @param {string} inchi InChi string.
 *  @return {string} elemental composition.
 */

function inchi2ElemenalComposition(inchi) {
	var ElemenalComposition = '';
	var cleanElemetalComposition = '?';

	if (inchi.length > 0) {
		ElemenalComposition = inchi.match('\/([0-9A-z]+)\/');
		// check there are some results
		if (ElemenalComposition === null) {
			// probably not a valid last part of inchi
			ElemenalComposition = inchi.match('\/([0-9A-z]+)');
		} else if (ElemenalComposition.length >= 1) {
			cleanElemetalComposition = ElemenalComposition[1];
		}
	}
	return (cleanElemetalComposition);

}


/**
 * Removes certain character from string and replace it with another character
 *  @param {char} from character that has to be replaced.
 *  @param {char} to replacement character.
 *  @return {string}
 */
function replaceAll(string, from, to) {

	var matches = string.indexOf(from);
	while (matches !== -1) {

		string = string.replace(from, to);
		matches = string.indexOf(from);
	}
	return (string);
}


/**
 * function to split a string with multiple inchis to a an array on inchis
 *  @param {string} multipleinchi a string with concatenated inchi with a ',' in between.
 *  @return {array}  array with sepparate inchi's.
 */
function splitInchis(multipleinchi) {
	inchiArray = multipleinchi.split(',InChI=');

	for (var ichiIndex = 1; ichiIndex < inchiArray.length; ichiIndex++) {
		inchiArray[ichiIndex] = 'InChI=' + inchiArray[ichiIndex];
	}

	return (inchiArray);
}


/**
 * function to transform a InChI to a URL for the structure.
* The function replace the backslashes in the InChI to prevent conflict with the server(server sees the backslash as directory)
 *  @param {string} inchi a InChi.
 *  @return {string}  a URL of an image.
 */

function inchi2ImageURL(inchi) {
	// change a inch to the right url of the webservice
	var url = '';
	if (inchi !== undefined) {
		var server = 'http://localhost:8090/png/';
		url = server + inchi;
	}
	return (url);

}


/**
 * function to select certain "records" out of a json message.
 *  @param {dicionary in dictionary} partofjson
 *  @param {string} field the key element to select on.
 *  @param {string}  value the value the key element should have.
 *  @return {array of dictionaries}
 */
function selectjson(partofjson, field, value) {
	var results = [];

	for (var key in partofjson) {
		if (partofjson[key][field] == value) {
			results.push(partofjson[key]);
		}
	}
	return results;
}


/**
 * function to select certain "records" out of a json message in a certain interval.
 *  @param {dicionary in dictionary} partofjson
 *  @param {string} field the key element to select on.
 *  @param {numeric} val1 first value of the interval.
 *  @param {numeric} val2 second value of the interval.
 *  @return {array of dictionaries}  an array of dict where key has the value between val1 and val2.
 */
function selectjsonWithInterval(partofjson, field, val1, val2) {
	val1 = parseFloat(val1);
	val2 = parseFloat(val2);
	var min;
	var max;
	// bind min and max value from val1 and val2
	if ((val1 - val2 < 0)) {
		max = val2;
		min = val1;

	} else {
		max = val1;
		min = val2;
	}

	var results = [];

	for (var key in partofjson) {
		if (partofjson[key][field] > min && partofjson[key][field] < max) {
			results.push(partofjson[key]);
		}
	}
	return results;
}
// function to find maximum in numeric array
// numericArray: a array with numbers
// return: a numeric which is the highest number


/**
 *  function to find maximum in numeric array.
 *  @param {array} numericArray a array with numbers.
 *  @return {numeric}  a numeric which is the highest number.
 */
function findmax(numericArray) {
	var max = Number.MIN_VALUE;
	for (var i = 0; i < numericArray.length; i++) {
		var number = Number(numericArray[i]);
		if (max < number) {
			max = number;
		}
	}
	return max;

}


/**
 *  function to find minimum in numeric array.
 *  @param {array} numericArray a array with numbers.
 *  @return {numeric}  a numeric which is the smallest number.
 */
function findmin(numericArray) {
	var min = Number.MAX_VALUE;
	for (var i = 0; i < numericArray.length; i++) {
		var number = Number(numericArray[i]);
		if (min > number) {
			min = number;
		}
	}
	return min;

}
/**
 *  function to return all values of a certain key(on second layer) in a dictionary  of dictionary as an array
 *  @param {array of dictionaries or dictionary of dictionaries} arrayOfArrays a array of dictionaries  or an dictionary of dictionaries.
 *  @param {string} key  key of element to return.
 *  @return {array}
 */

function getArrayFrom2DArrayAsKey(arrayOfArrays, key) {
	var valueArray = [];
	for (var item in arrayOfArrays) {
		if (arrayOfArrays[item][key] !== null) {
			valueArray.push(arrayOfArrays[item][key]);
		}
	}
	return (valueArray);

}


/**
 * Function to add the order in which the nodes of the tree are drawn
 * The order is determ by the mass of the peaks (small is left, big is right)
 * the siblings of the peaks are sorted first by there parent and then by mass
 *  @param {tree format} json the tree format  number of children must be added with addNumberOfChildrenToTree(json).
 *   @param {array of ids} parentids decimals number of decimals.
 *   @return {tree format} tree format with with order of nodes.
 */
function addOrderOfWholeTree(json, parentids) {
	var newparents = [];
	var orderOfElement = 0;
	for (var t = 0; t < parentids.length; t++) {
		var subset = selectjson(json, 'parent', parentids[t]);
		subset = subset.sort(sortByMass);
		for (var i = 0; i < subset.length; i++) {
			json[subset[i].id].order = orderOfElement;
			orderOfElement++;
			if (json[subset[i].id].children > 0) {
				newparents.push(subset[i].id);
			}// end if there are children
		}// end sorted loop
	}// end parentsid loop
	if (newparents.length !== 0) {
		json = addOrderOfWholeTree(json, newparents);
	}
	return (json);
}


/**
 * Function to add the number of children each node has.
 * addOrderOfWholeTree depends on this fucntion
 *  @param {tree format} json the tree format.
 *   @return {tree format} tree format with amount of children per node.
 */
function addNumberOfChildrenToTree(json) {
	// c.show();
	for (var key in json) {
		json[key].children = selectjson(json, 'parent', json[key].id).length;
	}
	return (json);

}


/**
 * Returns the tree format with depth(vertical level) of eah node atached to each node
 *  @param {tree format} json the tree format.
 *   @param {integer} decimals number of decimals.
 *   @return {tree format} tree format with in each node in the "level" parameter with depth of node in tree (top of the tree is zero).
 */

function AddLevelOfNode(json) {
	// find first level
	var levelcounter = 0;
	var parentArray = [];
	parentArray[levelcounter] = getArrayFrom2DArrayAsKey(selectjson(json,
			'parent', ''), 'id');
	// last itteration must contain a item
	var ids = [];
	while (parentArray[levelcounter] !== undefined) {

		ids = [];
		for (var i = 0; i < parentArray[levelcounter].length; i++) {
			var foundbyparents = selectjson(json, 'parent',
					parentArray[levelcounter][i]);

			for (var p = 0; p < foundbyparents.length; p++) {
				ids.push(foundbyparents[p].id);
			}
		}
		// update level to new setting
		levelcounter++;
		if (ids.length >= 1) {
			parentArray[levelcounter] = ids;
		}
	}
	// loop throught all levels
	for (var l = 0; l < parentArray.length; l++) {
		// loop through all ids of level
		for (var k = 0; k < parentArray[l].length; k++) {
			json[parentArray[l][k]].level = l;
		}
	}
	return (json);
}
//function to draw lines between to nodes in the tree and returns it
//var1: a id field (or key) of the json field
//var2: a id field (or key) of the json field
// retuns the raphael line object

/**
 * function to draw lines between to nodes in the tree and returns
 *  @param {node id} var1  a id field (or key) of the json field.
 * @param {node id} var2  a id field (or key) of the json field.
 *   @return {raphael line object}
 */

MSnViewer.prototype.drawConectionWhole = function(var1, var2) {

	var z;
	var obj1 = this.json[var1].wholetreenode;
	var obj2 = this.json[var2].wholetreenode;

	if (obj2 !== undefined) {
		var dimObj1 = obj1.getBBox();
		var dimObj2 = obj2.getBBox();
		var x1 = dimObj1.x + dimObj1.width / 2;
		var x2 = dimObj2.x + dimObj2.width / 2;
		var y2 = dimObj2.y + dimObj2.height;
		var y1 = dimObj1.y;
		z = this.paper.path('M' + x1 + ' ' + y1 + 'L' + x2 + ' ' + y2)
				.insertBefore(this.spectralBackground);
		z.attr({
			'stroke-width' : '0.5',
			'stroke-linecap' : 'round'
		});
	}
	return (z);
};


/**
 * function to draw all lines between parent and children nodes  and save them in this.lines
 */
MSnViewer.prototype.drawConectionsWholeTree = function() {
	for (var p in this.json) {
		var parentLocation = this.json[p].parent;

		if (parentLocation !== '') {

			this.lines.push(this.drawConectionWhole(p, parentLocation));
		}

	}
};


/**
 * function to find the widest level when drawing the tree
 *  @param {tree format with already draw tree} json
 * @param {numeric} spacing spacing in x direction between drawn nodes.
 * @param {integer} maxnumberoflevels  depth of the tree found with findmax(getArrayFrom2DArrayAsKey(this.json, "level")).
 *   @return {integer} number of level that is the widest.
 */
function FindLongestLevel(json, spacing, maxnumberoflevels) {

	var longestlevel = -1;
	var maxLength = 0;
	for (var l = 0; l < maxnumberoflevels; l++) {

		// get all nodes from json with level
		nodesOfLevel = selectjson(json, 'level', l);
		// //determ legth of all the images of above
		var totallength = 0;
		for (var i = 0; i < nodesOfLevel.length; i++) {

			totallength = totallength
					+ nodesOfLevel[i].wholetreenode.getBBox().width + spacing;
		}

		if (totallength > maxLength) {
			maxLenght = totallength;
			longestlevel = l;

		}

	}
	return longestlevel;
}


/**
 * function to draw the nodes of the tree UNPOSITIONED
 * also handles events of the nodes (clicked on, mouse over etc).
 * @param {numeric} id identifier of node in tree format.
 * @return {treeNode} unpositioned treenode (drawn node of the tree).
 */
MSnViewer.prototype.createWholeTreeNode = function(id) {

	var treeNode = this.paper
			.text(
					100,
					100,
					roundWithDecimals(this.json[id].mass, 2)
							+ '\n'
							+ inchi2ElemenalComposition(splitInchis(this.json[id].inchi)[0])
							+ '\n' + this.json[id].id).attr({
				'font-size' : '8'
			}).insertBefore(this.spectralBackground);
	treeNode.node.id = this.json[id].id;
	var self = this;
	treeNode.click(function() {
		self.drawNeutralLossReations(treeNode.node.id);
		// draw background of node
		var dimNode = this.getBBox();
		if (self.backgroundNode !== '') {
			self.backgroundNode.remove();
			// remove the background out of the drag and drop element
			self.lines.pop();
		}
		self.backgroundNode = self.paper.rect(dimNode.x, dimNode.y,
				dimNode.width, dimNode.height).attr({
			fill: 'red'
		}).insertAfter(self.lines[(lines.length - 1)]);
		// add to lines to make it dragable
		self.lines.push(self.backgroundNode);
		var toDrawnSpectrum;
		if (self.json[treeNode.node.id].children === 0) {
			toDrawnSpectrum = self.json[treeNode.node.id].parent;

		} else {
			toDrawnSpectrum = treeNode.node.id;
		}
		self.UpdateSpectra(toDrawnSpectrum);
	});
	json = this.json;
	spectralTracker = this.spectralTracker;
	mass2Peaks = this.mass2Peaks;
	treeNode.mouseover(function() {
		if (spectralTracker[mass2Peaks[json[treeNode.node.id].mass]]!== undefined ){
			spectralTracker[mass2Peaks[json[treeNode.node.id].mass]][0].attr({
				stroke: 'red'
			});
			spectralTracker[mass2Peaks[json[treeNode.node.id].mass]][1].attr({
				fill: 'red'
			});
		}

	});
	treeNode.mouseout(function() {
		if (spectralTracker[mass2Peaks[json[treeNode.node.id].mass]]!== undefined ){
			spectralTracker[mass2Peaks[json[treeNode.node.id].mass]][0].attr({
				stroke: 'black'
			});
			spectralTracker[mass2Peaks[json[treeNode.node.id].mass]][1].attr({
				fill: 'black'
			});
		}

	});

	return (treeNode);
};

MSnViewer.prototype.positionHigherThanLongestLevelNodes = function(
currentlevel, ySpacing, startOfTree    ) {

	var overlap;
	var children;
	var totalwidth;
	var rightEndLastBox = 0;

	var currentLevels = selectjson(this.json, 'level', currentlevel);
	sortedlevel = currentLevels.sort(sortByOrder);

	for (var i = 0; i < sortedlevel.length; i++) {

		currentnode = this.json[sortedlevel[i].id];
		children = selectjson(this.json, 'parent', currentnode.id);
		totalwidth = 0;

		if (children.length === 0) {
			// if there are no new children  just add it to the right side of last moved node(+ 5 pixels spacing +and own width)
			newXposition = currentnode.wholetreenode.getBBox().width / 2
					+ rightEndLastBox + 5;
		} else {
			//calculate the total width of the children to position it in the midle of the children
			for (var c = 0; c < children.length; c++) {
				totalwidth = totalwidth
						+ (children[c].wholetreenode.getBBox().width / 2)
						+ children[c].wholetreenode.getBBox().x;
			}
			newXposition = Math.round(totalwidth / children.length);
			// check for overlap of the old node and the new node
			overlap = rightEndLastBox
					- (newXposition - (currentnode.wholetreenode.getBBox().width / 2));
			if (overlap > 0) {
				// box is overlaping with last drawn shift, it to the right
				// "+newXposition +" "+rightEndLastBox);
				newXposition = newXposition + overlap;

			}
		}

		//move the node to new calculated position
		currentnode.wholetreenode.attr({
			x: newXposition,
			y: currentlevel * ySpacing + startOfTree
		});
		// set the right end bc to  new x value
		rightEndLastBox = currentnode.wholetreenode.getBBox().width
				+ currentnode.wholetreenode.getBBox().x;

	}
};

MSnViewer.prototype.positionLowerThanLongestLevelNodes = function(currentlevel,
ySpacing, startOfTree    ) {
	rightEndLastBox = 0;

	currentlevels = selectjson(this.json, 'level', currentlevel);
	sortedlevels = currentlevels.sort(sortByOrder);
	for (var i = 0; i < sortedlevels.length; i++) {

		currentnode = selectjson(this.json, 'id', sortedlevels[i].id)[0];
		// currentnode"+currentnode.id+"parent"+currentnode.parent);

		var parent = selectjson(this.json, 'id', currentnode.parent);
		children = selectjson(this.json, 'parent', currentnode.parent);
		if (children.length === 1) {
			var parentArrayIndex = parent[0].id;
			// width"+json[parentArrayIndex].wholetreenode.getBBox().width/2+"X");
			newXposition = (this.json[parentArrayIndex].wholetreenode.getBBox().width / 2)
					+ this.json[parentArrayIndex].wholetreenode.getBBox().x;

			overlap = rightEndLastBox
					- (newXposition - (currentnode.wholetreenode.getBBox().width / 2));
			if (overlap > 0) {
				// box is overlaping with last drawn shift, it to the
				// right
				// "+newXposition +" "+rightEndLastBox);
				newXposition = newXposition + overlap;
			}

			// contains only on parent

			currentnode.wholetreenode.attr({
				x: newXposition,
				y: currentlevel * ySpacing + topOfTree
			});
			rightEndLastBox = newXposition
					+ (currentnode.wholetreenode.getBBox().width / 2);

		} else {
			// TODO calculate the best postion with multiple children
			var totalWidth = 0;
			spacing = 4;
			for (var t = 0; t < children.length; t++) {

				totalWidth = totalWidth
						+ children[t].wholetreenode.getBBox().width + spacing;
			}

			var centerOfChildren = (totalWidth / 2);
			newXposition = (parent[0].wholetreenode.getBBox().width / 2)
					+ parent[0].wholetreenode.getBBox().x;
			newXposition = newXposition - centerOfChildren;

			overlap = rightEndLastBox
					- (newXposition - (currentnode.wholetreenode.getBBox().width / 2));
			if (overlap > 0) {
				// box is overlaping with last drawn shift, it to the
				// right
				// "+newXposition +" "+rightEndLastBox);
				newXposition = newXposition + overlap + 1;
			}

			currentnode.wholetreenode.attr({
				x: newXposition,
				y: currentlevel * 75 + startOfTree
			});
			rightEndLastBox = newXposition
					+ (currentnode.wholetreenode.getBBox().width / 2);
			// multiple layers
		}// end else
	} // end for (var i=0;i<sortedlevels.length;i++){
};
//function to draw the whole tree
//
//
MSnViewer.prototype.drawWholeTree = function() {
	topOfTree = this.SPECTRAL_HEIGTH + 30;
	var spacing = 10;
	var ySpacer = 75;
	//draw fore each node the boxes(positioning is done lateron)
	for (var item in this.json) {
		this.json[item].wholetreenode = this.createWholeTreeNode(item);
		// add node to glabal variable to make it drag and dropable
		this.lines.push(this.json[item].wholetreenode);
	}
	//find maximum number of levels(how deep is the tree)
	var maxnumberoflevels = findmax(getArrayFrom2DArrayAsKey(this.json, 'level'));
	// find which level has the largests  total width
	var longestlevel = FindLongestLevel(this.json, spacing, maxnumberoflevels);
	// select this tree information with the  longest level
	var longestLevelJSON = selectjson(this.json, 'level', longestlevel);
	// sort these items by the earlier set order
	var sortedlevel = longestLevelJSON.sort(sortByOrder);
	//new x position contains the x location of the node to move to
	var newXposition = 100;
	var currentnode;

	for (var i = 0; i < sortedlevel.length; i++) {

		currentnode = this.json[sortedlevel[i].id];
		currentnode.wholetreenode.attr({
			x: newXposition,
			y: (longestlevel * ySpacer) + topOfTree
		});
		newXposition = newXposition + currentnode.wholetreenode.getBBox().width
				+ spacing;

	}
	//
	//this part positions the nodes in the levels above the longest level
	//
	//
	var currentlevel = longestlevel - 1;

	while (currentlevel >= 0) {
		this.positionHigherThanLongestLevelNodes(currentlevel, ySpacer,
				topOfTree);
		currentlevel--;
	}
	//
	// draw levels under (in y direction)
	//
	currentlevel = longestlevel + 1;
	while (currentlevel <= maxnumberoflevels) {
		this.positionLowerThanLongestLevelNodes(currentlevel, ySpacer,
				topOfTree);
		currentlevel++;
	}

	this.drawConectionsWholeTree();

};


/**
 * function to zoom on spectrum which can be called externaly (from html)
 *  call with:
 * <input type="text" maxlength="4" size="4" id="min" name="min">
 * <input type="text" maxlength="4" size="4" id="max" name="max">
 * <input type="button" onclick="drawing_1.zoom()" value="Zoom">
 * (note the id fields)
 */
MSnViewer.prototype.zoom = function() {
	var min = document.getElementById('min').value;
	var max = document.getElementById('max').value;
	this.zoomSpectrum(min, max, false);

};


/**
 * function to draw a structure based on inchi key and add it to self.neutralLoss
 * @param {string} inchi Inchi.
 * @param {numeric} x  x postion of structure.
 * @param {numeric} y y postion of structure.
 * @param {numeric} width width of structure.
 * @param {numeric} height height of structure.
  * @param {string} structureLocationInNeutralLoss identiefier  where the sturcture is saved in self.neutralLoss
 *@returns {raphael object} structure.
 */
MSnViewer.prototype.drawStructure = function(inchi, x, y, width, height,
structureLocationInNeutralLoss    ) {
	var imageExists = false;
	var structureRapheal;
	var self = this;
	// check with internal function that function is valid inchi
	if (checkInchi(inchi)) {
		// if(true){
		// check the image exists and has a dimention
		var structureImage = new Image();

		structureImage.onerror = function() {
			//structureRapheal = self.paper.rect(x, y, width, height).attr({
			//	fill: 'none',
			//	stroke: '#000',
			//	'stroke-dasharray' : '- '
			//});
			structureRapheal = self.paper.image("http://localhost:8090/png/InChI=1S/C", x, y, width, height);
			self.neutralLoss[structureLocationInNeutralLoss] = structureRapheal;
			self.paper.safari();

		};// end function

		structureImage.onload = function() {
			if (structureImage.complete) {

			}

			if (structureImage.height != 1) {
				imageExists = true;
				//draw the structure to original dimension if it smaller than maximum space
				var currentWidth;
				var currentHeight;
				if (structureImage.height < height
						&& structureImage.width < width) {
					currentWidth = structureImage.width;
					currentHeight = structureImage.height;
					//shrink the image but keep the ratio
				} else {
					//find out what side bigger
					var ratioWidth = structureImage.width / width;
					var ratioHeight = structureImage.height / height;
					if (ratioWidth > ratioHeight) {
						currentWidth = structureImage.width / ratioWidth;
						currentHeight = structureImage.height / ratioWidth;
					} else {
						currentWidth = structureImage.width / ratioHeight;
						currentHeight = structureImage.height / ratioHeight;

					}
				}

				// draw structure
				structureRapheal = self.paper.image(inchi2ImageURL(inchi), x,
						y, currentWidth, currentHeight);
				self.paper.safari();
				self.neutralLoss[structureLocationInNeutralLoss] = structureRapheal;

			} else {

				//structureRapheal = self.paper.rect(x, y, width, height).attr({
				//	fill: 'none',
				//	stroke: '#777',
				//	'stroke-dasharray' : '- '
				//});
				structureRapheal = self.paper.image("http://localhost:8090/png/InChI=1S/C", x, y, width, height);
				self.paper.safari();

				self.neutralLoss[structureLocationInNeutralLoss] = structureRapheal;
			}// end if
		};// end function

		structureImage.src = inchi2ImageURL(inchi);

	} else {
		//Draw a rectangle when inchi is not valid
		//structureRapheal = this.paper.rect(x, y, width, height, 5).attr({
		//	fill: 'none',
		//	stroke: '#888',
		//	'stroke-dasharray' : '- '
		//});
		structureRapheal = self.paper.image("http://localhost:8090/png/InChI=1S/C", x, y, width, height);
		this.neutralLoss[structureLocationInNeutralLoss] = structureRapheal;

	}

	return (structureRapheal);
};


/**
 * remove all drawn structures and other information regarding structures and cleans this.neutralLoss

 */
MSnViewer.prototype.removeNeutralLoss = function() {
	// remove old neutral lose drawing

	for (var nl in this.neutralLoss) {
		this.neutralLoss[nl].remove();
	}
	this.neutralLoss = [];
};



/**
 *Find all parents of a node
* @param {tree format} json last node in tree to be drawn.
* @param {node id} id last node in tree to be drawn.
* @return {array of node ids}
 */
function findParents(json, id) {
	var parent = id;
	var parents = [];
	while (parent !== '') {
		parent = selectjson(json, 'id', parent)[0].parent;
		if (parent !== '') {
			parents.push(parent);
		}

	}
	return (parents.reverse());
}



/**
 * function tot get max and min x and y position of a raphael object
 * @param {raphael object} raphealObject drawned rapheal object.
 * @return {dictonary} dictonary with north south east west borders of object and x and y coordinates.
 */


function getPosition(raphealObject) {
	var labelDim = raphealObject.getBBox();

	var labelBoxXmin = -(labelDim.width / 2);
	var labelBoxXmax = labelDim.x + (labelDim.width / 2);
	var labelBoxYmin = labelDim.y - (labelDim.height / 2);
	var labelBoxYmax = labelDim.y + (labelDim.height / 2);

	var position = {
		'north' : labelBoxYmax,
		'south' : labelBoxYmin,
		'west' : labelBoxXmin,
		'east' : labelBoxXmax,
		'x' : labelDim.x,
		'y' : labelDim.y
	};
	return (position);
}


/**
 * Draw all structures, elemental compositions,arrow and links to alternative structures untill last node that is specified by id
 * @param {node id} last node in tree to be drawn.
 */
MSnViewer.prototype.drawNeutralLossReations = function(id) {

	this.removeNeutralLoss();
	// find parentids
	parents = findParents(this.json, id);
	// add the question id to parents
	parents.push(id);
	// set x y and spacing
	var x = 800;
	var y = 250;
	var spacing = 90;
	// draw information
	var structureLocation = 0;
	for (var p = 0; p < (parents.length); p++) {
		structureLocation = structureLocation + 1;
		var parent = this.json[parents[p]];
		//var child = this.json[parents[p + 1]];

		var imageWidth = 80;
		var imageHeight = spacing - 10;

		var inchiLoss;
		var inchPrecursor;
		var inchPrecursorText;
		var shiftAtraNextLineOfStructures = 0;
		if (p !== 0) {

			//draw first inchi loss
			var inchilossArray = splitInchis(parent.inchiloss);
			inchiLoss = this.drawStructure(inchilossArray[0], this.PAPER_WIDTH
					- imageWidth, y + (parent.level * spacing), imageWidth,
					imageHeight, structureLocation);
			this.neutralLoss[structureLocation] = inchiLoss;

			//draw the + sign in reaction
			inchPrecursorText = this.paper.text(this.PAPER_WIDTH
					- (1 * imageWidth), y + ((parent.level + 1) * spacing) - 5,
					'+');
			this.neutralLoss[structureLocation + 'plus'] = inchPrecursorText;


			if (inchilossArray.length === 1) {
				inchPrecursorText = this.paper.text(this.PAPER_WIDTH
						- (0.5 * imageWidth), y
						+ ((parent.level + 1) * spacing) - 5,
						inchi2ElemenalComposition(parent.inchiloss));
				this.neutralLoss[structureLocation + 'text'] = inchPrecursorText;


			} else {
				//draw links when there are multiple neutralloss in the inchi loss field
				for (var inchiCounter = 0; inchiCounter < inchilossArray.length; inchiCounter++) {
					inchPrecursorText = this.paper
							.text(
									this.PAPER_WIDTH - (0.5 * imageWidth),
									y + ((parent.level + 1) * spacing) - 5
											+ (inchiCounter * 8),
									inchi2ElemenalComposition(inchilossArray[inchiCounter]));
					this.neutralLoss[structureLocation + 'text' + inchiCounter] = inchPrecursorText;
					inchPrecursorText.node.inchi = inchilossArray[inchiCounter];
					inchPrecursorText.node.structureLocation = structureLocation;

					var self = this;
					inchPrecursorText
							.click(function() {
								var positionOldElement = getPosition(self.neutralLoss[this.node.structureLocation]);
								//remove old drawing
								self.neutralLoss[this.node.structureLocation]
										.remove();
								//
								inchiLoss = self.drawStructure(this.node.inchi,
										positionOldElement.x,
										positionOldElement.y, imageWidth,
										imageHeight,
										this.node.structureLocation);
								self.neutralLoss[this.node.structureLocation] = inchiLoss;
								//this.nodesTracker.inchi

							});
					//add the postion for extra neutral losses to prevent overlap with next structure
					shiftAtraNextLineOfStructures = shiftAtraNextLineOfStructures + 8;
				}//end for inchicounter
			}//end else
		} //	if (p !== parents.length - 1) {

		structureLocation++;
		// draw precursor
		var inchiParentArray = splitInchis(parent.inchi);

		inchPrecursor = this.drawStructure(inchiParentArray[0],
				this.PAPER_WIDTH - (2 * imageWidth), y
						+ (parent.level * spacing), imageWidth, imageHeight,
				structureLocation);

		// neutralLoss.push(inchiLoss);
		// neutralLoss.push(inchPrecursor);
		//TODO draw links to other precursors

		inchPrecursorText = this.paper.text(this.PAPER_WIDTH
				- (1.5 * imageWidth), y + ((parent.level + 1) * spacing) - 5,
				inchi2ElemenalComposition(parent.inchi));
		this.neutralLoss[structureLocation + 'text'] = inchPrecursorText;

		//if there are isoforms draw links
		if (inchiParentArray.length > 1) {
			if (shiftAtraNextLineOfStructures < 8) {
				shiftAtraNextLineOfStructures = 8;
			}
			for (var isoformNumber = 0; isoformNumber < inchiParentArray.length; isoformNumber++) {

				inchPrecursorText = this.paper.text(this.PAPER_WIDTH
						- (2 * imageWidth) + isoformNumber * 10, y
						+ ((parent.level + 1) * spacing) - 5 + 8,
						isoformNumber + 1);
				inchPrecursorText.node.inchi = inchiParentArray[isoformNumber];
				inchPrecursorText.node.structureLocation = structureLocation;
				this.neutralLoss[structureLocation + 'isoform' + isoformNumber] = inchPrecursorText;
				var selfIsoform = this;
				inchPrecursorText
						.click(function() {
							var positionOldElement = getPosition(selfIsoform.neutralLoss[this.node.structureLocation]);
							//remove old drawing
							selfIsoform.neutralLoss[this.node.structureLocation]
									.remove();
							//
							inchiLoss = selfIsoform.drawStructure(
									this.node.inchi, positionOldElement.x,
									positionOldElement.y, imageWidth,
									imageHeight, this.node.structureLocation);
							selfIsoform.neutralLoss[this.node.structureLocation] = inchiLoss;
							//this.nodesTracker.inchi

						});

			}//end for loop isoform
		}//end if inchiParentArray.length
		//Draw a arrow between the structures (skip the last one beacause it points to nothing)
		if (p !== parents.length - 1) {

			this.neutralLoss[structureLocation + 'arrow'] = this.paper.path(arrowAsString(
				this.PAPER_WIDTH - (1.5 * imageWidth),
				(y + (parent.level + 1) * spacing + shiftAtraNextLineOfStructures)));

		shiftAtraNextLineOfStructures = shiftAtraNextLineOfStructures + 22;
		}
		y = shiftAtraNextLineOfStructures + y;

	}

};




/**
* make in the spectra the peaks visable of the children of id
* @param {node id} id children of this node should be drawn.
* */
MSnViewer.prototype.UpdateSpectra = function(id) {

	var subset = selectjson(json, 'parent', id);
	this.parentpeak = id;
	var scalledsub = this.spectrascale(subset, true);

	this.drawSpectral(scalledsub);

};


/**
*
* */
MSnViewer.prototype.removeBackgroundNode = function() {

	if (this.backgroundNode !== '') {
		this.backgroundNode.remove();
		this.backgroundNode = '';
		//remove from lines object wich makes the object dragable
		this.lines.pop();
	}
};

function createCombinationSpectra(json) {
	// sort all spectra on mass
	var subset = [];
	for (var item in json) {
		subset.push(json[item]);
	}

	subset = subset.sort(sortByMass);
	// check for same mass as peak checked before and sum mass
	var lastMass;
	var peak = {};
	var output = [];
	for (var i = 0; i < subset.length; i++) {
		if (subset[i].mass !== lastMass) {
			if (peak.mass !== undefined) {
				output.push(peak);
				peak = {};
			}
			peak.mass = subset[i].mass;
			// add intensity
			peak.intensity = subset[i].intensity;

			// add id
			peak.id = [subset[i].id];
			lastMass = subset[i].mass;

		} else {
			// mass is the same
			// add intensity
			peak.intensity = parseFloat(peak.intensity, 10)
					+ parseFloat(subset[i].intensity, 10);
			// add id
			peak.id.push(subset[i].id);
		}

	}// end for

	output.push(peak);

	return (output);
}

/**
* function to draw the cumulative spectrum
* */
MSnViewer.prototype.DrawCumulativeSpectra = function() {

	// var subset = selectjson(json, "parent", parentid);
	//this.removeNeutralLoss();
	//this.removeBackgroundNode();
	this.parentpeak = false;
	var scalledsub = this.spectrascale(createCombinationSpectra(this.json),
			false);
	this.drawSpectral(scalledsub);

};


/**
* function to  zoom out to complete range of peaks availble
* */
MSnViewer.prototype.DrawFullSpectraOnCurrentView = function() {
	//TODO change minimum to real minimum and maximum
	var min = -1000;
	var max = +1000;
	this.zoomSpectrum(min, max, true);

};
/**
* function to zoom in part of spectra and shows only the peaks in range
* @param {numeric} min  minumum mass as number.
* @param {numeric} max  minumum mass as number.
*  @param {boolean} parentpeak view the parent peak or not(true== view).
* */

MSnViewer.prototype.zoomSpectrum = function(min, max, parentpeak) {
	var subset;
	if (this.parentpeak) {

		subset = selectjson(this.json, 'parent', this.parentpeak);
	} else {
		subset = createCombinationSpectra(this.json);

	}

	subset = selectjsonWithInterval(subset, 'mass', min, max);
	if (subset.length > 0) {
		var scalledsub = this.spectrascale(subset, parentpeak);
		this.drawSpectral(scalledsub);
	} else {
		alert('no peaks in given range');
	}
};

/**
* function to add generate a cumalative spectrum. this does NOT involve drawing.or scaling.
* @param {tree format} json  tree format.
* @return {output} results can be feed into spectrascale.
* */



/** Function to drawn the scalled spectrum on the display.
 *this function contains also some code to prevent overlapping of the labels but this does not work pretty well.
 *(To make a solution that is compational feasable is a PhD on it's own)
 * This function als handles mouse events on the peaks.
 * @param {spectralscale object} object that returned by  function.
 */
MSnViewer.prototype.drawSpectral = function(poj) {
	partofjson = poj.child;
	var yscale = poj.yscale;
	XSPACING = 10;
	//add white background to prevent overlapping of the tree and makes the spectrum harder to read
	if(this.spectralBackground===''){
		this.spectralBackground = this.paper.rect(0, 0, this.PAPER_WIDTH,
				this.SPECTRAL_HEIGTH + 10).attr({
			'stroke-width' : '0',
			'stroke' : 'white',
			fill: 'white',
			'fill-opacity' : '0.9'
		});
	}
	// delete old peaks in spectrum

	for (var key in this.spectralTracker) {
		for (var elem in this.spectralTracker[key]) {
			this.spectralTracker[key][elem].remove();


		}
		delete this.spectralTracker[key];
	}
	
	// remove old x and y axis
	for (var d = 0; d < this.labels.length; d++) {
		this.labels[d].remove();
	}
	// variable to catch the widest label of Y axis
	var widestTextBox = 0;
	// draw labels y axis
	for (q = 0; q < yscale.length; q++) {
		var currentLabel = this.paper.text(0, yscale[q].y, yscale[q].label);
		this.labels.push(currentLabel);
		// save if label is widther then widest label
		if (widestTextBox < currentLabel.getBBox().width) {
			widestTextBox = currentLabel.getBBox().width;
		}
	}
	// add some pixels to create a bit of whitespace
	widestTextBox = widestTextBox + 2;

	// move the labels into the drawingboard to view the whole label
	for (d = 0; d < this.labels.length; d++) {
		this.labels[d].attr({
			'x' : (widestTextBox / 2)
		});
	}

	var labeltick;
	// draw thicks y axis
	for (q = 0; q < yscale.length; q++) {

		labeltick = 'M' + (widestTextBox - 2) + ' ' + (yscale[q].y) + 'L'
				+ (widestTextBox + 2) + ' ' + (yscale[q].y) + 'z';
		this.labels.push(this.paper.path(labeltick));
	}
	// add again some whitespace
	var positionXAxis = widestTextBox + 7;

	// draw x axis
	var svgBaseLine = 'M' + (positionXAxis - 5) + ' ' + (this.SPECTRAL_HEIGTH)
			+ 'L' + (this.SPECTRAL_WIDTH + positionXAxis) + ' '
			+ (this.SPECTRAL_HEIGTH) + 'z';
	this.labels.push(this.paper.path(svgBaseLine));
	// draw y axis
	svgBaseLine = 'M' + (positionXAxis - 5) + ' ' + (this.SPECTRAL_HEIGTH + 5)
			+ 'L' + (positionXAxis - 5) + ' ' + (5) + 'z';
	this.labels.push(this.paper.path(svgBaseLine));

	// draw thicks and labels on x axis
	for (q = 0; q < poj.xscale.length; q++) {
		this.labels.push(this.paper.text(poj.xscale[q].x + positionXAxis,
				this.SPECTRAL_HEIGTH + 8.5, poj.xscale[q].label));
		labeltick = 'M' + (poj.xscale[q].x + positionXAxis) + ' '
				+ (this.SPECTRAL_HEIGTH) + 'L'
				+ (poj.xscale[q].x + positionXAxis) + ' '
				+ (this.SPECTRAL_HEIGTH + 4) + 'z';
		this.labels.push(this.paper.path(labeltick));
	}



	// draw peaks
	var peakNonParent;
	var text;
	var svgPeaks;
	for (var i = 0; i < poj.child.length; i++) {

		 svgPeaks = 'M' + (partofjson[i].X + positionXAxis) + ' '
				+ this.SPECTRAL_HEIGTH + 'L'
				+ (partofjson[i].X + positionXAxis) + ' ' + partofjson[i].Y
				+ 'z';
		// draw labels
		peakNonParent = this.paper.path(svgPeaks);
		peakNonParent.node.id = partofjson[i].id;

		peakNonParent.click(function() {

			whenClickedOnRect(peakNonParent.node.id);
		});

		peakNonParent.mouseover(function() {
			peakMouseOver(this.node.id);
		});
		peakNonParent.mouseout(function() {
			peakMouseOut(this.node.id);
		});

		var firstid = partofjson[i].id;
		//if the peaks consist out of multile real peaks, then select the first
		if (!this.parentpeak) {
			firstid = firstid[0];
		}

		// create label above peaks
		var message = roundWithDecimals(partofjson[i].mass, 2)
				+ '\n'
				+ inchi2ElemenalComposition(splitInchis(this.json[firstid].inchi)[0]);

		text = this.paper.text(partofjson[i].X + positionXAxis,
				partofjson[i].Y - 8, message).attr({
			'font-size' : '9',
			'title' : partofjson[i].intensity
		});
		text.node.id = partofjson[i].id;
		text.mouseover(function() {
			peakMouseOver(this.node.id);
		});
		text.mouseout(function() {
			peakMouseOut(this.node.id);

		});
		// save peaks and labels into hashmap to alter them lateron
		this.spectralTracker[peakNonParent.node.id] = Array(peakNonParent, text);
		// add mapping from mass to peak id's
		this.mass2Peaks[partofjson[i].mass] = peakNonParent.node.id;

	}
	//check for overlapping text
  /*
 * From this part the labeling overlap is implemented
 */
	for (element in this.spectralTracker) {
		var outerPostion = getPosition(this.spectralTracker[element][1]);
		for (elementInner in this.spectralTracker) {

			if (elementInner !== element) {
				var innerPostion = getPosition(this.spectralTracker[elementInner][1]);
				//Check if leftside of inner box is in other box
				if (innerPostion.east > outerPostion.east
						&& innerPostion.east < outerPostion.west) {
					if (innerPostion.south < outerPostion.north
							&& innerPostion.south > outerPostion.south) {
					}
					if (innerPostion.north < outerPostion.north
							&& innerPostion.north > outerPostion.south) {

					}
				}//end if leftside
				if (innerPostion.west < outerPostion.west
						&& innerPostion.west > outerPostion.east) {
					var moveX = innerPostion.west - outerPostion.east;

					if (innerPostion.south < outerPostion.north
							&& innerPostion.south > outerPostion.south) {

						if (Math.abs(moveX) < 10) {
							this.spectralTracker[element][1].translate(moveX
									* -0.5, 0);
							this.spectralTracker[elementInner][1].translate(
									moveX * 0.5, 0);

						}

					}
					if (innerPostion.north < outerPostion.north
							&& innerPostion.north > outerPostion.south) {
						//overlap is minmal in x direction: shift the labels a bit to the side
						if (Math.abs(moveX) < 10) {
							this.spectralTracker[element][1].translate(
									moveX * 0.5, 0);
							this.spectralTracker[elementInner][1].translate(
									moveX * -0.5, 0);

						} else {
							var moveY = innerPostion.south - outerPostion.north;
							this.spectralTracker[element][1]
									.translate(0, moveY);
						}
					}
				}
			}//end if elelement !== inneelement
		}//end elmentinner for loop
	}//end elment for loop

	// draw old line
	if (poj.parent !== undefined) {
		svgPeaks = 'M' + (poj.parent.X + positionXAxis) + ' '
				+ this.SPECTRAL_HEIGTH + 'L' + (poj.parent.X + positionXAxis)
				+ ' ' + poj.parent.Y + 'z';
		var peakParent = this.paper.path(svgPeaks).attr({
			stroke: 'gray'
		});
		var textParent = this.paper.text((poj.parent.X + positionXAxis),
				poj.parent.Y - 8, roundWithDecimals(poj.parent.mass, 2));
		this.spectralTracker.peak_higher_level = Array(peakParent, textParent);
	

	}

};

function peakMouseOver(id) {
	ids = id.split(',');
	for (var e = 0; e < ids.length; e++) {
		json[ids[e]].wholetreenode.attr({
			fill: 'red'
		});
	}

	spectralTracker[id][0].attr({
		stroke: 'red'
	});
	// colour the label of peak red
	spectralTracker[id][1].attr({
		fill: 'red'
	});
	spectralTracker[id][1].toFront();
	spectralTracker[id][0].toFront();

	// change cursus to move
	spectralTracker[id][0].node.style.cursor = 'move';
}

function peakMouseOut(id) {
	var ids = id.split(',');
	for (var e = 0; e < ids.length; e++) {

		json[ids[e]].wholetreenode.attr({
			fill: 'black'
		});
	}
	spectralTracker[id][0].attr({
		stroke: 'black'
	});
	spectralTracker[id][1].attr({
		fill: 'black'
	});

}


/**scale (a subset of) peaks to the right size
 * @param {array of dicionaries} partofjson  A array of nodes selected from tree format.
 * @param {boolean} add_parent_peak if true the parent peak is shown in the spectrum.
 * @return {dictonary} this object is the input of drawSpectral.
 *
 */
MSnViewer.prototype.spectrascale = function(partofjson, add_parent_peak) {
	// var MININIMALPEAKHEIGHT = 0;
	var MAXIMALPEAKHEIGHT = this.SPECTRAL_HEIGTH - 15;

	// get parent peak

	var parentpeak = this.json[partofjson[0].parent];
	var maxintensity;
	var maxmass;
	var minmass;

	if (partofjson.length > 0) {
		subset = partofjson.sort(sortByMass);
		// get all masses including parent
		var masses = getArrayFrom2DArrayAsKey(subset, 'mass');
		var intensities = getArrayFrom2DArrayAsKey(subset, 'intensity');

		// get all intensities including parent

		maxintensity = (findmax(intensities));

		if (add_parent_peak && parentpeak !== undefined) {

			masses.push(parentpeak.mass);
			// make the parent peak 10% bigger then the largest real peak
			parentpeak.intensity = maxintensity * 1.1;
			maxintensity = parentpeak.intensity;
			// intensities.push(parentpeak.intensity);
			partofjson.push(parentpeak);
		}

		maxmass = (findmax(masses));
		minmass = (findmin(masses));

		maxmass = maxmass + (0.02 * (maxmass - minmass));
		minmass = minmass - (0.02 * (maxmass - minmass));

		for (var i = 0; i < partofjson.length; i++) {
			partofjson[i].X = parseInt(
					((partofjson[i].mass - minmass) / (maxmass - minmass))
							* (this.SPECTRAL_WIDTH), 10);
			//the postion is null when there is only one 1peak: in theat case center the peak
			if (!partofjson[i].X) {
				partofjson[i].X = this.SPECTRAL_WIDTH * 0.5;
			}
			partofjson[i].Y = parseInt(
					this.SPECTRAL_HEIGTH
							- ((partofjson[i].intensity / maxintensity) * MAXIMALPEAKHEIGHT),
					10);

		}
	}

	var parent;
	var child;
	if (parentpeak !== undefined) {
		parent = partofjson[(partofjson.length - 1)];
		partofjson.pop();
		child = partofjson;
	} else {
		parent = undefined;
		child = partofjson;

	}
	var xscale = [];
	var xrange = maxmass - minmass;
	var steps = [10, 5, 2.5, 1];

	for (var a = 0; a < steps.length; a++) {
		if (xrange < steps[a] * 10 && xrange > steps[a] * 5) {
			var xaxis = parseInt(((minmass + steps[a]) / steps[a]), 10)
					* steps[a];
			for (xaxis; xaxis < maxmass; xaxis = xaxis + steps[a]) {
				xscale.push({
					'x' : parseInt(((xaxis - minmass) / (maxmass - minmass))
							* (this.SPECTRAL_WIDTH), 10),
					'label' : xaxis
				});

			}// end for
		}// end if
	}// end for

	var exp = Math.floor(Math.log(Math.abs(maxintensity)) / Math.LN10);
	if (maxintensity === 0) {
		exp = 0;
	}

	var remainer = maxintensity / Math.pow(10, exp);
	var yrange = roundWithDecimals(remainer, 2);

	var yscale = [];
	steps = [10, 5, 2.5, 1, 0.5, 0.25, 0.2, 0, 1];
	for (a = 0; a < steps.length; a++) {

		if (yrange < steps[a] * 10 && yrange > steps[a] * 5) {
			// * 5);

			for (var yaxis = 0; yaxis < yrange; yaxis = yaxis + steps[a]) {
				yscale
						.push({
							'y' : this.SPECTRAL_HEIGTH
									- parseInt((yaxis / yrange)
											* MAXIMALPEAKHEIGHT, 10),
							'label' : NumberAsEvalue(yaxis + 'e' + exp, 1)
						});

			}
		}
	}

	var allpeaks = {
		'child' : child,
		'parent' : parent,
		'yscale' : yscale,
		'xscale' : xscale
	};

	return (allpeaks);

};

function startTree() {
	for (var i in lines.items) {
		try {
			lines.items[i].attr({
				opacity: 0.5
			});
		} catch (ex) {
		}
		if (lines.items[i].type == 'path') {
			lines.items[i].ox = lines.items[i].getBBox().x;
			lines.items[i].oy = lines.items[i].getBBox().y;
		} else {
			lines.items[i].ox = lines.items[i].attrs.cx
					|| lines.items[i].attrs.x;
			lines.items[i].oy = lines.items[i].attrs.cy
					|| lines.items[i].attrs.y;
		}
	}

	// alert(this.ox);
}
function moveTree(dx, dy) {

	for (var i in lines.items) {
		if (lines.items[i].attrs.cx) { // circle has other dimension labels
			lines.items[i].attr({
				cx: lines.items[i].ox + dx,
				cy: lines.items[i].oy + dy
			});
		} else if (lines.items[i].attrs.x) {
			lines.items[i].attr({
				x: lines.items[i].ox + dx,
				y: lines.items[i].oy + dy
			});
		} else { // path has other dimension labels
			lines.items[i].translate(lines.items[i].ox
					- lines.items[i].getBBox().x + dx, lines.items[i].oy
					- lines.items[i].getBBox().y + dy);
		}
	}

}
function upTree() {

	for (var i in lines.items) {
		lines.items[i].attr({
			opacity: 1
		});
	}
}
/**
 * Creates the MSn visualisation
 *
 * @constructor
 * @this {MSnViewer}
 * @param {tree format} jsonString tree format.
 * @param {string} divid The HTML element the visualization is bound to.
 */


function MSnViewer(jsonString, divid) {
	
	var winW = 1125, winH = 900;
	if (document.body && document.body.offsetWidth) {
	 winW = document.body.offsetWidth;
	 winH = document.body.offsetHeight;
	}
	if (document.compatMode=='CSS1Compat' &&
	    document.documentElement &&
	    document.documentElement.offsetWidth ) {
	 winW = document.documentElement.offsetWidth;
	 winH = document.documentElement.offsetHeight;
	}
	if (window.innerWidth && window.innerHeight) {
	 winW = window.innerWidth;
	 winH = window.innerHeight;
	}	
	
	// add extra information to json to make handeling easier
	this.json = addNumberOfChildrenToTree(jsonString);
	this.json = AddLevelOfNode(this.json);
	this.json = addOrderOfWholeTree(this.json, ['']);

	// create paper as a global variable so all functions have access to draw
	this.PAPER_WIDTH = winW - 200;
	// set drawing element to div element
	this.paper = Raphael(document.getElementById(divid), this.PAPER_WIDTH, winH + 250);
	// a bookkeeping array for drawn objects
	this.nodesTracker = {};
	this.spectralTracker = {};
	//set to make drawing of tree possible
	this.lines = this.paper.set();
	// neutralloss stores all drawn structures and adition  drawn structures
	//TODO change name
	this.neutralLoss = {};
	// contains mappping from mass to peaks: these are neasasry for cumatalive spectrum where 1 map is mapped to multiple nodes
	this.mass2Peaks = {};
	// contains background of selected node in tree
	this.backgroundNode = '';
	// white background behind the spectrum to prevent vissible overlap of tree and spectrum
	this.spectralBackground = '';
	//TODO this labels have to been renamed and checked
	//contains labels of spectra and other stuff
	this.labels = [];

	// set the parent peak to specifing parent peak: needed for zooming to prevent switching to other spectra
	this.parentpeak = false;
	//set width of spectum space (this is a bit smaller the the paper width to let fit in the labels)
	this.SPECTRAL_WIDTH = 900;
	//set height of spectum space
	this.SPECTRAL_HEIGTH = 200;

	// draw the cumaltalive spectrum
	this.DrawCumulativeSpectra();

	// set y distance for all drown nodes
	this.drawWholeTree();
	//TODO: move this to a function
	// add a background to the tree to get a bigger drag and drop area (not only
	// the tree itself)
	this.lines.push(this.paper.rect(this.lines.getBBox().x,
			this.lines.getBBox().y, this.lines.getBBox().width,
			this.lines.getBBox().height).attr({
		fill: 'white',
		stroke: 'white'
	}).toBack());
	lines = this.lines;
	this.lines.drag(moveTree, startTree, upTree);

}

//function to check if inchi is valid
//WARNIG: this function is a hack!
//	a string that should be a inchi
// return true or false
/**
 * function to check if inchi is valid: WARNIG: this function is a hack!
 *
 * @param {string} inchi a string that should be a inchi.
 * @return {boolean} true if the inchi is a vallid inchi.
 */

function checkInchi(inchi) {

var valid = false;
if (inchi !== undefined) {
	var re = new RegExp(/\/\w+\//);

	if (inchi.match(re)) {
		valid = true;
	}
}

return (valid);
}

//
