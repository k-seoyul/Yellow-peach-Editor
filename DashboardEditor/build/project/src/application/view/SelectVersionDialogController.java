package application.view;

import com.jfoenix.controls.JFXButton;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class SelectVersionDialogController {
	@FXML private JFXButton fake;
	@FXML private JFXButton pcButton;
	@FXML private JFXButton mobileButton;
	@FXML private ImageView pcImageVI;
	@FXML private ImageView mobileImageVI;
	
	private String tabId;
	private Main main;
	private Stage dialogStage;
	
	@FXML
	private void initialize() {
		pcImageVI.setImage(new Image("file:resources/images/PC.png"));
		mobileImageVI.setImage(new Image("file:resources/images/Mobile.png"));
	}
	
	@FXML
	private void handlePcButton() {
		main.getVersion().put(tabId, "PC");
		dialogStage.close();
	}
	@FXML
	private void handleMobileButton() {
		main.getVersion().put(tabId, "Mobile");
		dialogStage.close();
	}
	
	
	public String getTabId() {
		return tabId;
	}
	public void setTabId(String tabId) {
		this.tabId = tabId;
	}
	public void setMain(Main main) {
		this.main = main;
	}
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public JFXButton getFake() {
		return fake;
	}

	public void setFake(JFXButton fake) {
		this.fake = fake;
	}
	
}
