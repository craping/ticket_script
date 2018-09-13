package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileSystemView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ts.entity.License;

public class AboutController implements Initializable {
	
	public static Stage stage;
	
	public final FileChooser importChooser = new FileChooser();
	
	private File lastImportFile;
	
	@FXML
	public TextField txt_sn;
	
	@FXML
	public Label lab_license;
	
	@FXML
	public Label lab_info;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		txt_sn.setText(AppInfo.sn());
		lab_info.setText(AppInfo.bit+"位  版本 "+AppInfo.verson);
		renderLicense();
		
		importChooser.setTitle("选择许可文件");
		importChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
		
	}
	
	public void importLicense(ActionEvent event) {
		if(lastImportFile != null && lastImportFile.isFile())
			importChooser.setInitialDirectory(lastImportFile.getParentFile());
		
		lastImportFile = importChooser.showOpenDialog(stage);
		if(lastImportFile == null)
			return;
		
		License license = AppInfo.getLicense(lastImportFile);
		if(license == null) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("提示");
			alert.setHeaderText("证书文件不可用");
			alert.showAndWait();
			return;
		}
		Main.license = license;
		try {
			Files.copy(lastImportFile.toPath(), new File(System.getProperty("user.dir")+"/License").toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderLicense();
	}
	
	private void renderLicense() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if(Main.license == null)
			lab_license.setText("未激活");
		else if(!AppInfo.sn().equals(Main.license.getSn()))
			lab_license.setText("证书不匹配");
		else if(Main.license.getExpire() == null)
			lab_license.setText("已激活");
		else if(Main.license.getExpire().compareTo(new Date()) == -1)
			lab_license.setText("已过期");
		else
			lab_license.setText("到期时间："+format.format(Main.license.getExpire()));
	}
}
