package com.project.app.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.app.model.Administrateur;
import com.project.app.model.Directeur;
import com.project.app.model.RH;
import com.project.app.model.Responsable;
import com.project.app.model.Utilisateur;
import com.project.app.repository.UtilisateurRepository;

@Service
public class UtilisateurUserDetailsService implements UserDetailsService {
	
	public final UtilisateurRepository utilisateurRepository;
	
	@Autowired
	public UtilisateurUserDetailsService(UtilisateurRepository utilisateurRepository) {
		this.utilisateurRepository=utilisateurRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    Utilisateur user = utilisateurRepository.findByUsername(username)
	            .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

	    Collection<GrantedAuthority> authorities = new ArrayList<>();
	    if (user instanceof Administrateur) {
	        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
	    } else if (user instanceof Directeur) {
	        authorities.add(new SimpleGrantedAuthority("ROLE_DIRECTEUR"));
	    } else if (user instanceof RH) {
	        authorities.add(new SimpleGrantedAuthority("ROLE_RH"));
	    } else if (user instanceof Responsable) {
	        authorities.add(new SimpleGrantedAuthority("ROLE_RESPONSABLE"));
	    } else {
	        throw new IllegalArgumentException("Type d'utilisateur inconnu");
	    }

	    return new UtilisateurUserDetails(user, authorities);
	}


}
