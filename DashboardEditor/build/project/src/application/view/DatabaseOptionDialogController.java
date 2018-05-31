package application.view;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import application.Main;
import application.util.Connect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class DatabaseOptionDialogController {
	@FXML private JFXTextField urlTextField;
	@FXML private JFXTextField userTextField;
	@FXML private JFXTextField passwordTextField;
	@FXML private JFXTextField topicTextField;
	@FXML private JFXTextField controlTopicTextField;
	@FXML private JFXListView<String> topicListView;
	@FXML private JFXListView<String> controlTopicListView;
	@FXML private Button add;
	@FXML private Button remove;
	@FXML private JFXButton apply;
	@FXML private JFXButton cancel;
	@FXML private TabPane tabPane;
	
	private Stage dialogStage;
	private Main main;
	
	private ObservableList<String> topicList = FXCollections.observableArrayList();
	private ObservableList<String> controlTopicList = FXCollections.observableArrayList();
	
	@FXML
	private void initialize() {
		topicListView.setItems(topicList);
		controlTopicListView.setItems(controlTopicList);
		
		FileReader file;
		// json parser
        JSONParser parser = new JSONParser();
        try {        	
        	file = new FileReader("dbData.json");
        	Object obj = parser.parse(file);
			JSONObject jsonObject = (JSONObject) obj;
			urlTextField.setText((String)jsonObject.get("url"));
			userTextField.setText((String)jsonObject.get("user"));
        } catch (Exception e) {
        	e.printStackTrace();
		}
        
	}
		
	@FXML
	private void handleAdd() {
		if(topicTextField.getText() == null || topicTextField.getText().length() == 0) {
			Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Please Insert Topic.");
	        alert.setHeaderText("");
	        alert.setContentText("토픽을 입력하세요.");
	        alert.showAndWait();
			return;
		}
		String newTopic = topicTextField.getText();
		String newControl = controlTopicTextField.getText();
		if(controlTopicTextField.getText() == null || controlTopicTextField.getText().length() == 0)
			newControl = "null";
		if(main.isDbConnect) {
			if(main.connect.isTopicExist(newTopic)) {
				Alert alert = new Alert(AlertType.INFORMATION);
		        alert.setTitle("Already Existed.");
		        alert.setHeaderText("");
		        alert.setContentText("토픽이 이미 데이터베이스에 존재합니다.");
		        alert.showAndWait();
		        topicTextField.setText("");
				controlTopicTextField.setText("");
		        return;
			}else {
				for (int i = 0; i < topicList.size(); i++) {
					if(topicList.get(i).equals(newTopic)) {
						Alert alert = new Alert(AlertType.INFORMATION);
				        alert.setTitle("Duplicate Topic.");
				        alert.setHeaderText("");
				        alert.setContentText("토픽이 중복됩니다.");
				        alert.showAndWait();
				        topicTextField.setText("");
						controlTopicTextField.setText("");
				        return;
					}
				}
				topicList.add(newTopic);
				controlTopicList.add(newControl);
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Database isn't Connected.");
	        alert.setHeaderText("");
	        alert.setContentText("데이터베이스에 연결되어있지않습니다.");
	        alert.showAndWait();
	        topicTextField.setText("");
			controlTopicTextField.setText("");
	        return;
		}
		topicTextField.setText("");
		controlTopicTextField.setText("");
	}
	@FXML
	private void handleRemove() {
		int selectedInt = topicListView.getSelectionModel().getSelectedIndex();
		if(selectedInt >= 0) {
			topicList.remove(selectedInt);
			controlTopicList.remove(selectedInt);
		}
	}
	
	@FXML
	private void handleApply() {
		if(tabPane.getSelectionModel().getSelectedItem().getId().equals("databaseTab")) {
			if(urlTextField.getText() == null || urlTextField.getText().length() == 0)
				urlTextField.setText(null);
			else 
				main.connect.getPool().set_url(urlTextField.getText());
			
			if(userTextField.getText() == null || userTextField.getText().length() == 0)
				userTextField.setText(null);
			else 
				main.connect.getPool().set_user(userTextField.getText());
			
			if(passwordTextField.getText() == null || passwordTextField.getText().length() == 0)
				passwordTextField.setText(null);
			else
				main.connect.getPool().set_password(passwordTextField.getText());
			
			// json파일로 저장
			JSONObject obj = new JSONObject();
			obj.put("url", urlTextField.getText());
			obj.put("user", userTextField.getText());
			obj.put("pass", passwordTextField.getText());
			
			try(FileWriter file = new FileWriter("dbData.json")){
				file.write(obj.toJSONString());
				file.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			main.checkDBConnect();
		}else {
			for (int i = 0; i < topicList.size(); i++) {
				if(controlTopicList.get(i).equals("null"))
					controlTopicList.set(i, null);
				if(!main.connect.insertTopic(topicList.get(i), controlTopicList.get(i))) {
					Alert alert = new Alert(AlertType.ERROR);
			        alert.setTitle("ERROR");
			        alert.setHeaderText("");
			        alert.setContentText("데이터베이스에 토픽이 저장되지못했습니다.");
			        alert.showAndWait();
			        topicTextField.setText("");
					controlTopicTextField.setText("");
			        return;
				}
			}
			Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("Insert Topic is Finished.");
	        alert.setHeaderText("");
	        alert.setContentText("데이터베이스에 토픽이 저장되었습니다.");
	        alert.showAndWait();
		}
		
		dialogStage.close();
	}
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}
	
	
	
	public void setMain(Main main) {
		this.main = main;
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

}
