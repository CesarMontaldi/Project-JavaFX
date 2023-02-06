package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {

	
	private DepartmentDao dao = DaoFactory.createDepartmentDao();// # Faz a chamada do metodo DepartmentDao. Inje��o de dependencia.
	
	public List<Department> findAll() {
		return dao.findAll();
	}
	
	// # Fun��o responsavel por inserir um novo departamento ou atualizar um departamento j� existente.
	public void saveOrUpdate(Department obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
}
