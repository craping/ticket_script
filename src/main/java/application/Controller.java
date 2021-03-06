package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ts.entity.Person;
import ts.entity.Train;
import ts.win32.TicketWin32;

public class Controller implements Initializable {
	
	public static Stage stage;
	
	public final FileChooser importChooser = new FileChooser();
	
	public final FileChooser exportChooser = new FileChooser();
	
	private List<Train> trains = new ArrayList<>();
	
	private List<TableView<Person>> tables = new ArrayList<>();
	
	private File lastImportFile;
	
	private File lastExportFile;
	
	private Thread scritpThread;
	
	@FXML
	private TableView<Person> table;
	
	@FXML
	private VBox vbox;
	
	@FXML
	private ScrollPane scroll;
	
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
		
		/*table.setPlaceholder(new Label("将多个文件拖放至此导入"));
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
		});*/
		
		vbox.setOnDragOver(event -> {
			if (event.getGestureSource() != vbox) {
				event.acceptTransferModes(TransferMode.ANY);
			}
		});
		
		vbox.setOnDragDropped(event -> {
			Dragboard dragboard = event.getDragboard();
			List<File> files = dragboard.getFiles().stream().filter(file -> {
				String name = file.getName();
				String suffix = name.substring(name.lastIndexOf(".") + 1);
				return suffix.equals("xls") || suffix.equals("xlsx");
			}).collect(Collectors.toList());
			importFiles1(files);
		});
	}
	
	public void start(ActionEvent event) {
		if(btn_start.getText().equals("开始")) {
			btn_start.setText("停止");
			
			scritpThread = new Thread(() -> {
				try {
					
					for (Train train : trains) {
						if(Thread.currentThread().isInterrupted())
							break;
						TableView<Person> table = tables.get(trains.indexOf(train));
						
						if(TicketWin32.search(train)) {
							
							FilteredList<Person> filterData = train.getPersons().filtered(person -> !"".equals(person.getId()) && !"".equals(person.getName()) && person.getStatus().equals("未执行"));
							for (Person person : filterData) {
								
								if(Thread.currentThread().isInterrupted())
									break;
								
								Thread.sleep(1000);
								
								person.setStatus("成功");
	//							person.setStatus(TicketWin32.print(person));
							}
						}
					}
				
				} catch (InterruptedException e) {
					e.printStackTrace();
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
	
	/*public void start1(ActionEvent event) {
		if(btn_start.getText().equals("开始")) {
			btn_start.setText("停止");
			
			scritpThread = new Thread(() -> {
				ObservableList<Person> data = table.getItems();
				FilteredList<Person> filterData = data.filtered(person -> !"".equals(person.getId()) && !"".equals(person.getName()) && person.getStatus().equals("未执行"));
				
				for (Person person : filterData) {
					if(Thread.currentThread().isInterrupted())
						break;
					
					person.setStatus(Win32.print(person));
					table.refresh();
				}
				Platform.runLater(() -> {
					btn_start.setText("开始");
					table.refresh();
				});
			});
			scritpThread.start();
		} else {
			scritpThread.interrupt();
		}
	}*/
	
	public void openImportFile(ActionEvent event) {
		if(lastImportFile != null && lastImportFile.isFile())
			importChooser.setInitialDirectory(lastImportFile.getParentFile());
		
		lastImportFile = importChooser.showOpenDialog(stage);
		if(lastImportFile == null)
			return;
		
		System.out.println(lastImportFile.getAbsolutePath());
		
		importFiles1(Arrays.asList(lastImportFile));
	}
	
	private void importFiles1(List<File> files) {
		vbox.getChildren().clear();
		trains.clear();
		tables.clear();
		files.forEach(file -> {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				Workbook book = WorkbookFactory.create(fis);
				Sheet sheet = book.getSheetAt(0);
				
				Train train = null;
				TitledPane tp = null;
				for (Row row : sheet) {
					row.forEach(cell -> {
						cell.setCellType(CellType.STRING);
					});
					String firstCell = row.getCell(0).getStringCellValue().trim();
					if(Character.isDigit(firstCell.charAt(0))) {
						train = new Train();
						train.setDate(firstCell.split(","));
						train.setNo(row.getCell(1).getStringCellValue().trim().split(","));
						train.setFrom(row.getCell(2).getStringCellValue().trim());
						train.setTo(row.getCell(3).getStringCellValue().trim());
						train.setSeat(row.getCell(4).getStringCellValue().trim());
						trains.add(train);
						
						TableView<Person> table = new TableView<>();
						table.setEditable(false);
						table.getStyleClass().add("hide-header");
						table.prefWidthProperty().bind(scroll.widthProperty().subtract(4));
						
						TableColumn<Person, String> col_name = new TableColumn<>("姓名");
						col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
						table.getColumns().add(col_name);
						
						TableColumn<Person, String> col_id = new TableColumn<>("证件");
						col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
						table.getColumns().add(col_id);

						TableColumn<Person, StringProperty> col_status = new TableColumn<>("状态");
						col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
						table.getColumns().add(col_status);
						table.getColumns().forEach(col -> {
							col.setSortable(false);
						});
						table.setItems(train.getPersons());
						tables.add(table);
						
						FlowPane content = new FlowPane();
						content.setAlignment(Pos.CENTER_LEFT);
						content.getChildren().add(table);
						FlowPane.setMargin(table, new Insets(-10));
						
						tp = new TitledPane(train.toString(), content);
						tp.textProperty().bind(train.textProperty());
						tp.setExpanded(false);
						tp.setAnimated(false);
						vbox.getChildren().add(tp);
						
						train.getPersons().addListener((Change<? extends Person> c) -> {
							table.setPrefHeight(c.getList().size()*24.4);
						});
						
					} else {
						Person person = new Person();
						person.setName(row.getCell(0).getStringCellValue().trim());
						person.setId(row.getCell(1).getStringCellValue().trim());
						
						person.statusProperty().addListener(new ChangeListener<String>() {
							
							private Train train;
							
							public ChangeListener<String> accept(Train train) {
				                this.train = train;
				                return this;
				            }
							
							@Override
							public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
								Platform.runLater(() -> {
									train.refresh();
								});
							}
						}.accept(train));
						
//						person.statusProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
//							System.out.println("chage");
//							train.refresh();
//						});
						train.getPersons().add(person);
					}
				}
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
	}
	
	
	/*private void importFiles(List<File> files) {
		ObservableList<Person> data = FXCollections.observableArrayList();
		
		files.forEach(file -> {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				Workbook book = WorkbookFactory.create(fis);
				Sheet sheet = book.getSheetAt(0);
				
				sheet.forEach(row ->{
					final Person person = new Person();
					row.forEach(cell -> {
						if(cell.getCellTypeEnum().equals(CellType.BLANK))
							return;
						
						switch (cell.getColumnIndex()) {
						case 2:
							person.setId(cell.getStringCellValue().trim());
							break;
						case 1:
							person.setName(cell.getStringCellValue().trim());
							break;
						}
					});
					
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
	}*/
	
	
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
	
	public void test(ActionEvent event) {
		/*HWND FNWNS380 = User32.INSTANCE.FindWindow("FNWNS380", "证件信息录入窗口");
		if(FNWNS380 != null) {
			HWND idEdit = User32.INSTANCE.FindWindowEx(FNWNS380, null, "Edit", null);
			while(idEdit != null && !User32.INSTANCE.IsWindowVisible(idEdit)) {
				idEdit = User32.INSTANCE.FindWindowEx(FNWNS380, idEdit, "Edit", null);
			}
			char[] staticText = new char[255];
			Win32.INSTANCE.SendMessage(idEdit, Win32.WM_GETTEXT, 255, staticText);
			System.out.println("id:"+Native.toString(staticText));
			
			HWND nameEdit = User32.INSTANCE.FindWindowEx(FNWNS380, idEdit, "Edit", null);
			while(nameEdit != null && !User32.INSTANCE.IsWindowVisible(nameEdit)) {
				nameEdit = User32.INSTANCE.FindWindowEx(FNWNS380, nameEdit, "Edit", null);
			}
			Win32.INSTANCE.SendMessage(nameEdit, Win32.WM_GETTEXT, 255, staticText);
			System.out.println("name:"+Native.toString(staticText));
		}*/
		/*HWND FNWND380 = User32.INSTANCE.FindWindow("FNWND380", null);
		if(FNWND380 != null) {
			HWND dateEdit = User32.INSTANCE.FindWindowEx(FNWND380, null, "Edit", null);
			while(dateEdit != null) {
				
				if(User32.INSTANCE.IsWindowVisible(dateEdit)) {
					char[] staticText = new char[255];
					Win32.INSTANCE.SendMessage(dateEdit, Win32.WM_GETTEXT, 255, staticText);
					System.out.println("id:"+Native.toString(staticText));
					
					RECT rect = new RECT();
					Win32.INSTANCE.GetClientRect(dateEdit, rect);
					System.out.println(rect);
					System.out.println(rect.toRectangle());
				}
				dateEdit = User32.INSTANCE.FindWindowEx(FNWND380, dateEdit, "Edit", null);
			}
			Win32.INSTANCE.SwitchToThisWindow(FNWND380, true);
			Win32.INSTANCE.keybd_event((byte)User32.VK_MENU, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
			Win32.INSTANCE.keybd_event((byte)78, (byte)0, Win32.KEYEVENTF_KEYDOWN, 0);
			Win32.INSTANCE.keybd_event((byte)User32.VK_MENU, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
			Win32.INSTANCE.keybd_event((byte)78, (byte)0, Win32.KEYEVENTF_KEYUP, 0);
		}*/
	}
}
