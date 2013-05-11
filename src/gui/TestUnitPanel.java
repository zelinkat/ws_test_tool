package gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import logging.ConsoleLog;
import testingUnit.NewResponseListener;
import data.HttpMessageData;
import data.TestCaseSettingsData;
import data.XMLFormat;

public class TestUnitPanel extends JPanel implements NewResponseListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5352882010763584802L;
	
	private JTable testCasesTable;
	private JTable responsesTable;
	private DefaultTableModel testCasesTableModel;
	private DefaultTableModel responsesTableModel;
	
	private JPanel outputDetailPanel;
	private JPanel panel;
	private JPanel topPanel;
	private JPanel centerPanel;
	
	private JPanel requestPanel;
	private JPanel responsePanel;
	private JSplitPane outputDetails;
	private JScrollPane requestScrollPane;
	private JScrollPane responseScrollPane;
	private JScrollPane testCasesTableScrollPane;
	private JScrollPane responsesTableScrollPane;
	private JLabel 		responseLabel;
	private JLabel 		requestLabel;
	private JEditorPane requestEditorPane;
	private JEditorPane responseEditorPane;
	private ArrayList<TestCaseSettingsData> testListData;
	private JLabel requestTableLabel;
	private JLabel responsesTableLabel;
	private HashMap<String,HttpMessageData[]> responses;
	 
		
	public TestUnitPanel (){
		initComponents();
		setupComponents();
		
	}
	
	 /**
	 * 
	 * @param path
	 */
	public void openTestList(ArrayList<TestCaseSettingsData> dataArray){
		
		clearTable(testCasesTableModel);
		this.testListData = dataArray;
		for(TestCaseSettingsData data : dataArray){
			loadTestCaseToTable(data);
		}
		
		ConsoleLog.Print("[UnitPanel] Opened Test List");
		
	}
	
	/**
	 * 
	 * @param casePath
	 */
	private void loadTestCaseToTable(TestCaseSettingsData data){
				
		if(data == null){
			ConsoleLog.Message("Test case file from list not found.");
		}else{
			insertToTable(data);
		}
	}
	
	/**
	 * 
	 * @param casePath
	 */
	public void insertTestCaseToTable(TestCaseSettingsData testCaseSettings){
		
		
		//TestCaseSettingsData testCaseSettings = (TestCaseSettingsData) ioProvider.readObject(casePath+TestCaseSettingsData.filename);
		if(this.testListData != null){
			if(detectDuplicityInTable(testCaseSettings.getName())){
				ConsoleLog.Message("Test case already in test list");
			}else{
				this.testListData.add(testCaseSettings);
				insertToTable(testCaseSettings);
			}
		}else{
			ConsoleLog.Message("Any opened test suite");
		}
	}

	@Override
	public void onNewResponseEvent(HttpMessageData[] dataArray) {
		ConsoleLog.Print("[UnitPanel] dostal jsem data, delka:" + dataArray.length);
		String  adrress = dataArray[0].getName() + dataArray[0].getThreadNumber();
		responses.put(adrress, dataArray);
		//ConsoleLog.Print("[UnitPanel] dalsi data od vlakna :"+ adrress);	
		//ConsoleLog.Print("[UnitPanel] vracim se");
	}
	
	public void clearResults(){
		ConsoleLog.Print("[UnitPanel] clearing");
		responses.clear();
		clearTable(responsesTableModel);
		responseEditorPane.setText("");
		
	}
	
	/**
	 * 
	 * @param path
	 */
	public ArrayList<TestCaseSettingsData> getTestListData(){
		
		if(this.testListData != null ){
		
			ConsoleLog.Print("[UnitPanel] Testlist size: "+ testListData.size());
			
			for(int id = 0; id <testCasesTableModel.getRowCount(); id++){
			
				Integer threadsNumberRow = (Integer)testCasesTableModel.getValueAt(id,2);
				Integer loopsCountRow =  (Integer)testCasesTableModel.getValueAt(id,3);
				Boolean runTestRow =  (Boolean)testCasesTableModel.getValueAt(id,4);
				
				TestCaseSettingsData testCaseSettings = testListData.remove(id);
				testCaseSettings.setThreadsNumber(threadsNumberRow);
				testCaseSettings.setLoopNumber(loopsCountRow);
				testCaseSettings.setRun(runTestRow);
				testListData.add(id, testCaseSettings);
			}
				
			
		}else{
			ConsoleLog.Message("Any opened test suite");
		}
		
		return this.testListData;
	}
	
	
	
	
	
	/**
	 * 
	 */
	public void removeSelectedTestCase(){
		
		int row = testCasesTable.getSelectedRow();
		ConsoleLog.Print("[UnitPanel] row: "+row);
		
		if(row >= 0){
			this.testListData.remove(row);
			testCasesTableModel.removeRow(row);
			
			for(int i =0; i < testCasesTable.getRowCount(); i++){
				testCasesTableModel.setValueAt((Integer)i, i,0);
			}
			
		}
	}

	/**
	 * 
	 */
	private void clearTable(DefaultTableModel table){
		table.setRowCount(0);
	}
	
	private void insertToTable(TestCaseSettingsData data){
		Object[] newRow = new Object[] {testCasesTable.getRowCount(),data.getName(),data.getThreadsNumber(),data.getLoopNumber(),new Boolean(data.getRun()),new Boolean(data.getUseProxy())};
		testCasesTableModel.insertRow(testCasesTable.getRowCount(), newRow);
	}
	
	/**
	 * 
	 */
	private boolean detectDuplicityInTable(String caseName){
		String caseNameFromTable = "";
		int rowNumber = testCasesTable.getRowCount();
		
		for (int i = 0; i < rowNumber;i++){
			caseNameFromTable = (String) testCasesTableModel.getValueAt(i,1);
			
			if(caseNameFromTable.compareTo(caseName) == 0)
				return true;
		}
		return false;
	}
	
	
	
	
	private class RequestSelectionAdapter extends MouseAdapter{
		public void mouseClicked(MouseEvent e) {
			clearTable(responsesTableModel);
			responseEditorPane.setText("");
			requestEditorPane.setText("");
			int row = testCasesTable.getSelectedRow();
			String name = (String) testCasesTableModel.getValueAt(row,1);
			Integer threadNumber = (Integer) testCasesTableModel.getValueAt(row,2);
			ConsoleLog.Print("[UnitPanel] case to find in table:" + responses.keySet().size());
			
			for(int i = 0; i < threadNumber; i++){
				String key = name + i;
				HttpMessageData[] caseResponses = responses.get(key);
					if(caseResponses != null){
						for(HttpMessageData data : caseResponses){
							Object[] newRow = new Object[] {data.getName(),null,data.getMethod(),data.getResource(),null,data.getLoopNumber(),data.getThreadNumber()};
							responsesTableModel.insertRow(responsesTable.getRowCount(), newRow);
						}
					}else{
						ConsoleLog.Message("Response not present!");
					}
			}
		}
	}
	
	private class ResponseSelectionAdapter extends MouseAdapter{
		public void mouseClicked(MouseEvent e) {
			int row = responsesTable.getSelectedRow();
			
			String name = (String) responsesTableModel.getValueAt(row,0);
			Integer threadNumber = (Integer) responsesTableModel.getValueAt(row,2);
			Integer loopNumber = (Integer) responsesTableModel.getValueAt(row,1);
			String key = name + threadNumber;
			
	//		ConsoleLog.Print("[UnitPanel] " + key +" "+loopNumber );
			HttpMessageData caseResponse =(responses.get(key))[loopNumber];
			requestEditorPane.setText(XMLFormat.format(caseResponse.getRequestBody()));
			responseEditorPane.setText(XMLFormat.format(caseResponse.getResponseBody()));
		}
	}
	
	/**
	 * 
	 */
	private void initComponents(){
		testCasesTable = new JTable();
		responsesTable = new JTable();
		outputDetails = new JSplitPane();
		outputDetailPanel = new JPanel();
		topPanel = new JPanel();
		centerPanel = new JPanel();
		panel = new JPanel();
		requestPanel = new JPanel();
		responsePanel = new JPanel();
		requestScrollPane = new JScrollPane();
		responseScrollPane = new JScrollPane();
		testCasesTableScrollPane = new JScrollPane();
		responsesTableScrollPane = new JScrollPane();
		responseLabel = new JLabel();
		requestLabel = new JLabel();
		requestEditorPane = new JEditorPane();
		responseEditorPane = new JEditorPane();
		//ioProvider = new DataProvider();
		requestTableLabel = new JLabel("Requests");
		responsesTableLabel = new JLabel("Responses");
		responses = new HashMap<String,HttpMessageData[]>();
	}
	
	/**
	 * 
	 */
	private void setupComponents(){
		this.setLayout(new BorderLayout());
		
		panel.setLayout(new BorderLayout());
		panel.add(topPanel,BorderLayout.PAGE_START);
		panel.add(centerPanel,BorderLayout.CENTER);
		this.add(panel,BorderLayout.PAGE_START);
		this.add(outputDetailPanel, BorderLayout.CENTER);
		
		outputDetailPanel.setLayout(new BorderLayout());
		outputDetailPanel.add(outputDetails,BorderLayout.CENTER);
		
		
		topPanel.add(testCasesTableScrollPane,BorderLayout.PAGE_START);
		centerPanel.add(responsesTableScrollPane,BorderLayout.CENTER);
		
		testCasesTableScrollPane.getViewport().add(testCasesTable);
		responsesTableScrollPane.getViewport().add(responsesTable);
		
		outputDetails.setDividerLocation(430);
		
		testCasesTable.setOpaque(false);
		responsesTable.setOpaque(false);
		testCasesTable.addMouseListener(new RequestSelectionAdapter());
		responsesTable.addMouseListener(new ResponseSelectionAdapter());
		
		testCasesTable.setModel(new javax.swing.table.DefaultTableModel(
	            
				
				new Object [][] {

	            },
	            new Object [] {
	                "#", "Name","Threads","Count","Http Unit","Proxy Unit"
	            }
	        ) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 2602176893989437449L;

				public Class<?> getColumnClass(int index){
					
					if(index == 4 || index == 5)
						return Boolean.class;
					
					return getValueAt(0, index).getClass();
				}
				
				boolean[] canEdit = new boolean [] {
	                false, false,true,true,true,false
	            };

	            public boolean isCellEditable(int rowIndex, int columnIndex) {
	                return canEdit [columnIndex];
	            }
			
		}	);
		
		testCasesTableModel = (DefaultTableModel) testCasesTable.getModel();
		responsesTable.setModel(new javax.swing.table.DefaultTableModel(
	            new Object [][] {

	            },
	            new String [] {
	            		 "Name","Http Code", "Method","Service","Elapsed Time" ,"Loop number","Thread number", 
	            }
	        ) {
	            /**
				 * 
				 */
				private static final long serialVersionUID = 3376163326142594714L;
				boolean[] canEdit = new boolean [] {
	                false, false,false, false,false, false,false
	            };

	            public boolean isCellEditable(int rowIndex, int columnIndex) {
	                return canEdit [columnIndex];
	            }
	        });
		responsesTableModel = (DefaultTableModel) responsesTable.getModel();
		
		javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
		topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
            	 .addContainerGap()
            	  .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(testCasesTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                    .addComponent(requestTableLabel))
                 .addContainerGap())
        );
            
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
               
                 .addComponent(requestTableLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(testCasesTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addContainerGap())
        );
		
		
		
		
        javax.swing.GroupLayout centerPanelLayout = new javax.swing.GroupLayout(centerPanel);
		centerPanel.setLayout(centerPanelLayout);
        centerPanelLayout.setHorizontalGroup(
            centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(responsesTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                    .addComponent(responsesTableLabel))
                   .addContainerGap())
        );
            
        centerPanelLayout.setVerticalGroup(
            centerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(centerPanelLayout.createSequentialGroup()
                
                .addComponent(responsesTableLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(responsesTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE,100, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addContainerGap())
        );
		
		
		requestLabel.setText("Request");
        requestEditorPane.setEditable(false);
        requestScrollPane.setViewportView(requestEditorPane);
        MainWindow.initEditorPane(requestEditorPane, requestScrollPane);

        javax.swing.GroupLayout requestPanelLayout = new javax.swing.GroupLayout(requestPanel);
        requestPanel.setLayout(requestPanelLayout);
        requestPanelLayout.setHorizontalGroup(
            requestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(requestPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(requestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(requestScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                    .addComponent(requestLabel))
                .addContainerGap())
        );
        requestPanelLayout.setVerticalGroup(
            requestPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(requestPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(requestLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(requestScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addContainerGap())
        );

        outputDetails.setLeftComponent(requestPanel);

        
        responseLabel.setText("Response");
        responseEditorPane.setEditable(false);
        responseScrollPane.setViewportView(responseEditorPane);
        MainWindow.initEditorPane(responseEditorPane, responseScrollPane);

        javax.swing.GroupLayout responsePanelLayout = new javax.swing.GroupLayout(responsePanel);
        responsePanel.setLayout(responsePanelLayout);
        responsePanelLayout.setHorizontalGroup(
            responsePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(responsePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(responsePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(responseScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                    .addComponent(responseLabel))
                .addContainerGap())
        );
        responsePanelLayout.setVerticalGroup(
            responsePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(responsePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(responseLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(responseScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addContainerGap())
        );

        outputDetails.setRightComponent(responsePanel);
	}
	
	
	
	
}
