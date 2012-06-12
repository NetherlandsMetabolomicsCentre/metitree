package nl.nmc.metitree

import grails.converters.*

import java.io.StringWriter
import java.util.concurrent.TimeoutException

import org.openscience.cdk.tools.manipulator.*
import org.sams.MEFgenerator
import org.sams.MZData
import org.sams.io.CMLWriter
import org.sams.io.PDFWriter
import org.sams.manipulator.MZDataManipulator
import org.springframework.context.*
import nl.nmc.metitree.ThreadExt

import org.springframework.web.context.request.RequestContextHolder

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class CmlService {

    static transactional = true

	def mconfService
	
    def fromTabularFormat(mzxmlMsnFile = null, tabularMsnFile = null, cmlMsnFile = null, settings = [:]) throws TimeoutException {
					
		if (mzxmlMsnFile == null || cmlMsnFile == null || tabularMsnFile == null){
			return false
		}

		// fetch File object from MsnFile		
		def mzxmlFile = new File(mzxmlMsnFile.location)
		def tabularFile = new File(tabularMsnFile.location)
		
		// Read tabular file line by line and look for peaks
		def treeMap = [:]

		def xcmsOutFileLines = tabularFile.readLines()
		xcmsOutFileLines.each { xcmsOutFileLine ->
			xcmsOutFileLine.splitEachLine('\t') {xcmsOutFileLineFields ->
				if (xcmsOutFileLineFields.size() > 5){ //with 5 columns it must be data!
					def peak = new Expando()
					peak.id = (xcmsOutFileLineFields[0]).trim()
					peak.parent = (xcmsOutFileLineFields[1]).trim()
					peak.level = (xcmsOutFileLineFields[2]).trim()
					peak.rt = (xcmsOutFileLineFields[3]).trim()
					peak.mass = (xcmsOutFileLineFields[4]).trim()
					peak.intensity = (xcmsOutFileLineFields[5]).trim()
					peak.file = 1 //for now a fixed value, should come from the mzXML
					peak.group = (xcmsOutFileLineFields[7]).trim()
					treeMap[peak.id] = peak
				} 
			}
		}
			
		double[][] matrix = new double[treeMap.size()][8];
		
		def matrixRow = 0
		
		treeMap.each { peak ->
			matrix[matrixRow][0] = peak.value.id as Double
			matrix[matrixRow][1] = peak.value.parent as Double
			matrix[matrixRow][2] = peak.value.level as Double
			matrix[matrixRow][3] = peak.value.rt as Double
			matrix[matrixRow][4] = peak.value.mass as Double
			matrix[matrixRow][5] = peak.value.intensity as Double
			matrix[matrixRow][6] = peak.value.file as Double
			matrix[matrixRow][7] = peak.value.group as Double

			matrixRow++ //matrixRow + 1
		}
	
		
		def instrumentRef = "orbiTrap.1"
		def protocol = "23.df"
		
		def mzData = new MZData()		
			mzData = MZDataManipulator.getMZData2(matrix, mzData, mzxmlFile.canonicalPath.toString(), instrumentRef, protocol, 1)		
		def gen = new MEFgenerator(mzData)
		
		//Set Mass Accuracy
		List<Integer> listMA = new ArrayList<Integer>()
		listMA.add(settings["accuracy-L1"] ? (settings["accuracy-L1"] as int) : 5) //default is 5
		listMA.add(settings["accuracy-L2"] ? (settings["accuracy-L2"] as int) : 5)
		listMA.add(settings["accuracy-L3"] ? (settings["accuracy-L3"] as int) : 5)
		listMA.add(settings["accuracy-L4"] ? (settings["accuracy-L4"] as int) : 5)
		listMA.add(settings["accuracy-L5"] ? (settings["accuracy-L5"] as int) : 5)
		gen.setMassAccuracy(listMA)
		
		//Set Rules		
		def rules = []
		try {
			if (settings["rulerdber"]) { rules.add(settings["rulerdber"]) } 
			if (settings["rulenitrogenr"]) { rules.add(settings["rulenitrogenr"]) }
		} catch(e) {
			log.error("Was unable to set the rules: ${rules}")
		}

		gen.setRules(rules.toListString());
		
		//Set Elements
		if (settings["elements"]?.size()){ //check if the user limits the result to a set of elements
			
			def splitElements = settings["elements"].split(',').toList()
			
			def elements = new String[splitElements.size()]
			def elementsMin = new int[splitElements.size()]
			def elementsMax = new int[splitElements.size()]
			
			def elementCount = 0
			splitElements.each { element ->

				//determine element characters
				def elementName = ""
				
				element.each { elementChar ->
					def charRange = 'a'..'z'
					if (charRange.contains(elementChar.toLowerCase())){
						elementName += (elementChar as String)
					}
				}
								
				elements[elementCount] = elementName
				
				//determine range
				def elementRange = ((element-elementName).replace('..',',')).split(',').toList()
				if (elementRange.size() == 2){ //we received a range
					elementsMin[elementCount] = elementRange[0] as int
					elementsMax[elementCount] = elementRange[1] as int
				} else { //we received a fixed value, so range is value..value :)
					elementsMin[elementCount] = elementRange[0] as int
					elementsMax[elementCount] = elementRange[0] as int
				}

				elementCount++
			}
												
			def range = MEFgenerator.creatingRange(elements, elementsMax, elementsMin)
			gen.setRangeMolecularFormula(range)
			
		}
			
		def mzData2
		try {
	
			def threadTimeLimit = mconfService.fromLabel("metitree.processing.jobs.max.execution") as double
			mzData2 = startThread(gen,threadTimeLimit);
		} catch (TimeoutException e){
			throw new TimeoutException()
		} catch (e) {
			// something went wrong
			log.error("error calling: def mzData2 = gen.init() ${e.message}")
			throw new Exception()
		}

		// now save the cml
		def output2 = new StringWriter()
		def cmlWriter = new CMLWriter(output2)
		cmlWriter.write(mzData2)
		cmlWriter.close()

		//save to cml file
		def cmlFile = new File(cmlMsnFile.location)
			cmlFile << output2.toString()
		
		return true
    }
	
	private MZData startThread(MEFgenerator gen, double threadTimeLimit) throws TimeoutException {
		
		ThreadExt thread = new ThreadExt() {
			@Override
			public void run() {
				MZData mzData0 = gen.init()
				setObject(mzData0)
			}
		};
		thread.start()
		long startMilliSecond = System.currentTimeMillis()
		while(thread.isAlive())
		{
			long currentMilliSecond = System.currentTimeMillis()
			double diff = currentMilliSecond-startMilliSecond
			log.info ("Thread ${thread} running for ${diff/1000} seconds")
			Thread.sleep( 15000 )
			if(diff > threadTimeLimit)
			{
				log.error ("Thread was killed!")
				thread.interrupt()
				thread.stop()
				throw new TimeoutException()
				break
			}
		}
		return thread.getObject()
	}
}
