package nl.nmc.metitree

class Member {
	
	static belongsTo = [ usergroup:Usergroup ]
	
	static hasMany = [ processjobs: ProcessJob ]
		
	static mapping = {
		processjobs(lazy:false)
	}
		
	Usergroup	usergroup
	String		name
	String		username
	String		password
	String		email
	Boolean		admin
	Date		dateCreated
	Date		lastUpdated
	
    static constraints = {
    }
	
	String toString() { "${this.name}" }
}
