package com.project.app.service;

import com.project.app.model.Diplome;
import com.project.app.model.Employe;
import com.project.app.model.TypeDiplome;
import com.project.app.repository.DiplomeRepository;
import com.project.app.repository.EmployeRepository;
import com.project.app.repository.TypeDiplomeRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiplomeService implements IDiplomeService{
    
	@Autowired
    private DiplomeRepository diplomeRepository;

    @Autowired
    private TypeDiplomeRepository typeDiplomeRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Transactional
    public Diplome saveOrAssignDiplome(Long employeId, String libelle, Long typeDiplomeId) {
        Employe employe = employeRepository.findById(employeId)
            .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        TypeDiplome typeDiplome = typeDiplomeRepository.findById(typeDiplomeId)
            .orElseThrow(() -> new RuntimeException("Type de diplôme non trouvé"));

        System.out.println("Type de diplôme trouvé : " + typeDiplome.getLibelleTypeDiplome()); // Ajoutez ce log

        Optional<Diplome> existingDiplome = diplomeRepository.findByLibelleAndTypeDiplome(libelle, typeDiplome);

        if (existingDiplome.isPresent()) {
            Diplome diplome = existingDiplome.get();
            diplome.setEmploye(employe);
            diplome.setLibelle(libelle);
            return diplomeRepository.save(diplome);
        } else {
            Diplome newDiplome = new Diplome();
            newDiplome.setLibelle(libelle);
            newDiplome.setEmploye(employe);
            newDiplome.setTypeDiplome(typeDiplome);
            return diplomeRepository.save(newDiplome);
        }
    }

    public List<Diplome> getDiplomesByEmploye(Long employeId) {
    	return diplomeRepository.findByEmployeId(employeId).stream()
    			.map(diplome -> {
    			    System.out.println("Diplôme récupéré : " + diplome.getLibelle() + ", Type: " + diplome.getTypeDiplome());
    			    return diplome;
    			}).collect(Collectors.toList());

    }

    @Transactional
    public Diplome updateDiplome(Long diplomeId, String newLibelle, Long newTypeDiplomeId) {
        Diplome diplome = diplomeRepository.findById(diplomeId)
            .orElseThrow(() -> new RuntimeException("Diplôme non trouvé"));

        TypeDiplome typeDiplome = typeDiplomeRepository.findById(newTypeDiplomeId)
            .orElseThrow(() -> new RuntimeException("Type de diplôme non trouvé"));

        diplome.setLibelle(newLibelle);
        diplome.setTypeDiplome(typeDiplome);

        return diplomeRepository.save(diplome);
    }

    @Transactional
    public void deleteDiplome(Long diplomeId) {
        diplomeRepository.deleteById(diplomeId);
    }
	@Override
	public Diplome saveDiplome(Long idType, String libelleTypeDiplome, String libelle) {
		// TODO Auto-generated method stub
		return null;
	}

}
