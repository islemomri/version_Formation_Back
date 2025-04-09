package com.project.app.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.project.app.dto.FormationDto;
import com.project.app.dto.FormationDto_Resultat;
import com.project.app.model.Formation;
import com.project.app.model.ResultatFormation;
import com.project.app.model.SousTypeFormation;
import com.project.app.model.TypeFormation;
import com.project.app.model.formation_employe;
import com.project.app.repository.FormationRepository;
import com.project.app.service.FormationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/formations")
@Tag(name = "Formation API", description = "API pour gérer les formations et les fichiers PDF associés aux employés")
@Validated
public class FormationController {

    private final FormationService formationService;
    @Autowired
    private  FormationRepository formationRepository;
    @Autowired
    public FormationController(FormationService formationService) {
        this.formationService = formationService;
    }
    @Operation(
    	    summary = "Modifier le document PDF d'un employé pour une formation spécifique",
    	    description = "Cet endpoint permet de mettre à jour le fichier PDF associé à un employé pour une formation donnée. " +
    	                  "Le fichier PDF est envoyé en tant que partie d'une requête multipart/form-data.",
    	    responses = {
    	        @ApiResponse(
    	            responseCode = "200",
    	            description = "Document mis à jour avec succès",
    	            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    	        ),
    	        @ApiResponse(
    	            responseCode = "404",
    	            description = "Employé, formation ou entrée non trouvée",
    	            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    	        ),
    	        @ApiResponse(
    	            responseCode = "500",
    	            description = "Erreur interne du serveur",
    	            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    	        )
    	    }
    	)
    	@PutMapping(
    	    value = "/{formationId}/employes/{employeId}/document",
    	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    	)
    	public ResponseEntity<String> modifierDocumentEmployeFormation(
    	    @Parameter(
    	        description = "ID de la formation",
    	        required = true
    	    )
    	    @PathVariable Long formationId,

    	    @Parameter(
    	        description = "ID de l'employé",
    	        required = true
    	       
    	    )
    	    @PathVariable Long employeId,

    	    @Parameter(
    	        description = "Fichier PDF à uploader",
    	        required = true,
    	        content = @Content(schema = @Schema(type = "string", format = "binary"))
    	    )
    	    @RequestPart("fichierPdf") MultipartFile fichierPdf) {

    	    try {
    	        // Appeler le service pour modifier le document
    	        formationService.modifierDocumentEmployeFormation(formationId, employeId, fichierPdf);
    	        return ResponseEntity.ok("Document mis à jour avec succès !");
    	    } catch (EntityNotFoundException e) {
    	        return ResponseEntity.status(404).body(e.getMessage()); // 404 Not Found
    	    } catch (IOException e) {
    	        return ResponseEntity.status(500).body("Erreur lors de la lecture du fichier PDF."); // 500 Internal Server Error
    	    } catch (Exception e) {
    	        return ResponseEntity.status(500).body("Une erreur inattendue s'est produite : " + e.getMessage()); // 500 Internal Server Error
    	    }
    	}
    
    @PostMapping("/{rhId}/creer")
    public ResponseEntity<?> creerFormation(@RequestBody  FormationDto formationDto, @PathVariable("rhId") Long rhId) {
        Formation formationCree = formationService.creerFormation(formationDto, rhId);
        return ResponseEntity.status(HttpStatus.CREATED).body(formationCree);
    }


    @GetMapping("/{rhId}")
    public ResponseEntity<List<Formation>> getFormationsParRH(@PathVariable("rhId") Long rhId) {
        return ResponseEntity.ok(formationService.getFormationsParRH(rhId));
    }
    
    
    @GetMapping("/responsable/{responsableId}")
    public List<Formation> getFormationsByResponsable(@PathVariable Long responsableId) {
        return formationService.getFormationsParResponsable(responsableId);
    }
   
    
    
    
    
    
    @GetMapping("/employe/{employeId}")
    public ResponseEntity<List<Formation>> getFormationsParEmploye(@PathVariable("employeId") Long employeId) {
        List<Formation> formations = formationService.getFormationsParEmploye(employeId);
        return ResponseEntity.ok(formations);
    }
    
    @Operation(
    	    summary = "Ajouter une formation avec un fichier PDF",
    	    description = "Permet d'ajouter une formation avec les informations associées et un fichier PDF.",
    	    responses = {
    	        @ApiResponse(responseCode = "200", description = "Formation ajoutée avec succès"),
    	        @ApiResponse(responseCode = "400", description = "Requête invalide"),
    	        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    	    }
    	)
    	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    	public ResponseEntity<Long> ajouterFormation(
    	        @RequestParam("titre") String titre,
    	        @RequestParam("description") String description,
    	        @RequestParam("typeFormation") String typeFormation,
    	        @RequestParam("sousTypeFormation") String sousTypeFormation,
    	        @RequestParam("dateDebutPrevue") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebutPrevue,
    	        @RequestParam("dateFinPrevue") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFinPrevue,
    	        @RequestParam(value = "responsableEvaluationId", required = false) Long responsableEvaluationId, // Non obligatoire
    	        @RequestParam(value = "responsableEvaluationExterne", required = false) String responsableEvaluationExterne, // Non obligatoire
    	        @RequestParam("employeIds") List<Long> employeIds,
    	        @RequestParam("fichierPdf") MultipartFile fichierPdf,
    	        @RequestParam("organisateurId") Long organisateurId, // Nouveau champ
    	        @RequestParam("titrePoste") String titrePoste) throws IOException { // Nouveau champ

    	    FormationDto formationDto = new FormationDto();
    	    formationDto.setTitre(titre);
    	    formationDto.setDescription(description);
    	    formationDto.setTypeFormation(TypeFormation.valueOf(typeFormation));
    	    formationDto.setSousTypeFormation(SousTypeFormation.valueOf(sousTypeFormation));
    	    formationDto.setDateDebutPrevue(dateDebutPrevue);
    	    formationDto.setDateFinPrevue(dateFinPrevue);
    	    formationDto.setResponsableEvaluationId(responsableEvaluationId);
    	    formationDto.setResponsableEvaluationExterne(responsableEvaluationExterne);
    	    formationDto.setEmployeIds(employeIds);
    	    formationDto.setFichierPdf(fichierPdf);
    	    formationDto.setOrganisateurId(organisateurId); // Ajouter l'organisateur
    	    formationDto.setTitrePoste(titrePoste); // Ajouter le titre du poste

    	    Long formationId = formationService.ajouterFormation(formationDto);
    	    return ResponseEntity.ok(formationId); // Retourner uniquement l'ID de la formation
    	}
    

    @Operation(
    	    summary = "Vérifier et valider une formation",
    	    description = "Cet endpoint vérifie si tous les employés d'une formation ont été évalués. " +
    	                  "Si c'est le cas, le champ 'valide' de la formation est mis à jour à 'true'.",
    	    responses = {
    	        @ApiResponse(
    	            responseCode = "200",
    	            description = "Formation validée avec succès",
    	            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    	        ),
    	        @ApiResponse(
    	            responseCode = "404",
    	            description = "Formation non trouvée",
    	            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    	        ),
    	        @ApiResponse(
    	            responseCode = "400",
    	            description = "Tous les employés n'ont pas été évalués",
    	            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    	        ),
    	        @ApiResponse(
    	            responseCode = "500",
    	            description = "Erreur interne du serveur",
    	            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    	        )
    	    }
    	)
    	@PutMapping("/{formationId}/valider")
    	public ResponseEntity<String> validerFormation(
    	    @Parameter(
    	        description = "ID de la formation",
    	        required = true
    	    )
    	    @PathVariable Long formationId) {

    	    try {
    	        formationService.verifierEtValiderFormation(formationId);
    	        return ResponseEntity.ok("Formation validée avec succès !");
    	    } catch (EntityNotFoundException e) {
    	        return ResponseEntity.status(404).body(e.getMessage()); // 404 Not Found
    	    } catch (IllegalStateException e) {
    	        return ResponseEntity.status(400).body(e.getMessage()); // 400 Bad Request
    	    } catch (Exception e) {
    	        return ResponseEntity.status(500).body("Une erreur inattendue s'est produite : " + e.getMessage()); // 500 Internal Server Error
    	    }
    	}
    
    
    @PutMapping("/{id}/commentaire")
    public ResponseEntity<String> ajouterCommentaire(@PathVariable Long id, @RequestBody String commentaire) {
        System.out.println("Commentaire reçu : " + commentaire);

        Optional<Formation> formationOptional = formationRepository.findById(id);
        if (formationOptional.isPresent()) {
            Formation formation = formationOptional.get();
            formation.setCommentaire(commentaire);
            formation.setCommente(true);
            formationRepository.save(formation);
            return ResponseEntity.ok("Commentaire ajouté avec succès.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
    	    summary = "Modifier une formation",
    	    description = "Permet de modifier une formation existante uniquement si elle n'est pas encore validée (valide = false).",
    	    responses = {
    	        @ApiResponse(responseCode = "200", description = "Formation modifiée avec succès"),
    	        @ApiResponse(responseCode = "400", description = "Requête invalide ou formation déjà validée"),
    	        @ApiResponse(responseCode = "404", description = "Formation non trouvée"),
    	        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    	    }
    	)
    	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    	public ResponseEntity<String> modifierFormation(
    	        @PathVariable Long id,
    	        @RequestParam("titre") String titre,
    	        @RequestParam("description") String description,
    	        @RequestParam("typeFormation") String typeFormation,
    	        @RequestParam("sousTypeFormation") String sousTypeFormation,
    	        @RequestParam("dateDebutPrevue") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebutPrevue,
    	        @RequestParam("dateFinPrevue") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFinPrevue,
    	        @RequestParam(value = "responsableEvaluationId", required = false) Long responsableEvaluationId,
    	        @RequestParam(value = "responsableEvaluationExterne", required = false) String responsableEvaluationExterne,
    	        @RequestParam("employeIds") List<Long> employeIds,
    	        @RequestParam(value = "fichierPdf", required = false) MultipartFile fichierPdf,
    	        @RequestParam("organisateurId") Long organisateurId,
    	        @RequestParam("titrePoste") String titrePoste) throws IOException {

    	    FormationDto formationDto = new FormationDto();
    	    formationDto.setTitre(titre);
    	    formationDto.setDescription(description);
    	    formationDto.setTypeFormation(TypeFormation.valueOf(typeFormation));
    	    formationDto.setSousTypeFormation(SousTypeFormation.valueOf(sousTypeFormation));
    	    formationDto.setDateDebutPrevue(dateDebutPrevue);
    	    formationDto.setDateFinPrevue(dateFinPrevue);
    	    formationDto.setResponsableEvaluationId(responsableEvaluationId);
    	    formationDto.setResponsableEvaluationExterne(responsableEvaluationExterne);
    	    formationDto.setEmployeIds(employeIds);
    	    formationDto.setFichierPdf(fichierPdf);
    	    formationDto.setOrganisateurId(organisateurId);
    	    formationDto.setTitrePoste(titrePoste);

    	    try {
    	        formationService.modifierFormation(id, formationDto);
    	        return ResponseEntity.ok("Formation modifiée avec succès.");
    	    } catch (IllegalArgumentException e) {
    	        return ResponseEntity.badRequest().body(e.getMessage());
    	    } catch (ResourceNotFoundException e) {
    	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    	    }
    	}

    
    @PutMapping("/{formationId}/employes/{employeId}/resultat")
    public ResponseEntity<String> ajouterResultat(
        @PathVariable Long formationId,
        @PathVariable Long employeId,
        @RequestParam String resultat) { // Utilisez String au lieu de ResultatFormation

        try {
            // Convertir manuellement la chaîne en ResultatFormation
            ResultatFormation resultatFormation = ResultatFormation.valueOf(resultat.toUpperCase());

            // Appeler le service avec l'enum
            formationService.ajouterResultat(formationId, employeId, resultatFormation);
            return ResponseEntity.ok("Résultat ajouté avec succès !");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body("Valeur invalide pour le résultat : " + resultat); // 400 Bad Request
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Une erreur inattendue s'est produite : " + e.getMessage()); // 500 Internal Server Error
        }
    }
    
    @Operation(
    	    summary = "Récupérer le résultat d'un employé pour une formation",
    	    description = "Cet endpoint permet de récupérer le résultat d'un employé pour une formation spécifique.",
    	    responses = {
    	        @ApiResponse(
    	            responseCode = "200",
    	            description = "Résultat récupéré avec succès",
    	            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
    	        ),
    	        @ApiResponse(
    	            responseCode = "404",
    	            description = "Aucune entrée trouvée pour cet employé et cette formation",
    	            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    	        ),
    	        @ApiResponse(
    	            responseCode = "500",
    	            description = "Erreur interne du serveur",
    	            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
    	        )
    	    }
    	)
    @GetMapping("/{formationId}/employes/{employeId}/resultat")
    public ResponseEntity<Map<String, Object>> getResultatFormation(
        @PathVariable Long formationId,
        @PathVariable Long employeId) {

        try {
            Map<String, Object> resultatEtRes = formationService.getResultatFormation(formationId, employeId);
            return ResponseEntity.ok(resultatEtRes);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "Une erreur inattendue s'est produite : " + e.getMessage()));
        }
    }
    @Operation(
    	    summary = "Créer une formation avec des résultats initiaux",
    	    description = "Permet de créer une formation avec des résultats pré-définis pour les employés.",
    	    responses = {
    	        @ApiResponse(responseCode = "201", description = "Formation créée avec succès"),
    	        @ApiResponse(responseCode = "400", description = "Requête invalide"),
    	        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    	     }
    	)
    	@PostMapping("/{rhId}/creerAvecResultat")
    	public ResponseEntity<Formation> creerFormationAvecResultat(
    	    @Parameter(description = "DTO contenant les données de la formation et les résultats", required = true)
    	    @Valid @RequestBody FormationDto_Resultat formationDto,
    	    @Parameter(description = "ID du RH créateur", required = true)
    	    @PathVariable("rhId") Long rhId) {
    	    
    	    System.out.println("JSON reçu : " + formationDto);
    	    Formation formation = formationService.creerFormationavecResultat(formationDto, rhId);
    	    return ResponseEntity.status(HttpStatus.CREATED).body(formation);
    	}  
    
    @Operation(
    	    summary = "Modifier une formation avec des résultats",
    	    description = "Permet de modifier une formation existante et ses résultats associés pour les employés.",
    	    responses = {
    	        @ApiResponse(responseCode = "200", description = "Formation modifiée avec succès"),
    	        @ApiResponse(responseCode = "400", description = "Requête invalide"),
    	        @ApiResponse(responseCode = "404", description = "Formation non trouvée"),
    	        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    	    }
    	)
    	@PutMapping("/{rhId}/modifierAvecResultat/{formationId}")
    	public ResponseEntity<Formation> modifierFormationAvecResultat(
    	    @Parameter(description = "DTO contenant les données mises à jour de la formation et les résultats", required = true)
    	    @Valid @RequestBody FormationDto_Resultat formationDto,
    	    @Parameter(description = "ID du RH modificateur", required = true)
    	    @PathVariable("rhId") Long rhId,
    	    @Parameter(description = "ID de la formation à modifier", required = true)
    	    @PathVariable("formationId") Long formationId) {
    	    
    	    Formation formation = formationService.modifierFormationAvecResultat(formationDto, rhId, formationId);
    	    return ResponseEntity.ok(formation);
    	}
    
    
    
    @GetMapping("/employe/{employeId}/details")
    public ResponseEntity< List<formation_employe>> getFormationsWithDetailsByEmploye(
            @PathVariable Long employeId) {
        List<formation_employe> result = formationService.getFormationsWithDetailsByEmploye(employeId);
        return ResponseEntity.ok(result);
    }
    
    
    @PostMapping("/formations/{id}/date-rappel")
    public ResponseEntity<String> ajouterDateRappel(@PathVariable Long id, @RequestBody LocalDate nouvelleDateRappel) {
        Optional<Formation> formationOpt = formationRepository.findById(id);

        if (formationOpt.isPresent()) {
            Formation formation = formationOpt.get();
            
            if (formation.getDateRappel() != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("⚠️ La date de rappel est déjà définie. Utilisez PUT pour la modifier.");
            }

            formation.setDateRappel(nouvelleDateRappel);
            formation.setRappelEnvoye(false);
            formationRepository.save(formation);
            return ResponseEntity.ok("✅ Date de rappel ajoutée avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("❌ Formation non trouvée.");
        }
    }

    @PutMapping("/formations/{id}/date-rappel")
    public ResponseEntity<String> modifierDateRappel(@PathVariable Long id, @RequestBody LocalDate nouvelleDateRappel) {
        Optional<Formation> formationOpt = formationRepository.findById(id);
        
        if (formationOpt.isPresent()) {
            Formation formation = formationOpt.get();
            formation.setDateRappel(nouvelleDateRappel);
            formation.setRappelEnvoye(false);
            formationRepository.save(formation);
            return ResponseEntity.ok("✅ Date de rappel mise à jour avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Formation non trouvée.");
        }
    }
    @GetMapping("/formations/{id}/date-rappel")
    public ResponseEntity<LocalDate> getDateRappel(@PathVariable Long id) {
        Optional<Formation> formationOpt = formationRepository.findById(id);

        if (formationOpt.isPresent()) {
            return ResponseEntity.ok(formationOpt.get().getDateRappel());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    
    
}