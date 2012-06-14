package nl.nmc.metitree

import grails.converters.*
import groovyx.gpars.*
import java.util.concurrent.TimeUnit

class ImportController {
	
	def jsonService
	def csvService
	def pdfService
	def importService

    def index = { 
		render "Use this to import data from a Usergroup folder"
	}
	
	def select = {
		
		if (params.database && params.directory) {
			
			def uploadedfile = request.getFile('importfile')
			
			if (!uploadedfile?.isEmpty()){
				
				try { 
	
					// select correct way of processing the file based on extension
					if (uploadedfile.getOriginalFilename().toLowerCase().tokenize(".")[-1] == 'zip'){
						
						//	... ziplocation
						def zipLocation = "/tmp/metitree_import/${UUID.randomUUID().toString()}"
						def zipLocationFile = new File("${zipLocation}")
							zipLocationFile.mkdirs()
						
						// ... zipfile
						def zipFile = new File("${zipLocation}/import_file.zip")
						zipFile.setWritable(true, false) && zipFile.canWrite()
						
						uploadedfile.transferTo(zipFile)
						
						def usergroup 		= session.member.usergroup
						def member			= session.member
						def strDirectory	= params.directory ?: 'import'
						def directory		= Directory.findByUsergroupAndName(usergroup, strDirectory) ?: new Directory(usergroup: usergroup, name: strDirectory).save()
						def strDatabase		= params.database ?: 'importDb'
						def database		= Database.findByUsergroupAndName(usergroup, strDatabase) ?: new Database(usergroup: usergroup, name: strDatabase).save()
					
						//	... use ant to unzip
						def ant = new AntBuilder()
						ant.unzip(src: zipFile.canonicalPath, dest: zipLocation, overwrite: "true")
													
						//Add MzXml's to directory
						zipLocationFile.eachFile { f ->
							if (importService.fileparts(f)['ext'] == 'mzxml'){								
								importService.addMsnfile('mzxml', f, member, directory)
							}
						}
						
						//	... read the zipLocationFile
						zipLocationFile.eachFile { f ->
							
							//	... determine extension of unzipped files
							def unzippedFilename = f.absolutePath.toLowerCase()
							def unzippedName = unzippedFilename.tokenize("/")[-1]
							def unzippedExt = unzippedName.tokenize(".")[-1]
										
							if (unzippedExt == 'cml'){
								
								//	... clear vars
								def cmlContents = ''
								def filenameMzXml = ''
								def mzXml = ''
								def xcms = ''
								def cml = ''
								def settings 	= [:]
								def inchi = ''
				
								//read some of the metadata from the cml
								new XmlSlurper().parseText(f.text).spectrumList.metadataList.metadata.each {
									settings[it.'@dictRef' as String] = it.'@content' as String
								}
																
								// get mzxml used to produce CML
								if (settings['nmc:parentFile']){
									
									filenameMzXml = "${settings['nmc:parentFile'].tokenize("/")[-1]}".toLowerCase()

									mzXml = Msnfile.findByDirectoryAndName(directory, filenameMzXml)
													
									if (mzXml != ''){
										
										def settingsHash = [:]
											settingsHash['mzgap'] = settings['nmc:mzgap']
											settingsHash['elements'] = settings['nmc:elements']
											settingsHash['accuracy'] = settings['nmc:accuracy']
											settingsHash['snthresh'] = settings['nmc:snthresh']
											settingsHash['rules'] = settings['nmc:rules']
				
										def processSettings = importService.processSettingsFromHashMap(settingsHash, member)
				
										//add mzxml to processJob
										def processJob = new ProcessJob(active:true, member:member,	lastStatus:2, report: "#import job", settings: processSettings,	fids: mzXml.id as String).save()										
										
										// add xcms (if available)
										xcms = importService.addMsnfile('xcms', new File("${f.absolutePath.toLowerCase()}".replace('.cml', '.xcms')), member, directory, mzXml, processJob)
										
										// add cml
										def tempCmlFile = new File('/tmp/' + UUID.randomUUID().toString() + '.cml') << f.text.replaceAll("${settings['nmc:parentFile']}", "${xcms.parent.location}")
										cml = importService.addMsnfile('cml', tempCmlFile , member, directory, xcms, processJob)
										tempCmlFile.delete()
										
										//add the cml to the import db
										cml.addToDatabase(database)
				
										// get/set InChI from old CML to Metitree Db
										if (settings['nmc:parentCompounds']){
											def compound = Compound.findByInchi(settings['nmc:parentCompounds']) ?: new Compound(inchi: settings['nmc:parentCompounds']).save()
											mzXml.compound = compound
										}
										
										//fetch comments from cml
										if (settings['nmc:comments']){
											def comments = settings['nmc:comments']
											mzXml.comments = comments
										}
										
										//save additional data to the mzXML file
										mzXml.save()
										
										//add json
										def json = importService.addMsnfile('json', new File("${f.absolutePath.toLowerCase()}".replace('.cml', '.json')), member, directory, cml, processJob)
										def masterjson = importService.addMsnfile('master.json', new File("${f.absolutePath.toLowerCase()}".replace('.cml', '.master.json')), member, directory, cml, processJob)
										
										//add csv, tab & pdf
										def csv = importService.addMsnfile('csv', new File("${f.absolutePath.toLowerCase()}".replace('.cml', '.csv')), member, directory, masterjson, processJob)
										def tab = importService.addMsnfile('tab', new File("${f.absolutePath.toLowerCase()}".replace('.cml', '.tab')), member, directory, masterjson, processJob)
										def pdf = importService.addMsnfile('pdf', new File("${f.absolutePath.toLowerCase()}".replace('.cml', '.pdf')), member, directory, csv, processJob)
									}
								}
							}
						}
						
						flash.message = "Uploading finished."
					}	
				} catch (e) {
					log.error (e)
					flash.message = "Uploading failed. Please make sure the uploaded zip is correct!"
				}
			}
		}
	}
}
