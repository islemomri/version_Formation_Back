package com.project.app.controller;

import com.project.app.dto.PosteDTO;
import com.project.app.model.Direction;
import com.project.app.model.Poste;
import com.project.app.service.IPosteService;

import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/recrutement/postes")
public class PosteController {

    @Autowired
    private IPosteService posteService;
    
    @Operation(
    	    summary = "Ajouter un poste avec un fichier",
    	    description = "Permet d'ajouter un poste avec les informations associées et un fichier (document Word, PDF, etc.)",
    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	        description = "Détails du poste et fichier associé",
    	        required = true,
    	        content = @Content(
    	            mediaType = "multipart/form-data",
    	            schema = @Schema(implementation = PosteDTO.class) // Utiliser la nouvelle classe DTO
    	        )
    	    )
    	)
    @PostMapping("/ajouter")
    public ResponseEntity<Poste> ajouterPoste(
            @ModelAttribute PosteDTO posteDTO,
            @RequestPart("document") @Schema(type = "string", format = "binary") MultipartFile document) throws IOException {

        // Ajouter le fichier dans le DTO
        posteDTO.setDocument(document);

        Poste poste = posteService.addPosteWithDirections(posteDTO);
        return new ResponseEntity<>(poste, HttpStatus.CREATED);
    }
    
    
    @Operation(
    	    summary = "Mettre à jour un poste avec un fichier",
    	    description = "Permet de mettre à jour un poste avec les informations associées et un fichier (document Word, PDF, etc.)",
    	    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
    	        description = "Détails du poste et fichier associé",
    	        required = true,
    	        content = @Content(
    	            mediaType = "multipart/form-data",
    	            schema = @Schema(implementation = PosteDTO.class) // Utiliser la classe DTO
    	        )
    	    )
    	)
    	@PutMapping("/{id}")
    	public ResponseEntity<Poste> updatePoste(
    	        @PathVariable Long id,
    	        @ModelAttribute PosteDTO posteDTO,
    	        @RequestPart(value = "document", required = false) @Schema(type = "string", format = "binary") MultipartFile document) throws IOException {

    	    // Ajouter le fichier dans le DTO si présent
    	    if (document != null) {
    	        posteDTO.setDocument(document);
    	    }

    	    Poste updatedPoste = posteService.updatePoste(id, posteDTO);
    	    return ResponseEntity.ok(updatedPoste);
    	}
    
    @GetMapping
    public List<Poste> getAllPostes() {
        return posteService.getAllPostes();
    }
    
    @GetMapping("/{id}")
    public Poste getPosteById(@PathVariable("id") Long id) {
        return posteService.getPosteById(id);
    }
 
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePoste(@PathVariable("id") Long id) {
        posteService.deletePoste(id);
        return ResponseEntity.ok("Poste supprimé avec succès !");
    }
    
    @GetMapping("getAllPostesnonArchivés")
    public List<Poste> getAllPostesnonArchivés() {
        return posteService.getAllPostesnonArchivés();
    }

    @PutMapping("/{id}/archiver")
    public Poste archiverPoste(@PathVariable Long id) {
        return posteService.archiverPoste(id);
    }

    @GetMapping("/liste-Postes-archivés")
    public List<Poste> getAllPostesArchivés() {
        return posteService.getAllPostesArchivés();
    }

    @PutMapping("/{id}/desarchiver")
    public Poste desarchiverDirection(@PathVariable Long id) {
        return posteService.desarchiverPoste(id);
    }
    @GetMapping("/{id}/directions")
    public ResponseEntity<Set<Direction>> getDirectionsByPosteId(@PathVariable Long id) {
        return posteService.getDirectionsByPosteId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    
}
