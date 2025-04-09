package com.project.app.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.dto.DiplomeRequest;
import com.project.app.model.Diplome;

import com.project.app.service.IDiplomeService;


@RestController
@RequestMapping("/diplomes")
public class DiplomeController {
	
	@Autowired
	private IDiplomeService diplomeService;
	
	@PostMapping("/import")
    public String importDiplomes(@RequestBody List<Map<String, String>> diplomeList) {
        for (Map<String, String> row : diplomeList) {
            Long idType = Long.parseLong(row.get("NRIType_Diplome"));
            String libelleTypeDiplome = row.get("LibelleTypeDiplome");
            String libelle = row.get("Libelle");
            diplomeService.saveDiplome(idType, libelleTypeDiplome, libelle);
        }
        return "Importation terminée avec succès !";
    }
	
	// Ajouter ou associer un diplôme à un employé
	@PostMapping("/add")
	public ResponseEntity<Diplome> addDiplome(@RequestBody DiplomeRequest request) {
	    System.out.println("Requête reçue : employeId=" + request.getEmployeId() +
	                       ", libelle=" + request.getLibelle() + 
	                       ", typeDiplomeId=" + request.getTypeDiplomeId());
	    Diplome diplome = diplomeService.saveOrAssignDiplome(
	        request.getEmployeId(), 
	        request.getLibelle(), 
	        request.getTypeDiplomeId()
	    );
	    return ResponseEntity.ok(diplome);
	}


    @GetMapping("/employe/{employeId}")
    public ResponseEntity<List<Diplome>> getDiplomesByEmploye(@PathVariable("employeId") Long employeId) {
        List<Diplome> diplomes = diplomeService.getDiplomesByEmploye(employeId);

        
        diplomes.forEach(d -> System.out.println("Diplôme: " + d.getLibelle() + ", Type: " + 
            (d.getTypeDiplome() != null ? d.getTypeDiplome().getLibelleTypeDiplome() : "null")));

        return ResponseEntity.ok(diplomes);
    }


    @PutMapping("/update/{diplomeId}")
    public ResponseEntity<Diplome> updateDiplome(@PathVariable("diplomeId") Long diplomeId, @RequestBody DiplomeRequest request) {
        Diplome updatedDiplome = diplomeService.updateDiplome(diplomeId, request.getLibelle(), request.getTypeDiplomeId());
        return ResponseEntity.ok(updatedDiplome);
    }

    @DeleteMapping("/delete/{diplomeId}")
    public ResponseEntity<String> deleteDiplome(@PathVariable("diplomeId") Long diplomeId) {
        diplomeService.deleteDiplome(diplomeId);
        return ResponseEntity.ok("Diplôme supprimé avec succès !");
    }


}
