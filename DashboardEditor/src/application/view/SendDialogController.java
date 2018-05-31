package application.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Vector;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SendDialogController {
	@FXML private ListView<String> dashBoardList;
	@FXML private JFXButton addDashBoard;
	@FXML private JFXButton send;
	@FXML private JFXButton cancle;
	
	private Main main;
	
    private Stage dialogStage;
    private boolean okClicked = false;
    private String version;
    
    private Vector<File> fileVector;
    private ObservableList<String> fileName = FXCollections.observableArrayList();
    
    // 생성자
	public SendDialogController() {
		fileVector = new Vector<File>();
	}
	// 다이얼로그 스테이지 설정
	public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
	
	@FXML
	private void initialize() {
		
	}
	
	public void setMain(Main main) {
		this.main = main;
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
    public boolean isOkClicked() {
        return okClicked;
    }
	
	/**
	 * 서버 데이터베이스로 json파일들을 보내는 메서드 구현
	 */
	@FXML
	private void handleSend() {
		if(!main.isDbConnect) {
			Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("데이터베이스 연결 오류");
            alert.setHeaderText("Please check database setting");
            alert.setContentText("데이터베이스가 연결되어있지 않습니다.");

            alert.showAndWait();
            return;
		} else if(fileVector.size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("선택된 파일이 없습니다.");
            alert.setHeaderText("Please select Json File");
            alert.setContentText("파일을 선택해주세요.");
            
            alert.showAndWait();
            return;
		}
		 
		// json parser
        JSONParser parser = new JSONParser();
        try {
        	for (int i = 0; i < fileVector.size(); i++) {
        		Object obj = parser.parse(new FileReader(fileVector.get(i).getAbsolutePath()));
    			JSONObject jsonObject = (JSONObject) obj;
    			
    			System.out.println(version + ", "+(String)jsonObject.get("dashboardName"));
    			
    			// 이미 있는 대시보드 파일 이면 확인하여 update
    			if(main.connect.isDashBoardExist(version, (String)jsonObject.get("dashboardName"))) {
    				Alert alert = new Alert(AlertType.CONFIRMATION);
    				alert.setTitle("Confirmation Dialog");
    				alert.setHeaderText("이미 있는 대시보드 파일 이름입니다.");
    				alert.setContentText("덮어씌우시겠습니까?");
    				
    				Optional<ButtonType> result = alert.showAndWait();
    				if(result.get() == ButtonType.OK) {
    					main.connect.updateDashboard(version, fileName.get(i), jsonObject);
    				}else{
    					return;
    				}
    			}else {
    				// 새로운 대시보드 파일이면 insert
    				main.connect.insertDashboard(version, fileName.get(i), jsonObject);
    			}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Dadabase에 저장하였습니다.");
        alert.setHeaderText("Dadabase에 저장하였습니다.");
        alert.setContentText("Dadabase에 저장하였습니다.");

        alert.showAndWait();
        
        System.out.println("DB에 저장 되었습니다.");
		okClicked = true;
        dialogStage.close();  
	}
	
	@FXML
	private void handleAddDashBoard() {
		FileChooser fileChooser = new FileChooser();

        // 확장자 필터를 설정한다.
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        // Save File Dialog를 보여준다.
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            fileVector.add(file);
            fileName.add(file.getName().substring(0, file.getName().lastIndexOf('.')));
            dashBoardList.setItems(fileName);
            
        }
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	
}
