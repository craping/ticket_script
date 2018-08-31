package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.swing.filechooser.FileSystemView;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ts.entity.Person;
import ts.win32.KeyHook;
import ts.win32.TicketWin32;

public class Controller implements Initializable {
	
	public static Stage stage;
	
	public final FileChooser importChooser = new FileChooser();
	
	public final FileChooser exportChooser = new FileChooser();
	
	private File lastImportFile;
	
	private File lastExportFile;
	
	private Thread scritpThread;
	
	@FXML
	private TableView<Person> table;

	@FXML
	private Button btn_start;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		importChooser.setTitle("选择表格文件");
		importChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
		importChooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("xls", "*.xls"),
			new FileChooser.ExtensionFilter("xlsx", "*.xlsx")
		);
		
		exportChooser.setTitle("导出表格文件");
		exportChooser.setInitialFileName("result.xls");
		exportChooser.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
		exportChooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("xls", "*.xls"),
			new FileChooser.ExtensionFilter("xlsx", "*.xlsx")
		);
		
		table.setPlaceholder(new Label("将多个文件拖放至此导入"));
		
		TableColumn<Person, StringProperty> col_status = new TableColumn<>("状态");
		col_status.setId("col_status");
		col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
		table.getColumns().add(col_status);
		
		table.getColumns().forEach(col -> {
			col.setCellValueFactory(new PropertyValueFactory<>(col.getId().split("_")[1]));
		});
		
		table.setOnDragOver(event -> {
			if (event.getGestureSource() != table) {
				event.acceptTransferModes(TransferMode.ANY);
			}
		});
		table.setOnDragDropped(event -> {
			Dragboard dragboard = event.getDragboard();
			List<File> files = dragboard.getFiles().stream().filter(file -> {
				String name = file.getName();
				String suffix = name.substring(name.lastIndexOf(".") + 1);
				return suffix.equals("xls") || suffix.equals("xlsx");
			}).collect(Collectors.toList());
			importFiles(files);
		});
		
		new Thread(new KeyHook(new LowLevelKeyboardProc() {
            @Override
            public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
            	if (nCode >= 0) {
                    switch(wParam.intValue()) {
                    case WinUser.WM_KEYUP:
                    case WinUser.WM_KEYDOWN:
                    case WinUser.WM_SYSKEYUP:
                    case WinUser.WM_SYSKEYDOWN:
                        System.err.println("in callback, key=" + info.vkCode);
                        if (info.vkCode == 35) {
                            System.err.println("exit");
                            User32.INSTANCE.UnhookWindowsHookEx(KeyHook.hhk);
                            System.exit(0);
                        }
                        if(info.vkCode == 45) {
                        	Platform.runLater(() -> {
                        		start(null);
            				});
                        }
                    }
                }
                Pointer ptr = info.getPointer();
                long peer = Pointer.nativeValue(ptr);
                return User32.INSTANCE.CallNextHookEx(KeyHook.hhk, nCode, wParam, new LPARAM(peer));
            }
        })).start();
		
		stage.setOnCloseRequest(e -> {
			User32.INSTANCE.UnhookWindowsHookEx(KeyHook.hhk);
			System.exit(0);
		});
	}
	
	public void start(ActionEvent event) {
		if(btn_start.getText().equals("开始")) {
			btn_start.setText("停止");
			
			scritpThread = new Thread(() -> {
				ObservableList<Person> data = table.getItems();
				FilteredList<Person> filterData = data.filtered(person -> !"".equals(person.getId()) && !"".equals(person.getName()) && person.getStatus().equals("未执行"));
				
				for (Person person : filterData) {
					if(Thread.currentThread().isInterrupted())
						break;
					
					person.setStatus(TicketWin32.print(person));
				}
				Platform.runLater(() -> {
					btn_start.setText("开始");
				});
			});
			scritpThread.start();
		} else {
			scritpThread.interrupt();
		}
	}
	
	public void openImportFile(ActionEvent event) {
		if(lastImportFile != null && lastImportFile.isFile())
			importChooser.setInitialDirectory(lastImportFile.getParentFile());
		
		lastImportFile = importChooser.showOpenDialog(stage);
		if(lastImportFile == null)
			return;
		
		System.out.println(lastImportFile.getAbsolutePath());
		
		importFiles(Arrays.asList(lastImportFile));
	}
	
	
	private void importFiles(List<File> files) {
		ObservableList<Person> data = FXCollections.observableArrayList();
		
		files.forEach(file -> {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				Workbook book = WorkbookFactory.create(fis);
				Sheet sheet = book.getSheetAt(0);
				
				sheet.forEach(row ->{
					row.forEach(cell -> {
						cell.setCellType(CellType.STRING);
					});
					
					Person person = new Person();
					person.setName(row.getCell(0).getStringCellValue().trim());
					person.setId(row.getCell(1).getStringCellValue().trim());
					data.add(person);
				});
			} catch (IOException | EncryptedDocumentException | InvalidFormatException e) {
				e.printStackTrace();
			} finally {
				try {
					if(fis != null)
						fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		table.setItems(data);
	}
	
	
	public void openExportFile(ActionEvent event) {
		if(lastExportFile != null && lastExportFile.isFile())
			exportChooser.setInitialDirectory(lastExportFile.getParentFile());
		
		lastExportFile = exportChooser.showSaveDialog(stage);
		if(lastExportFile == null)
			return;
		
		exportFile(lastExportFile);
	}
	
	private void exportFile(File file) {
		String prefix = file.getName().substring(file.getName().lastIndexOf(".")+1);
		ObservableList<Person> data = table.getItems();
		try {
			@SuppressWarnings("resource")
			Workbook book = prefix.equals("xls")?new HSSFWorkbook():new XSSFWorkbook();
			Sheet sheet = book.createSheet("0");
	        Row row = sheet.createRow(0);
	        
	        CellStyle style = book.createCellStyle();
	        style.setVerticalAlignment(VerticalAlignment.CENTER);
	        row.createCell(0).setCellStyle(style);
	        row.createCell(0).setCellValue("身份证");
	 
	        row.createCell(1).setCellStyle(style);
	        row.createCell(1).setCellValue("姓名");
	        
	        row.createCell(2).setCellStyle(style);
	        row.createCell(2).setCellValue("状态");
	        
	        
	        for (int i = 0; i < data.size(); i++) {
	        	Person person = data.get(i);
	        	Row dataRow = sheet.createRow(i+1);
	        	dataRow.createCell(0).setCellStyle(style);
	        	dataRow.createCell(0).setCellValue(person.getId());
		 
	        	dataRow.createCell(1).setCellStyle(style);
	        	dataRow.createCell(1).setCellValue(person.getName());
		        
	        	dataRow.createCell(2).setCellStyle(style);
	        	dataRow.createCell(2).setCellValue(person.getStatus());
			}
	        
	        book.setSheetName(0, "结果");
	        
            FileOutputStream fos = new FileOutputStream(file);
            book.write(fos);
            fos.close();
		} catch (EncryptedDocumentException | IOException e) {
			e.printStackTrace();
		}
	}
}
