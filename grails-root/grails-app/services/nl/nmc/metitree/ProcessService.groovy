package nl.nmc.metitree

import nl.nmc.metitree.Msnfile
import nl.nmc.metitree.Directory
import nl.nmc.metitree.ProcessJob
import nl.nmc.metitree.ProcessSettings
import java.util.concurrent.TimeoutException

import javax.servlet.http.HttpSession
import org.springframework.web.context.request.RequestContextHolder
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ProcessService {

	static transactional = false

	// include required services
	def mconfService
	def mzxmlService
	def cmlService
	def jsonService
	def csvService
	def pdfService

	ProcessJob runJob(ProcessJob job){

		try {
			job.msnfiles?.each { mzXMLFile ->
				this.processMsnfile(job, mzXMLFile)
			}
		} catch(e){
			log.error e
		}

		return job
	}

	/*
	 * Process a single Msnfile
	 */
	void processMsnfile(ProcessJob job, Msnfile msnfile) {

		// reset error count
		def errorsFound = null

		// prepare a unique name for storing the files
		def processFileName = "${UUID.randomUUID().toString()}"

		// prepare mzXML file (as a File())
		def mzXMLFile = new File("${msnfile.location}")

		/*
		 * STEP 1 : XCMS
		 * extract peaks using XCMS via RApache and save the results is a file
		 */
		def xcmsOutMsnfile 	= null
		def xcmsOut 		= null

		if (errorsFound == null){
			try {

				xcmsOutMsnfile 	= new Msnfile(processjob: job, member: job.member, directory: msnfile.directory, parent: msnfile, filename: "${processFileName}.xcms", name: "${msnfile.name}.xcms").save()
				xcmsOut 		= new File(xcmsOutMsnfile.location)

				// call service
				mzxmlService.convertToTabularFormat(mzXMLFile, xcmsOut, job.settings.settingsmap)

			} catch (e) {

				// log error and cleanup
				errorsFound = "Was unable to convert to tabular format using XCMS on file: ${msnfile?.name}..."
				log.error("${errorsFound}: ${e}")
			}
		}

		/*
		 * STEP 2 : CML = enrich the MSn data using the MEF generator
		 */
		def cmlOutMsnfile = null

		if (errorsFound == null){
			try {

				cmlOutMsnfile = new Msnfile(processjob: job, member: job.member, directory: msnfile.directory, parent: xcmsOutMsnfile, filename: "${processFileName}.cml", name: "${msnfile.name}.cml").save()

				// call service
				cmlService.fromTabularFormat(msnfile, xcmsOutMsnfile, cmlOutMsnfile, job.settings.settingsmap)
				cmlOutMsnfile.storeFingerprints()
			} catch (TimeoutException e){
				errorsFound = "Time to enrich the CML file took too long: ${cmlOutMsnfile?.name}..."
				job.report += "${new Date()} - ERROR: ${errorsFound}\n"
			} catch (Exception e) {
				// log error and cleanup
				errorsFound = "Was unable to enrich msnfile using the MSn: ${cmlOutMsnfile?.name}..."
			}

			if (errorsFound){ //clean up after errors
				new File(cmlOutMsnfile.location).delete()
				cmlOutMsnfile.delete()
			}
		}

		/*
		 * STEP 3 : JSON formats
		 */
		def jsonOutMsnfile, jsonTreeFile  	= null
		def mjsonOutMsnfile, jsonMTreeFile  = null

		if (errorsFound == null){
			try {

				// trees
				jsonOutMsnfile = new Msnfile(processjob: job, member: job.member, parent: cmlOutMsnfile, directory: msnfile.directory, filename: "${processFileName}.json", name: "${msnfile.name}.json").save()
				jsonTreeFile = new File(jsonOutMsnfile.location)
				jsonTreeFile << jsonService.treeFromCML(cmlOutMsnfile)

				// mtree
				mjsonOutMsnfile = new Msnfile(processjob: job, member: job.member, parent: cmlOutMsnfile, directory: msnfile.directory, filename: "${processFileName}.master.json", name: "${msnfile.name}.master.json").save()
				jsonMTreeFile = new File(mjsonOutMsnfile.location)
				jsonMTreeFile << jsonService.treeFromCML(cmlOutMsnfile, mconfService.fromLabel("metitree.processing.cml.occurrence") as Float)

			} catch (e) {

				// log error and cleanup
				errorsFound = "Creation of JSON formatted files failed on file(s): ${jsonOutMsnfile?.name} and/or ${mjsonOutMsnfile?.name}..."
				log.error("${errorsFound}: ${e}")
			}
		}

		/*
		* STEP 4 : CSV/TAB
		*/
	   def csvOutMsnfile = null
	   def tabOutMsnfile = null

	   if (errorsFound == null){
		   try {

			   // csv
			   csvOutMsnfile = new Msnfile(processjob: job, member: job.member, parent: mjsonOutMsnfile, directory: msnfile.directory, filename: "${processFileName}.csv", name: "${msnfile.name}.csv").save()
			   csvService.csvFromCML(mjsonOutMsnfile, csvOutMsnfile)

			   // tab
			   tabOutMsnfile = new Msnfile(processjob: job, member: job.member, parent: mjsonOutMsnfile, directory: msnfile.directory, filename: "${processFileName}.tab", name: "${msnfile.name}.tab").save()
			   csvService.tabFromCML(mjsonOutMsnfile, tabOutMsnfile)

		   } catch (e) {

			   // log error and cleanup
			   errorsFound = "Creation of CSV/TAB formatted files failed on file(s): ${csvOutMsnfile?.id} and/or ${tabOutMsnfile?.name}..."
			   log.error("${errorsFound}: ${e}")
		   }
		}

	   /*
	   * STEP 5 : PDF
	   */
	   def pdfOutMsnfile = null

	   if (errorsFound == null){
		   try {

			   // pdf
			   pdfOutMsnfile = new Msnfile(processjob: job, member: job.member, parent: csvOutMsnfile, directory: msnfile.directory, filename: "${processFileName}.pdf", name: "${msnfile.name}.pdf").save()
			   pdfService.pdfFromCSV(csvOutMsnfile, pdfOutMsnfile)
		   } catch (e) {

			   // log error and cleanup
			   errorsFound = "Creation of PDF formatted files failed on file: ${pdfOutMsnfile?.name}..."
			   log.error("${errorsFound}: ${e}")
		   }
		}
	}
}
