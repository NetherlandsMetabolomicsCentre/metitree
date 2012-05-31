package nl.nmc.metitree

class Database {
	
	static belongsTo = [ usergroup:Usergroup ]
	
	static hasMany = [ msnfiles: Msnfile ]
	
  static mapping = {
      table 'metidbs' //the name database is a reserved word in databases like MySQL
	  msnfiles(lazy:false)
	}

	Usergroup	usergroup
	String		name	
	Date		dateCreated
	Date		lastUpdated
	
    static constraints = {
    }
}
