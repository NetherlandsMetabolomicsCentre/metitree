package nl.nmc.metitree

import org.sams.MZData;

/**
 * Thread to control what ever you want.
 * 
 * @author Miguel Rojas-Cherto
 *
 */
class ThreadExt extends Thread {  
    private Object object;
    
    public Object getObject() {  
        return this.object;  
    }  
      
    public void setObject(Object object) {  
        this.object = object;  
    }  

}
