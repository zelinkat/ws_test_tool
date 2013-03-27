package modalWindows;

import gui.MainWindow;
import gui.ProjectNavigator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 * 
 * @author Tomas Zelinka, xzelin15@stud.fit.vutbr.cz
 *
 */
public class NewTestCaseDialog extends InputModalWindow {
	/**
	 * 
	 */
	
	public static final int TESTTYPE_HTTP = 1;
	public static final int TESTTYPE_FAULT = 2;
	public static final int TESTTYPE_HTTP_FAULT = 3;
	
	
	private JTextField testCaseName;
	private JTextField path;
	private int testType;
	private static final long serialVersionUID = 9187751988881264097L;

	public NewTestCaseDialog(){
		super("New Test Case", 640,580);
		
	}
	
		
	/**
	 * 
	 */
	@Override
	protected void putContent(){
		
		JPanel labels = new JPanel(new GridLayout(0,1,0,10));
		JPanel fields = new JPanel(new GridLayout(0,1,0,10));
		testType = 0;
		String selectedPath = MainWindow.getSuitePath();
		if(selectedPath.isEmpty()){
			messageLabel.setText("Path not selected. Please select Test Suite Path");
		}
		
		testCaseName = new JTextField(10);
		path = new JTextField(MainWindow.getSuitePath()+ File.separator,10);
		JLabel label = new JLabel("Test Case: ");
		JLabel pathLabel = new JLabel("Path: ");
        
		labels.add(pathLabel);
		labels.add(label);
		
		path.setEditable(false);
			
		fields.add(path);
		fields.add(testCaseName);
		fields.setSize(new Dimension(200,50));
		
				
		label.setHorizontalAlignment(JLabel.RIGHT);
        label.setFont(label.getFont().deriveFont(Font.PLAIN,14.0f));
        pathLabel.setHorizontalAlignment(JLabel.RIGHT);
        pathLabel.setFont(label.getFont().deriveFont(Font.PLAIN,14.0f));
        
        getSecondInsidePanel().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getSecondInsidePanel().setLayout(new BorderLayout());
        getSecondInsidePanel().add(labels,BorderLayout.WEST);
        getSecondInsidePanel().add(fields);
        
        
        
        JPanel checkBoxPanel = new JPanel();
        JCheckBox box1 = new JCheckBox("HTTP Test");
        JCheckBox box2 = new JCheckBox("Fault Injection");
        JCheckBox box3 = new JCheckBox("HTTP Test with Fault Injection");
        
        checkBoxPanel.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));
        
        CheckBoxListener checkBoxListener = new CheckBoxListener();
        box1.addItemListener(checkBoxListener);
        box1.addItemListener(checkBoxListener);
        box1.addItemListener(checkBoxListener);
        
        checkBoxPanel.add(box1);
        checkBoxPanel.add(box2);
        checkBoxPanel.add(box3);
        
        
        getSecondInsidePanel().add(checkBoxPanel,BorderLayout.SOUTH);
        /*     
        JTabbedPane tabbedPane = new JTabbedPane();
              
        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(1,1,1,1));
        UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);
        
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        //JComponent panel1 = makeTextPanel("Panel #1");
        JPanel panel1 = new HttpRequestEditor();
        
        tabbedPane.addTab("Http Request", panel1);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
         
        JComponent panel2 = makeTextPanel("Panel #2");
        tabbedPane.addTab("Fault Injection",panel2);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
         
        JComponent panel3 = makeTextPanel("Panel #3");
        tabbedPane.addTab("Test Case Settings", panel3);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
         
         panel1.setPreferredSize(new Dimension(410, 250));
         //panel1.setMinimumSize(new Dimension(410, 220));
         
        //Add the tabbed pane to this panel.
        getSecondInsidePanel().add(tabbedPane,BorderLayout.SOUTH);
         
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        
        */
    }
     
    
	private void setTestType (int type){
		this.testType = type;
	}
	
	private int getTestType(){
		return this.testType;
	}
	
	/**
	 * 
	 * @param text
	 * @return
	 */
	protected JComponent makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
	    filler.setHorizontalAlignment(JLabel.CENTER);
	    panel.setLayout(new GridLayout(1, 1));
	    panel.add(filler);
	    return panel;
	}
	/**
	 * 
	 * 
	 */
	@Override
	protected String testEmptyInput(){
		return getCaseName();
	}
	
	/**
	 * 
	 */
	private String getCaseName(){
		return testCaseName.getText();
	}
	
	/**
	 * 
	 * 
	 */
	@Override
	protected void initButtons(){
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new OkButtonAction());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelButtonAction()); 
		Document projectField = testCaseName.getDocument();
		projectField.addDocumentListener(new ButtonStateController(okButton,testCaseName,messageLabel));
		
		if(testEmptyInput().isEmpty())
			okButton.setEnabled(false);
		
		addButton(okButton);
		addButton(cancelButton);
		
	}
	
	/**
	 * 
	 */
	private class OkButtonAction  implements ActionListener{
		
		public void actionPerformed(ActionEvent e) {
             
			File newTestCase = new File(MainWindow.getDataRoot()+File.separator+MainWindow.getSuitePath()+File.separator+getCaseName());
             
			if (newTestCase.exists()){
            	 messageLabel.setText("Test suite with this name already exists");
             }else{
            	 
            	 newTestCase.mkdir();
            	 ProjectNavigator.refreshTree();
			
            	 System.out.println("New project name: "+ getCaseName());
            	 setVisible(false);
            	 dispose();
             }
		}
	}
	/**
	 * 
	 */
	private class CancelButtonAction  implements ActionListener{
				
		public void actionPerformed(ActionEvent e) {
            setVisible(false);
            dispose();
        }
	}
	
	
	
	private class CheckBoxListener implements ItemListener{
		
		public void itemStateChanged(ItemEvent e){
			Object testCheckBox = e.getItemSelectable();
			
			if ((testCheckBox.toString()) == "HTTP Test"){
				System.out.println("httptest");
			}else{
				System.out.println(testCheckBox.toString().);
			}
		}
	}
	
}