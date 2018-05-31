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
	
	// ���̾�α׿��� ����� ��Ʈ�� �����Ѵ�.
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
     * ����ڰ� Apply�� Ŭ���ϸ� true��, �� �ܿ��� false�� ��ȯ�Ѵ�.
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
     * ����ڰ� cancel�� Ŭ���ϸ� ȣ��ȴ�.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    // ������� �Է°˻�
    private boolean inInputValid() {
    	String errorMessage = "";
    	
    	if(streammingTitle.getText() == null || streammingTitle.getText().length() == 0) {
    		errorMessage += "No valid Streamming Title\n";
    	}
    	/*
    	if(chart.getType().equals("camera")) {
    		String ctr = main.connect.getUrlCtrTopic(streammingUrl.getText());
    		if(ctr.length() == 0 || ctr == null) {
    			 // ���� �޽����� �����ش�.
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
    	// ��Ʈ �̸��� �ߺ��̸� �������� �ʴ´�.
		for (int j = 0; j < main.getDashBoardHashMap().get(tab.getId()).size(); j++) {
			if(streammingTitle.getText().equals(chart.getChartName()) == false && streammingTitle.getText().equals(main.getDashBoardHashMap().get(tab.getId()).get(j).getChartName())) {
				errorMessage += "�̸��� �ߺ��˴ϴ�.";
			}
		}
    	
    	if(errorMessage.length() == 0) {
    		return true;
    	}else {
    		 // ���� �޽����� �����ش�.
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
