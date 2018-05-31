package application.view;

import java.util.HashMap;
import java.util.Vector;
import java.util.function.Predicate;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.controls.RecursiveTreeItem;

import application.Main;
import application.model.Chart;
import application.util.Connect;
import application.view.ModifyDialogController.TopicView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Callback;


public class ModifyDialogController {
	@FXML private JFXTextField chartType;
	@FXML private JFXTextField chartName;
	
	@FXML private JFXTextField firstSensor;
	@FXML private JFXTextField firstSensorLabel;
	@FXML private JFXTextField secondSensor;
	@FXML private JFXTextField secondSensorLabel;
	@FXML private JFXTextField searchTextField;
	
	@FXML private Label unitLabel;
	@FXML private JFXComboBox<String> unitCombo;
	
	@FXML private JFXTreeTableView<TopicView> topicTable;
	
	@FXML private JFXButton apply;
	@FXML private JFXButton cancle;
	
	private Stage dialogStage;
	private Chart chart;
	private boolean applyClicked = false;
	private int seletNum;	// 선택한 TextField를 구분하기 위한 변수 
	
	private DraggableNode node;
	
	private Main main;
	
	private TabPane tabPane = null;
    private Tab tab = null;
	
	@FXML
	private void initialize() {
		unitCombo.getItems().addAll("％","\u00b0C","㏓","㎍/㎥");
		unitCombo.valueProperty().addListener(new ChangeListener<String>() {
	        @Override public void changed(ObservableValue ov, String oldV, String newV) {
	            chart.setUnit(newV);
	            if(!tab.getText().contains("*"))
	    			tab.setText("* "+tab.getText());
	            
	          }    
	     });
		setTreeTable();
	}
	public ModifyDialogController() {
		searchTextField = firstSensor;
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public void setMain(Main main) {
		this.main = main;
		tabPane = main.getBoardLayoutController().getTabPane();
		tab = tabPane.getSelectionModel().getSelectedItem();
	}
	public void setDraggableNode(DraggableNode node) {
		this.node = node;
	}
	
	
	// 다이얼로그에서 변경될 차트를 선택한다.
	public void setChart(Chart chart) {
		this.chart = chart;
		
		chartType.setText(chart.getType());
		if(!chart.getType().equals("value")) {
			unitLabel.setDisable(true);
			unitCombo.setDisable(true);
		}else {		// value일 때 
			if(!chart.getUnit().equals("null")) 
				unitCombo.setPromptText(chart.getUnit());
		}
		
		if(!chart.getChartName().equals("null"))
			chartName.setText(chart.getChartName());
		if(!chart.getFirstTopic().equals("null"))
			firstSensor.setText(chart.getFirstTopic());
		if(!chart.getFirstName().equals("null"))
			firstSensorLabel.setText(chart.getFirstName());
		if(!chart.getSecondTopic().equals("null"))
			secondSensor.setText(chart.getSecondTopic());
		if(!chart.getSecondName().equals("null"))
			secondSensorLabel.setText(chart.getSecondName());
			
		chartName.requestFocus();
	}
	
	
	
	
    /**
     * 사용자가 cancel을 클릭하면 호출된다.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
	
    /**
     * 사용자가 apply를 클릭하면 true를, 그 외에는 false를 반환한다.
     *
     * @return
     */
    public boolean isApplyClicked() {
        return applyClicked;
    }
	
    // 사용자가 apply버튼을 누르면 호출.
    @FXML
    private void handleApply() {
    	if(inInputValid()) {    			
    		chart.setChartName(chartName.getText());
    		
    		chart.setFirstTopic(firstSensor.getText());
    		
    		chart.setFirstName(firstSensorLabel.getText());
    		
    		if(secondSensor.getText().equals(""))
    			chart.setSecondTopic("null");
    		else {
    			chart.setSecondTopic(secondSensor.getText());
        		// topic과 매칭되는 ctrTopic설정
    			if(main.isDbConnect) {
	        		String ctrTopic = main.connect.getCtrTopic(chart.getFirstTopic());
	        		if(ctrTopic != null)
	        			chart.setFirstControl(ctrTopic);
    			}
    		}
    		
    		if(secondSensorLabel.getText().equals(""))
    			chart.setSecondName("null");
    		else
    			chart.setSecondName(secondSensorLabel.getText());
    		
    		if(main.isDbConnect) {
	    		// topic과 매칭되는 ctrTopic설정
	    		String ctrTopic = main.connect.getCtrTopic(chart.getFirstTopic());
	    		if(ctrTopic != null)
	    			chart.setFirstControl(ctrTopic);
    		}
    		
    		if(!tab.getText().contains("*"))
    			tab.setText("* "+tab.getText());
    		applyClicked = true;
    		dialogStage.close();
    	}
    }
    
    // 사용자의 입력검사
    @FXML
    private boolean inInputValid() {
    	String errorMessage = "";
    	
    	if(chartName.getText() == null || chartName.getText().length() == 0 || chartName.getText().equals("null")) {
    		errorMessage += "No valid Chart Name\n";
    	}
    	/*
    	if(firstSensor.getText() == null || firstSensor.getText().length() == 0) {
    		errorMessage += "No valid First Sensor\n";
    	}
    	if(firstSensorLabel.getText() == null || firstSensorLabel.getText().length() == 0) {
    		errorMessage += "No valid First Sensor Label\n";
    	}
    	*/
    	BoardLayoutController blc = main.getBoardLayoutController();
    	Tab tab = blc.getTabPane().getSelectionModel().getSelectedItem(); 
    	// 차트 이름이 중복이면 생성하지 않는다.
		for (int j = 0; j < main.getDashBoardHashMap().get(tab.getId()).size(); j++) {
			if(chartName.getText().equals(chart.getChartName()) == false && chartName.getText().equals(main.getDashBoardHashMap().get(tab.getId()).get(j).getChartName())) {
				errorMessage += "차트 이름이 중복됩니다.";
			}
		}
    	
    	if(errorMessage.length() == 0) {
    		return true;
    	}else {
    		 // 오류 메시지를 보여준다.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
    	}
    	
    }
    
    @FXML
    public void clickFirstTextFiled() {
    	seletNum = 0;
    }
    @FXML
    public void clickSecondTextFiled() {
    	seletNum = 1;
    }
    
    // Topic을 담을 TreeTableView 설정 
    public void setTreeTable() {
    	JFXTreeTableColumn<TopicView, String> session1 = new JFXTreeTableColumn<>("Topic Level 1");
    	session1.setPrefWidth(150);
    	session1.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TopicView, String>, ObservableValue<String>>(){
    		@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TopicView, String> param) {
    			if(session1.validateValue(param)) {
            		return param.getValue().getValue().session1;
            	}else {
            		return session1.getComputedValue(param);
            	}
    		}
    	});
    	
    	JFXTreeTableColumn<TopicView, String> session2 = new JFXTreeTableColumn<>("Topic Level 2");
    	session2.setPrefWidth(150);
    	session2.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TopicView, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TopicView, String> param) {
            	if(session2.validateValue(param)) {
            		return param.getValue().getValue().session2;
            	}else {
            		return session2.getComputedValue(param);
            	}
            }
        });
    	
    	JFXTreeTableColumn<TopicView, String> session3 = new JFXTreeTableColumn<>("Topic Level 3");
    	session3.setPrefWidth(150);
    	session3.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<TopicView, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<TopicView, String> param) {
            	if(session3.validateValue(param)) {
            		return param.getValue().getValue().session3;
            	}else {
            		return session3.getComputedValue(param);
            	}
    		}
        });
    	
    	String[] colors = {"#D9E5FF","#E4F7BA"};
    	// topicList를 분할하여 table 각각의 session에 넣는다
    	ObservableList<TopicView> topics = FXCollections.observableArrayList();
    	if(main.isDbConnect) {
    		for (int i = 0; i < main.topicList.size(); i++) {
        		int c = 0;
        		topics.add(new TopicView(main.dividedTopicList.get(c).get(i), main.dividedTopicList.get(c+1).get(i), main.dividedTopicList.get(c+2).get(i)));
    		}
    	}
    	
    	final TreeItem<TopicView> root = new RecursiveTreeItem<TopicView>(topics, RecursiveTreeObject::getChildren);
    	topicTable.getColumns().setAll(session1, session2, session3);
    	topicTable.setRoot(root);
    	topicTable.setShowRoot(false);
    	
    	// 검색 창 설정
    	searchTextField.textProperty().addListener(new ChangeListener<String>() {
    		@Override
    		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
    			topicTable.setPredicate(new Predicate<TreeItem<TopicView>>() {
					@Override
					public boolean test(TreeItem<TopicView> topic) {
						Boolean flag =  topic.getValue().session1.getValue().contains(newValue) || 
								topic.getValue().session2.getValue().contains(newValue) || 
								topic.getValue().session3.getValue().contains(newValue);
						return flag;
					}
				});
    		}
		} );
    	
    	// un grup,grup 설정
    	JFXButton groupButton = new JFXButton("Group");
        groupButton.setOnAction((action) -> new Thread(() -> topicTable.group(session1)).start());

        JFXButton unGroupButton = new JFXButton("unGroup");
        unGroupButton.setOnAction((action) -> topicTable.unGroup(session1));
        
        // table에서 선택된 topic 가져오기
        topicTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<TopicView>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<TopicView>> observable, TreeItem<TopicView> old_val, TreeItem<TopicView> new_val) {
            	TreeItem<TopicView> selectedItem = new_val;
            	//String selectTopic = selectedItem.getValue().session1.getValue() + "/" + selectedItem.getValue().session2.getValue()+"/"+selectedItem.getValue().session3.getValue();
            	
            	String selectTopic = "";
                selectTopic += selectedItem.getValue().session1.getValue();
                selectTopic += "/";
                selectTopic += selectedItem.getValue().session2.getValue();
                selectTopic += "/";
                selectTopic += selectedItem.getValue().session3.getValue();
                
                if(seletNum == 0)
                	firstSensor.setText(selectTopic);
                else 
                	if(!chart.getType().equals("value"))
                		secondSensor.setText(selectTopic);
                	else
                		secondSensorLabel.editableProperty().set(false);
            }

        });
    }
    
    
    class TopicView extends RecursiveTreeObject<TopicView> {
        StringProperty session1;
        StringProperty session2;
        StringProperty session3;

        public TopicView(String session1, String session2, String session3) {
        	this.session1 = new SimpleStringProperty(session1);
        	this.session2 = new SimpleStringProperty(session2);
        	this.session3 = new SimpleStringProperty(session3);
        }
    }

}
