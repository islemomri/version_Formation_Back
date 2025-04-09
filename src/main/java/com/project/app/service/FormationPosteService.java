package com.project.app.service;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.app.model.FormationPoste;
import com.project.app.repository.FormationPosteRepository;

import java.util.List;

@Service
public class FormationPosteService {

    @Autowired
    private FormationPosteRepository formationPosteRepository;

    // Ajouter une paire
    public FormationPoste addPair(Long formationId, Long posteId) {
        FormationPoste pair = new FormationPoste(formationId, posteId);
        return formationPosteRepository.save(pair);
    }

    
 // Mettre à jour le posteId d'une formation spécifique
    public FormationPoste updatePosteForFormation(Long formationId, Long newPosteId) {
        // Trouver la paire existante pour la formation donnée
        FormationPoste pair = formationPosteRepository.findByFormationId(formationId)
            .orElseThrow(() -> new ResourceNotFoundException("Paire non trouvée pour la formation avec l'ID : " + formationId));

        // Mettre à jour le posteId
        pair.setPosteId(newPosteId);
        return formationPosteRepository.save(pair);
    }
    
    // Récupérer toutes les paires
    public List<FormationPoste> getAllPairs() {
        return formationPosteRepository.findAll();
    }

    // Supprimer une paire par ID
    public void deletePair(Long id) {
        formationPosteRepository.deleteById(id);
    }

    // Récupérer une paire par ID
    public FormationPoste getPairById(Long id) {
        return formationPosteRepository.findById(id).orElse(null);
    }
    
    
    
    public Long getPosteIdByFormationId(Long formationId) {
        FormationPoste pair = formationPosteRepository.findByFormationId(formationId)
            .orElseThrow(() -> new ResourceNotFoundException("Paire non trouvée pour la formation avec l'ID : " + formationId));
        return pair.getPosteId();
    }
}