package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable {

	@FXML 
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML 
	private MenuItem menuItemAbout;

	@FXML 
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}
	
	@FXML 
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.SetDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}
	
	@FXML 
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}
	
	@Override
	public void initialize(URL uri, ResourceBundle rb) {
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) { // # Asegura que a aplicação rode sem sofre alguma interrupção pelo multithreds.
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene();// # Cria uma referencia para a Scene principal que esta em Main.java.
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();// # Cria uma referencia para a VBox.
			
			Node mainMenu = mainVBox.getChildren().get(0);// # Cria uma referencia para o primeiro filho do VBox que é o mainMenu da tela principal.
			mainVBox.getChildren().clear();// # Limpa todos os filhos do VBox da tela principal.
			mainVBox.getChildren().add(mainMenu);// # Adiciona mainMenu da tela principal de volta.
			mainVBox.getChildren().addAll(newVBox.getChildren());// # Adiciona o newVBox e os seus filhos da tela about.
			
			T controller = loader.getController();// # Ativa a função passada no segundo argumento da chamada da função loadView. E o getController vai retornar o controller do tipo que chamar, que nesse caso é foi o DepartmentListController. 
			initializingAction.accept(controller); // # Executa a função passada no segundo argumento da chamada da função onMenuItemDepartmentAction. 
		}
		catch (IOException e) {
			Alerts.showAlert("IOEexception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
