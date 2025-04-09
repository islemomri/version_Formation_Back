package com.project.app.service;

import java.util.List;
import java.util.Optional;

import com.project.app.model.Diplome;

public interface IDiplomeService {
	
	public Diplome saveDiplome(Long idType, String libelleTypeDiplome, String libelle);
	public Diplome saveOrAssignDiplome(Long employeId, String libelle, Long typeDiplomeId);
	public Diplome updateDiplome(Long diplomeId, String newLibelle, Long newTypeDiplomeId);
	public void deleteDiplome(Long diplomeId);
	public List<Diplome> getDiplomesByEmploye(Long employeId);
}
