package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService service;

	private DepartmentService departmentService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>(); // # Cria uma lista para adicionar objetos
																				// para ser escutados e depois ser feito
																				// alguma tratamento.

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Department> obsList;

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService service, DepartmentService departmentService) {
		this.service = service;
		this.departmentService = departmentService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) { // # faz a adição na lista os objetos a serem
																			// tratados pela função DataChangeListener.
																			// Obs: os objetos que forem usar a função
																			// tem que implementar a interface
																			// DataChangeListener.
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
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangedListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormdata() { // # Função responsavel por capturar os dados do formulario e retornar um novo
									// objeto.
		Seller obj = new Seller();// # Estancia um Departamento vazio.

		ValidationException exception = new ValidationException("Validation error");// # Faz instansiação da classe
																					// ValidationException para ser
																					// usada.

		obj.setId(Utils.tryParseToInt(txtId.getText())); // # Pega o campo Id do formulario e a funcção
															// Utils.tryParseToInt faz a verificação caso estaja null
															// vai inserir um novo departamento caso contrario faz um
															// Update.
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", " Field can't be empty"); // # Lança uma exceção caso o capmo name não for
																	// preenchido.
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
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
	}

	public void updateFormData() { // # Responsavel por popular os campos do form.
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId())); // # Seta o value do campo Id e converte em o Id de integer para
														// String.
		txtName.setText(entity.getName());// # Seta o value do campo Name.
		txtEmail.setText(entity.getEmail()); // # Seta o value do campo email.
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary())); // # Seta o value do campo BaseSalary.
		if (entity.getBirthDate() != null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault())); // # Seta o campo Birth Date pegando a data a partir da maquina do usuario.
		}
		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();// #Seta o primeiro elemento do comboBox como Default para não gerar um erro quando abrir a janela para adicionar um novo vendedor.
		}
		else {
			comboBoxDepartment.setValue(entity.getDepartment());// #Carrega o departamento associado ao vendedor selecionado para uma possivel edição.
		}
	}

	// # Metodo responsavel por buscar no banco e carregar os departamentos.
	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");// # Lança uma exception caso o campo
																			// DepartmentService estiver null;
		}
		List<Department> list = departmentService.findAll();// # Carrega os dados que estão no banco de dados.
		obsList = FXCollections.observableArrayList(list);// # Carrega os dados na lista obsList criada no começo dessa
															// pagina.
		comboBoxDepartment.setItems(obsList);// # Seta a lista e faz a associação ao ComboBox de departamentos.
	}

	private void setErrorMessages(Map<String, String> errors) { // # metodo responsavel por setar a menssagem de erro.
		Set<String> fields = errors.keySet(); // # Recupera o nome dos campos com erro.

		// # verifica qual o campo que lançou o erro e seta a mensagem de erro.
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
	// # Metodo para inicializar o comboBox. 
	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
}
