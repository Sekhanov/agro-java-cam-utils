package ru.skhanov.ipcam.fxcontrollers;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.ipcam.IpCamAuth;
import com.github.sarxos.webcam.ds.ipcam.IpCamDevice;
import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry;
import com.github.sarxos.webcam.ds.ipcam.IpCamDriver;
import com.github.sarxos.webcam.ds.ipcam.IpCamMode;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import ru.skhanov.ipcam.utils.ImageUtils;



public class WebCamPreviewController implements Initializable {
	
	private static final String IMAGE_CAM_NAME = "KPP_holl";
	private static final String IMAGE_CAM_NAME2 = "controllers";
	private static final String USER = "admin";
	private static final String USER2 = "admin";
	private static final String PASSWORD = "";
	private static final String PASSWORD2 = "veterIP";
	private static final String IMAGE_URL = "http://192.168.100.188/dms?nowprofileid=1";
	private static final String IMAGE_URL2 = "http://192.168.100.195/dms?nowprofileid=1";

	@FXML
	private TextField shotNameTextField;
	
	private String shotNameString = "";

	@FXML
	private Button btnStartCamera;

	@FXML
	private Button btnStopCamera;

	@FXML
	private Button btnDisposeCamera;

	@FXML
	private ComboBox<WebCamInfo> cbCameraOptions;

	@FXML
	private VBox vBoxCamPlaceholder;

	@FXML
	private FlowPane fpBottomPane;

	@FXML
	private ImageView imgWebCamCapturedImage;

	private class WebCamInfo {

		private String webCamName;
		private int webCamIndex;

		public String getWebCamName() {
			return webCamName;
		}

		public void setWebCamName(String webCamName) {
			this.webCamName = webCamName;
		}

		public int getWebCamIndex() {
			return webCamIndex;
		}

		public void setWebCamIndex(int webCamIndex) {
			this.webCamIndex = webCamIndex;
		}

		@Override
		public String toString() {
			return webCamName;
		}
	}

	private BufferedImage grabbedImage;
	private Webcam selWebCam = null;
	private boolean stopCamera = false;
	private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<Image>();
	private String cameraListPromptText = "Выберите камеру";

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Webcam.setDriver(new IpCamDriver());
		try {
			IpCamDeviceRegistry.register(new IpCamDevice(IMAGE_CAM_NAME, IMAGE_URL, IpCamMode.PULL, new IpCamAuth(USER, PASSWORD)));
			IpCamDeviceRegistry.register(new IpCamDevice(IMAGE_CAM_NAME2, IMAGE_URL2, IpCamMode.PULL, new IpCamAuth(USER2, PASSWORD2)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		fpBottomPane.setDisable(true);
		ObservableList<WebCamInfo> options = FXCollections.observableArrayList();
		int webCamCounter = 0;
		for (Webcam webcam : Webcam.getWebcams()) {
			WebCamInfo webCamInfo = new WebCamInfo();
			webCamInfo.setWebCamIndex(webCamCounter);
			webCamInfo.setWebCamName(webcam.getName());
			options.add(webCamInfo);
			webCamCounter++;
		}
		cbCameraOptions.setItems(options);
		cbCameraOptions.setPromptText(cameraListPromptText);
		cbCameraOptions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<WebCamInfo>() {

			@Override
			public void changed(ObservableValue<? extends WebCamInfo> arg0, WebCamInfo arg1, WebCamInfo arg2) {
				if (arg2 != null) {
					System.out.println("WebCam Index: " + arg2.getWebCamIndex() + ": WebCam Name:" + arg2.getWebCamName());
					initializeWebCam(arg2.getWebCamIndex());
				}
			}
		});
		shotNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
//		    System.out.println("textfield changed from " + oldValue + " to " + newValue);
			shotNameString = newValue;
		});
	}



	protected void initializeWebCam(final int webCamIndex) {
		Task<Void> webCamIntilizer = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				if (selWebCam == null) {
					selWebCam = Webcam.getWebcams().get(webCamIndex);
					selWebCam.open();
				} else {
					closeCamera();
					selWebCam = Webcam.getWebcams().get(webCamIndex);
					selWebCam.open();
				}
				startWebCamStream();
				return null;
			}
		};

		new Thread(webCamIntilizer).start();
		fpBottomPane.setDisable(false);
		btnStartCamera.setDisable(true);
		btnStopCamera.setDisable(false);
		cbCameraOptions.setDisable(true);
	}

	protected void startWebCamStream() {
		stopCamera = false;
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				while (!stopCamera) {
					try {
						if ((grabbedImage = selWebCam.getImage()) != null) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									final Image mainiamge = SwingFXUtils
										.toFXImage(grabbedImage, null);
									imageProperty.set(mainiamge);
								}
							});
							grabbedImage.flush();

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				while(stopCamera && selWebCam != null) {
					try(FileInputStream fileInputStream = new FileInputStream(new File("123.jpg"));) {
						imageProperty.set(new Image(fileInputStream));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		};
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
		imgWebCamCapturedImage.imageProperty().bind(imageProperty);
	}

	private void closeCamera() {
		if (selWebCam != null) {
			selWebCam.close();
			selWebCam = null;
		}
	}

	public void stopCamera(ActionEvent event) {
		stopCamera = true;
		btnStartCamera.setDisable(false);
		btnStopCamera.setDisable(true);
		ImageUtils.getImageFromCamWithMetadata(selWebCam, shotNameString);
		selWebCam.open();
		shotNameTextField.setText("");
		
	}

	public void startCamera(ActionEvent event) {
		stopCamera = false;
		startWebCamStream();
		btnStartCamera.setDisable(true);
		btnStopCamera.setDisable(false);
	}

	public void disposeCamera(ActionEvent event) {
		stopCamera = true;
		closeCamera();
//		btnStopCamera.setDisable(true);
//		btnStartCamera.setDisable(true);
		cbCameraOptions.setDisable(false);
		fpBottomPane.setDisable(true);
		
	}
}
