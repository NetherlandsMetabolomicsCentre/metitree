package nl.nmc.metitree

import grails.converters.*

import org.openscience.cdk.CDKConstants
import org.openscience.cdk.tools.manipulator.*
import org.sams.MZData
import org.sams.io.CMLReader
import org.sams.manipulator.MZDataManipulator
import org.springframework.context.*
import org.xmlcml.cml.element.CMLSpectrum

class JsonService {

	static transactional = true

	def treeFromCML(msnFile, occurrence = null) { //provided msnFile should always be the CML

		def input = new BufferedInputStream(new FileInputStream(msnFile.location))
		def cmlReader = new CMLReader(input)
		input.close()
		def mzData = cmlReader.read(new MZData())
		
		try {
			if (occurrence != null){ //produce mastertree
				def masterMzData = MZDataManipulator.group(mzData, occurrence)
				mzData = masterMzData
			}
		} catch(e) {
			log.error(e)
		}

		def JSONnodes = []
		def JSONtrees = [:]

		try {

			// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			//extract reactions
			def reactionScheme = mzData.getListReactions()
			//extract spectra
			def specElem = mzData.getListSpectra().getSpectrumElements()

			def molSet = ReactionSchemeManipulator.getAllMolecules(reactionScheme)
			def reaSet = ReactionSchemeManipulator.getAllReactions(reactionScheme)

			molSet.molecules().each { molecule ->

				def id = molecule.getID()
				
				// find if it is a fragment o loss
				if(!id.contains("Loss")){

					// formule
					def inchi = ""
					molecule.getProperty(CDKConstants.FORMULA).each { molecPart ->
	
						if (inchi != ""){ inchi += "," } //add a , after each Inchi when there is a next one...
						try {
							
							inchi += "InChi=1S/${MolecularFormulaManipulator.getString(molecPart)}/p+1"
						} catch(e)  {
							inchi += "InChi=1S/${molecPart.replace(" ","")}/p+1"
						}
					}
	
					def cmlSpectList = mzData.getListSpectra()
					def cmlPeakMol = null
					def spectrumMol = null
	
					// group
					def numGroups = 0;
					specElem.each { spectrum ->
						CMLSpectrum.getDescendantPeaks(spectrum).each { peak ->
							if(peak.getMoleculeRefs() != null){
								if(peak.getMoleculeRefs()[0].equals(id)){
									spectrumMol = spectrum
									cmlPeakMol = peak
									def ml = spectrum.getMetadataListElements().get(0).getMetadataDescendants();
									ml.each { metadata ->
										if(metadata.getDictRef().equals("nmc:groupPeakMSn"))
											numGroups = Integer.parseInt(metadata.getContent());
									}
								}
							}
						}
					}
	
					def reacSetR = ReactionSetManipulator.getRelevantReactionsAsProduct(reaSet, molecule)
					// mass
					def mass = cmlPeakMol?.getXValue()
					// intensity
					def intensity = cmlPeakMol?.getYValue()
	
					// id precusor
					def precursorID = ""
					def inchiNL = ""
	
					/* OLD VERSION
					 if(reacSetR.getReaction(0) != null){
					 def nl = reacSetR.getReaction(0).getProducts().getAtomContainer(1)
					 inchiNL = "InChi=1S/${MolecularFormulaManipulator.getString(nl.getProperty(CDKConstants.FORMULA))}"
					 precursorID = reacSetR.getReaction(0).getReactants().getAtomContainer(0).getID()
					 }
					 */
	
					if(reacSetR.getReaction(0) != null){
						
						// Determine what is the fragment
						def nl0 = reacSetR.getReaction(0).getProducts().getAtomContainer(0)
						def nl1 = reacSetR.getReaction(0).getProducts().getAtomContainer(1)
						
						def nl = nl0
						if(nl1.getID().contains("Loss"))
							nl = nl1
							
						nl.getProperty(CDKConstants.FORMULA).each { molecPartNL ->
							
							try {
								inchiNL += "InChi=1S/${MolecularFormulaManipulator.getString(molecPartNL)}/"
							} catch(e)  {
								inchiNL += "InChi=1S/${molecPartNL.replace(" ","")}/"
							}
						}
	
						precursorID = reacSetR.getReaction(0).getReactants().getAtomContainer(0).getID()
					} else {
						inchiNL = "" // empty inchiNL to avoid looking at data of other molecules
					}
	
					if (cmlPeakMol){
						//setup node
						def JSONnode = [:]
						JSONnode["id"] = "${molecule.getID()}"
						JSONnode["parent"] = "${precursorID}"
						JSONnode["mass"] = "${mass}"
						JSONnode["intensity"] = "${intensity}"
						JSONnode["inchi"] = "${inchi}"
						JSONnode["inchiloss"] = "${inchiNL}"
						JSONnode["group"] = "${numGroups}"
	
						JSONnodes << JSONnode
					}
				}
			}

			def groupedNodes = JSONnodes.groupBy{ it.group }
			groupedNodes.each { group, nodes ->
				def branch = [:] //init new branch of nodes
				nodes.each { node ->
					def nodeID = node.id as Integer
					branch[nodeID] = node //add all Nodes of a Group to a Branch and add an id as key of Node HashMap
				}
				JSONtrees[group] = branch.sort() //add branche to tree
			}

		} catch(e) {
			// something went wrong
			log.error("ERROR PARSING FILE ${e}")
			return false
		}


		return (JSONtrees as JSON) as String

	}
}
