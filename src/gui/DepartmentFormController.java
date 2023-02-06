package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController  implements Initializable {
	
	private Department entity;
	
	private DepartmentService service;
	
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
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
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
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChangedListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Department getFormdata() { // # Função responsavel por capturar os dados do formulario e retornar um novo objeto.
		Department obj = new Department();// # Estancia um Departamento vazio.
		
		obj.setId(Utils.tryParseToInt(txtId.getText())); // # Pega o campo Id do formulario e a funcção Utils.tryParseToInt faz a verificação caso estaja null vai inserir um novo departamento caso contrario faz um Update.
		obj.setName(txtName.getText());
		
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
}
