package gui;



public class FileNode extends java.io.File {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2796997206556668704L;

	/**
	 * 
	 * @param directory
	 */
	public FileNode(String directory) {
        super(directory);
    }

	/**
	 * 
	 * @param parent
	 * @param child
	 */
    public FileNode(FileNode parent, String child) {
        super(parent, child);
    }
    
    /**
     * 
     * @return
     */
    public boolean isHttpTestCase(){
    	
    	if (this.isDirectory() && (this.getName().compareTo("Http") == 0)){
    		    		
    		//ConsoleLog.Print("http test case clicked");
    		return true;
    	}
    	return false;
    }

    public boolean isWSDL(){
    		
    	
    	if (this.isFile() ){
    		String filename = this.getName().substring(this.getName().length()-4,this.getName().length());
    	   	if(filename.compareTo("wsdl") == 0)
    			return true;
    		//ConsoleLog.Print("wsdl test case clicked");
    	}
    	return false;
    }
    
    
    
    public boolean isTestList(){
	    	
    	if (this.isFile() && (this.getName().compareTo("testlist.xml") == 0)){
    		    		
    		//ConsoleLog.Print("wsdl test case clicked");
    		return true;
    	}
    	return false;
    }
    
    public boolean isSettings(){
    	
    	if (this.isFile() && (this.getName().compareTo("settings.xml") == 0)){
    		    		
    		//ConsoleLog.Print("wsdl test case clicked");
    		return true;
    	}
    	return false;
    }
    
    
    /**
     * 
     * @return
     */
    public boolean isFaultInjectionTestCase(){
    	
    	if (this.isDirectory() && (this.getName().compareTo("FaultInjection") == 0)){
    		
    		//ConsoleLog.Print("FaultInjection case clicked");
    		return true;
    	}
    	return false;
    }
    
      
    /**
     * 
     */
    @Override
    public String toString() {
        return getName();
    }
    
    public String getAbsolutePath() {
        return this.getPath();
    }
}
