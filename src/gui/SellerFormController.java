package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController  implements Initializable {
	
	private Seller entity;
	
	private SellerService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>(); // # Cria uma lista para adicionar objetos para ser escutados e depois ser feito alguma tratamento.
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button btSave;
	
	@FXML
	private Button btCancel;
	
	public void setSeller(Seller entity) {
		this.entity = entity;
	}
	
	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) { // # faz a adição na lista os objetos a serem tratados pela função DataChangeListener. Obs: os objetos que forem usar a função tem que implementar a interface DataChangeListener.
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		if (service == null) {
			throw new IllegalStateException("service was null");
		}
		
		try {
			entity = getFormdata();
			service.saveOrUpdate(entity);
			notifyDataChangedListeners();
			Utils.currentStage(event).close();// # Fecha o Modal ao clicar em save.
		} 
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangedListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormdata() { // # Função responsavel por capturar os dados do formulario e retornar um novo objeto.
		Seller obj = new Seller();// # Estancia um Departamento vazio.
		
		ValidationException exception = new ValidationException("Validation error");// # Faz instansiação da classe ValidationException para ser usada.
		
		obj.setId(Utils.tryParseToInt(txtId.getText())); // # Pega o campo Id do formulario e a funcção Utils.tryParseToInt faz a verificação caso estaja null vai inserir um novo departamento caso contrario faz um Update.
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", " Field can't be empty"); // # Lança uma exceção caso o capmo name não for preenchido.
		}
		obj.setName(txtName.getText());
		
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();// # Fecha o Modal ao clicar em cancel.
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
	public void updateFormData() { // # Responsavel por popular os campos do form.
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId())); // # Pega o value do campo Id e converte em o Id de integer para String.
		txtName.setText(entity.getName());// # Pega o value do campo Name.
	}
	
	private void setErrorMessages(Map<String, String> errors) { // # metodo responsavel por setar a menssagem de erro.
		Set<String> fields = errors.keySet(); // # Recupera o nome dos campos com erro.
		
		// # verifica qual o campo que lançou o erro e seta a mensagem de erro.
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
}
