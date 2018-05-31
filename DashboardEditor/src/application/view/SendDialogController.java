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
    
    // ������
	public SendDialogController() {
		fileVector = new Vector<File>();
	}
	// ���̾�α� �������� ����
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
     * ����ڰ� cancel�� Ŭ���ϸ� ȣ��ȴ�.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
	
	   /**
     * ����ڰ� OK�� Ŭ���ϸ� true��, �� �ܿ��� false�� ��ȯ�Ѵ�.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }
	
	/**
	 * ���� �����ͺ��̽��� json���ϵ��� ������ �޼��� ����
	 */
	@FXML
	private void handleSend() {
		if(!main.isDbConnect) {
			Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("�����ͺ��̽� ���� ����");
            alert.setHeaderText("Please check database setting");
            alert.setContentText("�����ͺ��̽��� ����Ǿ����� �ʽ��ϴ�.");

            alert.showAndWait();
            return;
		} else if(fileVector.size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("���õ� ������ �����ϴ�.");
            alert.setHeaderText("Please select Json File");
            alert.setContentText("������ �������ּ���.");
            
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
    			
    			// �̹� �ִ� ��ú��� ���� �̸� Ȯ���Ͽ� update
    			if(main.connect.isDashBoardExist(version, (String)jsonObject.get("dashboardName"))) {
    				Alert alert = new Alert(AlertType.CONFIRMATION);
    				alert.setTitle("Confirmation Dialog");
    				alert.setHeaderText("�̹� �ִ� ��ú��� ���� �̸��Դϴ�.");
    				alert.setContentText("�����ðڽ��ϱ�?");
    				
    				Optional<ButtonType> result = alert.showAndWait();
    				if(result.get() == ButtonType.OK) {
    					main.connect.updateDashboard(version, fileName.get(i), jsonObject);
    				}else{
    					return;
    				}
    			}else {
    				// ���ο� ��ú��� �����̸� insert
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
        alert.setTitle("Dadabase�� �����Ͽ����ϴ�.");
        alert.setHeaderText("Dadabase�� �����Ͽ����ϴ�.");
        alert.setContentText("Dadabase�� �����Ͽ����ϴ�.");

        alert.showAndWait();
        
        System.out.println("DB�� ���� �Ǿ����ϴ�.");
		okClicked = true;
        dialogStage.close();  
	}
	
	@FXML
	private void handleAddDashBoard() {
		FileChooser fileChooser = new FileChooser();

        // Ȯ���� ���͸� �����Ѵ�.
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        // Save File Dialog�� �����ش�.
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
