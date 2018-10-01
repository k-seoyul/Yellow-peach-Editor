package application.view;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jfoenix.controls.JFXButton;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LoadDialogController {
	@FXML private ListView<String> dashboardListView;
	@FXML private ListView<String> selectedListView;
	@FXML private ImageView addIV;
	@FXML private ImageView removeIV;
	@FXML private JFXButton change;
	@FXML private Label versionLabel;
	
	private Main main;
	
    private Stage dialogStage;
    private boolean okClicked = false;
    
    private String version;
    
    private Vector<JSONObject> dashboardVector;
    private ObservableList<String> nameList;
    private ObservableList<String> selectedList;
    
	// 다이얼로그 스테이지 설정
	public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
	
	@FXML
	private void initialize() {
		version = "PC";
		versionLabel.setText(version);
		nameList = FXCollections.observableArrayList();
		selectedList = FXCollections.observableArrayList();
		
		//addIV = new ImageView(new Image("file:resources/images/arr.png"));
		//removeIV = new ImageView(new Image("file:resources/images/arr2.png"));
		addIV.setImage(new Image("file:resources/images/arr.png"));
		removeIV.setImage(new Image("file:resources/images/arr2.png"));
		
		if(main.isDbConnect) {
			dashboardVector = main.connect.getDashboard(version);
			for (int i = 0; i < dashboardVector.size(); i++) {
				JSONObject obj = dashboardVector.get(i);
				nameList.add((String)obj.get("dashboardName"));
			}		
		}
		
		dashboardListView.setItems(nameList);
		selectedListView.setItems(selectedList);
	
	}
	
	public void setMain(Main main) {
		this.main = main;
	}
    
	@FXML
	private void handleAdd() {
		String selected = dashboardListView.getSelectionModel().getSelectedItem();
		if(selected != null) {
			selectedListView.getSelectionModel().clearSelection();
			nameList.remove(selected);
			selectedList.add(selected);
		}
	}
	
	@FXML
	private void handleRemove() {
		String selected = selectedListView.getSelectionModel().getSelectedItem();
		if(selected != null) {
			dashboardListView.getSelectionModel().clearSelection();
			selectedList.remove(selected);
			nameList.add(selected);
		}
	}
	
	@FXML
	private void handleChange() {
		if(version.equals("PC")) 
			version = "Mobile";
		else 
			version = "PC";
		
		versionLabel.setText(version);
		nameList = FXCollections.observableArrayList();
		selectedList = FXCollections.observableArrayList();
		
		if(main.isDbConnect) {
			dashboardVector = main.connect.getDashboard(version);
			for (int i = 0; i < dashboardVector.size(); i++) {
				JSONObject obj = dashboardVector.get(i);
				nameList.add((String)obj.get("dashboardName"));
			}
		}
		dashboardListView.setItems(nameList);
		selectedListView.setItems(selectedList);
	}
	
	@FXML
	private void handleLoad() {
		if(!main.isDbConnect) {
			Alert alert = new Alert(AlertType.ERROR);
	        alert.setTitle("Database not Connected.");
	        alert.setHeaderText("");
	        alert.setContentText("데이터베이스에 연결되어 있지 않습니다.");
	        alert.showAndWait();
	        return;

		}
		System.out.println("download start");
		for (int i = 0; i < dashboardVector.size(); i++) {
			JSONObject obj = dashboardVector.get(i);
			for (int j = 0; j < selectedList.size(); j++) {
				String name = (String)obj.get("dashboardName");
				if(name.equals(selectedList.get(j))) {
					TextInputDialog dialog = new TextInputDialog("");
					dialog.setTitle("File Name Dialog");
					dialog.setHeaderText(name+"의 대시보드파일 이름을 고치시겠습니까? 아니면 취소를 누르세요");
					dialog.setContentText("New Dashboard Name:");

					// Traditional way to get the response value.
					Optional<String> result = dialog.showAndWait();
					
					if (result.isPresent()){	// 바꾸는 코드
						name = result.get();
						JSONObject tempobj = new JSONObject();
						tempobj.put("dashboardName", name);
								
						// insert "content":[]
						JSONArray jsonArray = (JSONArray)obj.get("content");
						tempobj.put("content", jsonArray);
						
						obj = tempobj;
					}
					String home = System.getProperty("user.home");
					String path = home.concat("/Downloads/").concat(name).concat(".json");
					try(FileWriter fileWrite = new FileWriter(path)){
						fileWrite.write(obj.toJSONString());
						fileWrite.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
						
			}
			
		}
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Save Finished.");
        alert.setHeaderText("");
        alert.setContentText("저장되었습니다.");

        alert.showAndWait();
        dialogStage.close();
	}
	
	
    /**
     * 사용자가 cancel을 클릭하면 호출된다.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
	   /**
     * 사용자가 OK를 클릭하면 true를, 그 외에는 false를 반환한다.
     *
     * @return
     */
    public boolean isApplyClicked() {
        return okClicked;
    }
    
}
