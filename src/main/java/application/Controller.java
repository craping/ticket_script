package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.swing.filechooser.FileSystemView;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinUser.LowLevelKeyboardProc;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import ts.entity.Person;
import ts.entity.Ticket;
import ts.entity.Train;
import ts.win32.KeyHook;
import ts.win32.TicketWin32;

public class Controller implements Initializable {
	
	public static Stage stage;
	
	public final FileChooser importChooser = new FileChooser();
	
	public final FileChooser exportChooser = new FileChooser();
	
	private List<Train> trains = new ArrayList<>();
	
	private List<Ticket> tickets = new ArrayList<>();
	
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
			importFiles(files, (User32.INSTANCE.GetAsyncKeyState(User32.VK_CONTROL) & 0x8000) != 0);
		});
		
		new Thread(new KeyHook(new LowLevelKeyboardProc() {
            @Override
            public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) {
            	if (nCode >= 0) {
                    switch(wParam.intValue()) {
//                    case WinUser.WM_KEYUP:
                    case WinUser.WM_KEYDOWN:
//                    case WinUser.WM_SYSKEYUP:
                    case WinUser.WM_SYSKEYDOWN:
//                        System.err.println("in callback, key=" + info.vkCode);
                        if (info.vkCode == 35) {
                            System.err.println("exit");
                            User32.INSTANCE.UnhookWindowsHookEx(KeyHook.hhk);
                            System.exit(0);
                        }
                        if (info.vkCode == 36) {
                        	Platform.runLater(() -> {
	                        	if(stage.isShowing()) {
	                    			stage.hide();
	                    		} else {
	                				stage.show();
	                    		}
                        	});
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
		if(Main.license == null) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("提示");
			alert.setHeaderText("软件未激活");
			alert.showAndWait();
			return;
		}
		if(!AppInfo.sn().equals(Main.license.getSn())) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("提示");
			alert.setHeaderText("证书不匹配");
			alert.showAndWait();
			return;
		}
		if(Main.license.getExpire() != null && Main.license.getExpire().compareTo(new Date()) == -1) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("提示");
			alert.setHeaderText("证书已过期");
			alert.showAndWait();
			return;
		}
		
		if(new Date().compareTo(Main.license.getTime()) == -1) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("提示");
			alert.setHeaderText("系统时间与证书异常");
			alert.showAndWait();
			return;
		}
		
		if(btn_start.getText().equals("开始")) {
			btn_start.setText("停止");
			
			scritpThread = new Thread(() -> {
				loop : while(true) {
					if(Thread.currentThread().isInterrupted())
						break;
					
					int remain = 0;
					for (Train train : trains) {
						FilteredList<Person> undo = train.getPersons().filtered(p -> !"".equals(p.getId()) && !"".equals(p.getName()) && p.getStatus().get(Arrays.toString(train.getNo())).get().equals("未执行"));
						remain += undo.size();
					}
					
					if(remain == 0)
						break;
					
					for (Train train : trains) {
						
						if(Thread.currentThread().isInterrupted())
							break loop;
						
						FilteredList<Person> undo = train.getPersons().filtered(p -> !"".equals(p.getId()) && !"".equals(p.getName()) && p.getStatus().get(Arrays.toString(train.getNo())).get().equals("未执行"));
						if(undo.size() > 0) {
							//取出票总数
							int total = 0;
							//当前取票数
							int pick = 0;
							
							int limit = 5;
							TicketWin32.clear();
							
							for (int i = 0; true; i++) {
								if(Thread.currentThread().isInterrupted())
									break loop;
								
								if(i == 0) {
									total += pick = TicketWin32.search(train);
								} else {
									total += pick = TicketWin32.searchRepeat();
								}
								
								//有取到过票且当前无票 || 取票达到上限
								if((pick == 0 && total != 0)  || total >= (undo.size() < limit?undo.size():limit)) {
									
									TicketWin32.pick();
									int execute = 0;
									
									for (Person person : undo) {
										if(Thread.currentThread().isInterrupted())
											break loop;
										
										if(execute == total)
											break;
										
										if(person.getStatus().get(Arrays.toString(train.getNo())).get().equals("未执行")) {
											String status = "成功";
											person.getStatus().get(Arrays.toString(train.getNo())).set("执行中");
											try {
												Thread.sleep(1000);
											} catch (InterruptedException e) {
//												e.printStackTrace();
												scritpThread.interrupt();
											}
//											String status = TicketWin32.print(person);
											person.getStatus().get(Arrays.toString(train.getNo())).set(status);
											
											if(!status.equals("未执行"))
												execute ++;
										}
									}
									
									total = 0;
									undo = undo.filtered(p -> p.getStatus().get(Arrays.toString(train.getNo())).get().equals("未执行"));
								}
								
								if(undo.size() == 0 || pick == 0) {
									TicketWin32.clear();
									break;
								}
							}
						}
					}
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
		
		importFiles(Arrays.asList(lastImportFile), false);
	}
	
	public void appendImportFile(ActionEvent event) {
		if(lastImportFile != null && lastImportFile.isFile())
			importChooser.setInitialDirectory(lastImportFile.getParentFile());
		
		lastImportFile = importChooser.showOpenDialog(stage);
		if(lastImportFile == null)
			return;
		
		System.out.println(lastImportFile.getAbsolutePath());
		
		importFiles(Arrays.asList(lastImportFile), true);
	}
	
	private void importFiles(List<File> files, boolean append) {
		
		if(!append) {
			vbox.getChildren().clear();
			trains.clear();
			tickets.clear();
			tables.clear();
		}
		
		files.forEach(file -> {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				Workbook book = WorkbookFactory.create(fis);
				Sheet sheet = book.getSheetAt(0);
				
				Ticket ticket = new Ticket();
				Person person = null;
				
				for (Row row : sheet) {
					Cell firstCell = row.getCell(0);
					if(firstCell == null || firstCell.getCellTypeEnum() == CellType.BLANK || firstCell.getCellTypeEnum() == CellType._NONE)
						break;
					
					row.forEach(cell -> {
						cell.setCellType(CellType.STRING);
					});
					
					String firstCellValue = firstCell.getStringCellValue().trim();
					if(Character.isDigit(firstCellValue.charAt(0))) {
						
						if(person != null) {
							ticket = new Ticket();
						}
						
						Train train = new Train();
						train.setDate(firstCellValue.split(","));
						train.setNo(row.getCell(1).getStringCellValue().trim().split(","));
						train.setFrom(row.getCell(2).getStringCellValue().trim());
						train.setTo(row.getCell(3).getStringCellValue().trim());
						train.setSeat(row.getCell(4).getStringCellValue().trim());
						
						trains.add(train);
						ticket.getTrains().add(train);
						person = null;
					} else {
						
						if(person == null) {
							TableView<Person> table = new TableView<>();
							table.setEditable(false);
							
							table.prefWidthProperty().bind(scroll.widthProperty().subtract(4));
							
							TableColumn<Person, String> col_name = new TableColumn<>("姓名");
							col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
							table.getColumns().add(col_name);
							
							TableColumn<Person, String> col_id = new TableColumn<>("证件");
							col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
							table.getColumns().add(col_id);
							
							TableColumn<Person, String> col_type = new TableColumn<>("类型");
							col_type.setCellValueFactory(new PropertyValueFactory<>("type"));
							table.getColumns().add(col_type);
							
							ticket.getTrains().forEach(t -> {
								
								TableColumn<Person, String> col_status = new TableColumn<>("车次"+Arrays.toString(t.getNo()));
								col_status.setCellValueFactory(param -> param.getValue().getStatus().get(Arrays.toString(t.getNo())));
								col_status.setCellFactory(new Callback<TableColumn<Person,String>, TableCell<Person,String>>() {
									@Override
									public TableCell<Person, String> call(TableColumn<Person, String> param) {
										TableCell<Person, String> cell = new TableCell<Person, String>() {
							                @Override 
							                protected void updateItem(String item, boolean empty) {
							                    if (item == getItem()) return;
							                    super.updateItem(item, empty);
							                    
							                    if (item == null) {
							                        super.setText(null);
							                        super.setGraphic(null);
							                    } else {
							                    	if(item.equals("成功")) {
							                    		setStyle("");
							                    		setTextFill(Color.web("#28A745"));
							                    	} else if(item.equals("未执行")) {
							                    		setStyle("");
								                    	setTextFill(Color.web("#3399EA"));
							                    	} else if(item.equals("执行中")) {
							                    		setStyle("-fx-background-color:#28A745");
							                    		setTextFill(Color.web("#FFFFFF"));
							                    		/*double y = getParent().getParent().getParent().getParent().getParent().getParent().getParent().getParent().getBoundsInParent().getMaxY();
							                    		double height = scroll.getContent().getBoundsInLocal().getHeight();
							                    		double diffY = getParent().getBoundsInParent().getMaxY();
							                    		System.out.println(y);
							                    		System.out.println(getParent().getBoundsInParent().getMaxY());
							                    		System.out.println((y-diffY)/height);
							                    		scroll.setVvalue(y/height);*/
							                    	} else {
							                    		setStyle("");
								                    	setTextFill(Color.web("#DC3545"));
							                    	}
							                        super.setText(item);
							                        super.setGraphic(null);
							                    }
							                }
							            };
							            return cell;
									}
								});
								table.getColumns().add(col_status);
							});
							
							table.getColumns().forEach(col -> {
								col.setSortable(false);
							});
							table.setItems(ticket.getPersons());
							tables.add(table);
							
							FlowPane content = new FlowPane();
							content.setAlignment(Pos.CENTER_LEFT);
							content.getChildren().add(table);
							FlowPane.setMargin(table, new Insets(-10));
							
							TitledPane tp = new TitledPane(ticket.toString(), content);
							tp.textProperty().bind(ticket.textProperty());
//							tp.setExpanded(false);
							tp.setAnimated(false);
							vbox.getChildren().add(tp);
							
							ticket.getPersons().addListener((Change<? extends Person> c) -> {
								table.setPrefHeight(24.4 + c.getList().size()*24.4);
							});
							tickets.add(ticket);
						}
						
						person = new Person();
						person.setName(row.getCell(0).getStringCellValue().trim());
						person.setId(row.getCell(1).getStringCellValue().trim());
						person.setType(renderType(person.getId()));
						for (Train t : ticket.getTrains()) {
							StringProperty sp = new SimpleStringProperty("未执行");
							sp.addListener(new ChangeListener<String>() {
								
								private Ticket ticket;
								
								public ChangeListener<String> accept(Ticket ticket) {
					                this.ticket = ticket;
					                return this;
					            }
								
								@Override
								public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
									Platform.runLater(() -> {
										ticket.refresh();
									});
								}
							}.accept(ticket));
							person.getStatus().put(Arrays.toString(t.getNo()), sp);
							t.getPersons().add(person);
						}
						
						ticket.getPersons().add(person);
						ticket.refresh();
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
	        
	        int rownum = 0;
        	for(Ticket ticket : tickets) {
        		
        		for(Train train :ticket.getTrains()) {
        			Row dataRow = sheet.createRow(rownum);
        			dataRow.createCell(0).setCellStyle(style);
    	        	dataRow.createCell(0).setCellValue(StringUtil.join(train.getDate(), ","));
    	        	
    	        	dataRow.createCell(1).setCellStyle(style);
    	        	dataRow.createCell(1).setCellValue(StringUtil.join(train.getNo(), ","));
    	        	
    	        	dataRow.createCell(2).setCellStyle(style);
    	        	dataRow.createCell(2).setCellValue(train.getFrom());
    	        	
    	        	dataRow.createCell(3).setCellStyle(style);
    	        	dataRow.createCell(3).setCellValue(train.getTo());
    	        	
    	        	dataRow.createCell(4).setCellStyle(style);
    	        	dataRow.createCell(4).setCellValue(train.getSeat());
        			rownum ++;
	        	}
        		
        		for(Person person : ticket.getPersons()) {
        			Row dataRow = sheet.createRow(rownum);
        			dataRow.createCell(0).setCellStyle(style);
    	        	dataRow.createCell(0).setCellValue(person.getName());
    	        	
    	        	dataRow.createCell(1).setCellStyle(style);
    	        	dataRow.createCell(1).setCellValue(person.getId());
    	        	
    	        	int n = 2;
    	        	for (String key : person.getStatus().keySet()) {
    	        		StringProperty sp = person.getStatus().get(key);
    	        		dataRow.createCell(n).setCellStyle(style);
        	        	dataRow.createCell(n).setCellValue(key+":"+sp.get());
        	        	n++;
					}
        			rownum ++;
        		}
        	}
        	
        	for (int i = 0; i < 5; i++) {
        		sheet.autoSizeColumn(i);
			}
	        book.setSheetName(0, "结果");
	        
            FileOutputStream fos = new FileOutputStream(file);
            book.write(fos);
            fos.close();
		} catch (EncryptedDocumentException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void about(ActionEvent event) {
		try {
			Parent target = FXMLLoader.load(getClass().getClassLoader().getResource("Abount.fxml"));
			Scene scene = new Scene(target);
			Stage aboutStage = new Stage();
			aboutStage.setScene(scene);
			aboutStage.setResizable(false);
			aboutStage.initOwner(stage);
			aboutStage.initModality(Modality.APPLICATION_MODAL);
			aboutStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String renderType(String id) {
		if(id.matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)"))
			return "身份证";
		if(id.matches("^[a-zA-Z]{5,17}$") || id.matches("^[a-zA-Z0-9]{5,17}$"))
			return "护照";
		if(id.matches("^[HMChmc]{1}([0-9]{10}|[0-9]{8})$"))
			return "港澳通行";
		if(id.matches("^[0-9]{8}$") || id.matches("^[0-9]{10}$"))
			return "台湾通行证";
		return "";
	}
	
	public void test(ActionEvent event) {
		HWND FNWND380 = User32.INSTANCE.FindWindow("FNWND380", null);
		if(FNWND380 != null) {
			int num = TicketWin32.ticketCount(FNWND380);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("票数");
			alert.setHeaderText(num+"张");
			alert.showAndWait();
			System.out.println(num+"张");
		}
		double width = scroll.getContent().getBoundsInLocal().getWidth();
        double height = scroll.getContent().getBoundsInLocal().getHeight();
        System.out.println(width);
        System.out.println(height);
	}
}
