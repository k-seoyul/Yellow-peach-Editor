package application.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import application.Main;
import application.model.Chart;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class StreammingDialogController {
	@FXML private Label titleLabel;
	@FXML private JFXTextField streammingTitle;
	@FXML private JFXTextField streammingUrl;
	@FXML private JFXButton apply;
	@FXML private JFXButton cancel;
	
	private Main main;
	private Stage dialogStage;
	private Chart chart;
	private boolean applyClicked = false;
	
	public void setMain(Main main) {
		this.main = main;
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	// 다이얼로그에서 변경될 차트를 선택한다.
	public void setChart(Chart chart) {
		this.chart = chart;
		if(main.isDbConnect) {
			if(chart.getType().equals("camera")) {
				dialogStage.setTitle("Camera Dialog");
				titleLabel.setText("Camera Title");
			}else {
				dialogStage.setTitle("Video Dialog");
				titleLabel.setText("Video Title");
			}
		} else {
			if(chart.getType().equals("camera")) {
				dialogStage.setTitle("Camera Dialog - Database isn`t setting");
				titleLabel.setText("Camera Title");
			}else {
				dialogStage.setTitle("Video Dialog - Database isn`t setting");
				titleLabel.setText("Video Title");
			}
		}
		
		if(chart.getChartName() != "null")
			streammingTitle.setText(chart.getChartName());
		if(chart.getFirstTopic() != "null")
			streammingUrl.setText(chart.getFirstTopic());
		
		streammingTitle.requestFocus();
	}
		
	/**
     * 사용자가 Apply를 클릭하면 true를, 그 외에는 false를 반환한다.
     *
     * @return
     */
    public boolean isApplyClicked() {
        return applyClicked;
    }
    @FXML
	private void handleApply() {
		if(inInputValid()) {
			chart.setChartName(streammingTitle.getText());
			chart.setFirstTopic(streammingUrl.getText());
			chart.setFirstControl(main.connect.getUrlCtrTopic(streammingUrl.getText()));
			applyClicked = true;
			dialogStage.close();
		}
	}
	
	/**
     * 사용자가 cancel을 클릭하면 호출된다.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    // 사용자의 입력검사
    private boolean inInputValid() {
    	String errorMessage = "";
    	
    	if(streammingTitle.getText() == null || streammingTitle.getText().length() == 0) {
    		errorMessage += "No valid Streamming Title\n";
    	}
    	/*
    	if(chart.getType().equals("camera")) {
    		String ctr = main.connect.getUrlCtrTopic(streammingUrl.getText());
    		if(ctr.length() == 0 || ctr == null) {
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
    	*/
    	
    	BoardLayoutController blc = main.getBoardLayoutController();
    	Tab tab = blc.getTabPane().getSelectionModel().getSelectedItem(); 
    	// 차트 이름이 중복이면 생성하지 않는다.
		for (int j = 0; j < main.getDashBoardHashMap().get(tab.getId()).size(); j++) {
			if(streammingTitle.getText().equals(chart.getChartName()) == false && streammingTitle.getText().equals(main.getDashBoardHashMap().get(tab.getId()).get(j).getChartName())) {
				errorMessage += "이름이 중복됩니다.";
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
	
}
