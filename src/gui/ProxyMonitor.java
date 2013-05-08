package gui;

import java.awt.BorderLayout;
import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import logging.ConsoleLog;
import proxyUnit.ProxyPanelListener;
import central.UnitController;

public class ProxyMonitor extends JPanel {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5479720929723166064L;
	private int proxyUnitCounter;
	private UnitController controller;
	private JTabbedPane tabbedPane;
	
	public ProxyMonitor(UnitController controller){
		this.proxyUnitCounter = 0;
		this.controller = controller;
		initComponents();
		addUnit("",0);
	}
	
	
	/**
	 * This method inserts remote unit tab to the tabbed pane
	 * and initialize new remote unit in unit controller
	 */
	public void addUnit(String host, int port){
		int selectedPanel = 0;
		
		
		try{
			controller.addProxyUnit(proxyUnitCounter,host,port);
			
			ProxyUnitPanel panel = new ProxyUnitPanel();
			ProxyPanelListener listner = new ProxyPanelListener(panel);
			
			if(proxyUnitCounter == 0){
				tabbedPane.addTab("Local Unit",panel);
				controller.setPanelListener(listner,proxyUnitCounter);
				
			}else{
				
				controller.setPanelListener(listner,proxyUnitCounter);
				tabbedPane.addTab("Remote Unit "  + proxyUnitCounter,panel);
				selectedPanel = tabbedPane.indexOfTab("Remote Unit " +proxyUnitCounter);
			}
		}catch(RemoteException ex){
			ConsoleLog.Message(ex.getClass().getName() + ": " + ex.getMessage());
			return;
		}catch(NotBoundException ex){
			ConsoleLog.Message(ex.getClass().getName() + ": " +ex.getMessage());
			return;
		}catch(Exception ex){
			ConsoleLog.Message(ex.getClass().getName() + ": " +ex.getMessage());
			ex.printStackTrace();
			return;
		}
		
		tabbedPane.setSelectedIndex(selectedPanel);
		ConsoleLog.Print("[ProxyMonitor] panel index:" + selectedPanel);
		proxyUnitCounter++;
	}
	
	public void removeUnit(){
		int panelIndex = tabbedPane.getSelectedIndex();
		
		if (panelIndex != 0){
			controller.removeTestUnit(getUnitKey());
			tabbedPane.remove(panelIndex);
			tabbedPane.revalidate();
			ConsoleLog.Print("[ProxyMonitor] Removed Unit: " + getUnitKey());
		}else{
			ConsoleLog.Print("[ProxyMonitor] You cannot close local testing unit");
		}
	}
	
	
	public void runUnit(String path){
		

		int panelIndex = tabbedPane.getSelectedIndex();
		
		String[] splittedPath = path.split("\\"+File.separator);
		
		if(splittedPath.length > 2){
			
			String caseName = splittedPath[3];
			
			if(panelIndex == 0){
				tabbedPane.setTitleAt(panelIndex,"Local Unit"+" - "+caseName );
			}else{
				tabbedPane.setTitleAt(panelIndex,"Remote Unit "+ getUnitKey() +" - "+caseName );
			}
			//getSelectedPanel().clearResults();
			controller.runProxy(path,getUnitKey());
			
		}else{
			ConsoleLog.Message("Any Test case selected.");
		}
	}
	
	public void stopUnit(){
		controller.stopProxy(getUnitKey());
		
		int panelIndex = tabbedPane.getSelectedIndex();
		if(panelIndex == 0 ){
			tabbedPane.setTitleAt(panelIndex, "Local Unit");
		}else{
			tabbedPane.setTitleAt(panelIndex,"Remote Unit "+ getUnitKey());
		}
		
	}
	
		
	/**
	 * 
	 * @return JPanel - returns the selected unit panel
	 */
	private ProxyUnitPanel getSelectedPanel(){
		ProxyUnitPanel selectedPanel = (ProxyUnitPanel)tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
		ConsoleLog.Print("[ProxyMonitor] returned Unit: " + getUnitKey());
		return selectedPanel;
	}
	
	
	/**
	 * 
	 * @return int - return unit key, the key is used in tab titles
	 * 				 and like an id for testing units
	 */ 
	public int getUnitKey(){
		
		int panelIndex = tabbedPane.getSelectedIndex();
		String keyString = tabbedPane.getTitleAt(panelIndex);
		
		if(panelIndex == 0)// local unit selected
			return 0;
		else{
			int key = Integer.parseInt(keyString.split(" ")[2]);
			return key;
		}
	}
	
	private void initComponents(){
		tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	            ConsoleLog.Print("[ProxyMonitor] Tab: " + tabbedPane.getSelectedIndex());
	        }
	    });
		this.setLayout(new BorderLayout());
		this.add(tabbedPane,BorderLayout.CENTER);
	}
}

