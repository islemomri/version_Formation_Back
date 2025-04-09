package com.project.app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.project.app.dto.FormationDto;
import com.project.app.model.Formation;

import io.jsonwebtoken.io.IOException;

public interface IFormationService {
    Formation creerFormation(FormationDto formationDto, Long rhId);
    List<Formation> getFormationsParRH(Long rhId);

  
}

