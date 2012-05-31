package nl.nmc.metitree

import grails.converters.*

import org.openscience.cdk.tools.manipulator.*
import org.sams.MZData
import org.sams.manipulator.FingerMZDataManipulator
import org.sams.manipulator.MZDataManipulator
import org.springframework.context.*

class MzdataService {

    static transactional = true

	def fromJson(jsonString, mode = 1) {
						
		def parsedJson = JSON.parse(jsonString)
		
		double[][] matrix = new double[parsedJson.size()][5]
		def matrixRow = 0
		
		parsedJson.sort { a,b -> a.key as int <=> b.key as int }.each { l -> //make sure the matrix is sorted according to the tree !!!
			matrix[matrixRow][0] = l.value.id as Double //id
			matrix[matrixRow][1] = l.value.parent as Double //parent
			matrix[matrixRow][2] = l.value.mass as Double //mass
			matrix[matrixRow][3] = l.value.intensity as Double //intensity
			matrix[matrixRow][4] = mode as Double //charge == 1 for positive and -1 for negative

			matrixRow++ //matrixRow + 1
		}

		return MZDataManipulator.getMZData(matrix, new MZData())
	}
	
    def fromCsvFile(File csvfile, mode = 1) {
		
		//mzData from text
		double[][] matrix = new double[csvfile.readLines().size()][5]
		def matrixRow = 0

		csvfile.eachLine{ line ->
			def arrLine = line.split(",")
			matrix[matrixRow][0] = arrLine[0] as Double //id
			matrix[matrixRow][1] = arrLine[1] as Double //parent
			matrix[matrixRow][2] = arrLine[2] as Double //mass
			matrix[matrixRow][3] = arrLine[3] as Double //intensity
			matrix[matrixRow][4] = mode as Double //charge == 1 for positive and -1 for negative

			matrixRow++ //matrixRow + 1
		}

		return MZDataManipulator.getMZData(matrix, new MZData())
    }
	
	def featuresEcFromMzdata(MZData mzdata){
		return FingerMZDataManipulator.getbs24_24_1(mzdata).unique()
	}
	
	def featuresNomFromEcFeatures(features, digits = "#"){
		return FingerMZDataManipulator.getNominalPath(features, digits).unique()
	}
	
	def featuresNomFromMzdata(MZData mzdata){
		return FingerMZDataManipulator.getbs24_24_1Nom(mzdata).unique()
	}
	
	def simScoreV3(List listA, List listB){		
		return FingerMZDataManipulator.tanimotto3(listA, listB)
	}	
}
