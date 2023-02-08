package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {

	
	private SellerDao dao = DaoFactory.createSellerDao();// # Faz a chamada do metodo SellerDao. Injeção de dependencia.
	
	public List<Seller> findAll() {
		return dao.findAll();
	}
	
	// # Função responsavel por inserir um novo departamento ou atualizar um vendedor já existente.
	public void saveOrUpdate(Seller obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	// # Metodo para remover um vendedor
	public void remove(Seller obj) {
		dao.deleteById(obj.getId());
	}
}
