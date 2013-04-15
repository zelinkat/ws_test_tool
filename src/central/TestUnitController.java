package central;

import java.util.HashMap;
import java.util.Map;

import testingUnit.LocalTestUnit;
import testingUnit.RemoteTestUnit;
import testingUnit.TestingUnit;

public class TestUnitController {

	
	private Map<Integer,RemoteTestUnit> unitStorage;
	private LocalTestUnit localUnit;
	private TestingUnit selectedUnit;
	
	
	
	
	public TestUnitController(){
		
		this.unitStorage = new HashMap<Integer,RemoteTestUnit>();
	}
	
	public void addRemoteUnit(Integer key){
		RemoteTestUnit newRemoteUnit = new RemoteTestUnit(); 
		unitStorage.put(key, newRemoteUnit);
	}
	
	public void addLocalUnit(){
		this.localUnit = new LocalTestUnit();
	}
	
	public void selectMachine(Integer key){
		if(key == 0){
			this.selectedUnit = localUnit;
		}else{
			this.selectedUnit = unitStorage.get(key);
		}
	}
	
	public void removeRemoteUnit(Integer key){
		this.unitStorage.remove(key);
	
	}
	
	public RemoteTestUnit getRemoteTestUnit(Integer key){
		
		return this.unitStorage.get(key);
	}
	
	public LocalTestUnit getLocalTestUnit(){
		return this.localUnit;
	}
	
	
	
	public void runTestOnUnit(String path, int unitId){
		if(unitId == 0){
			localUnit.setTestList(path);
			localUnit.runTestList();
		}else{
			
		}
	}
	
	public void runTestOnAllUnits(){
		
	}
	
	
		
}
