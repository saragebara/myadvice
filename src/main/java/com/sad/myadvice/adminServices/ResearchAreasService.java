package com.sad.myadvice.adminServices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sad.myadvice.adminEntity.AdminUser;
import com.sad.myadvice.adminEntity.ResearchAreas;
import com.sad.myadvice.adminRepo.AdminUserRepository;
import com.sad.myadvice.adminRepo.ResearchRepository;


@Service
public class ResearchAreasService {
    private final  ResearchRepository  ResearchRepository;
    private final AdminUserRepository userRepository; // for linking research areas to users

    public ResearchAreasService( ResearchRepository  ResearchRepository, AdminUserRepository userRepository) {
        this. ResearchRepository =  ResearchRepository;
        this.userRepository = userRepository;
    }

    //get all research areas
    public List<ResearchAreas> getAllResearchAreas() {
        return  ResearchRepository.findAll();
    }
    public ResearchAreas getResearchAreaById(Long id) {
        return  ResearchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Research area not found"));
    }

    //create research area for a user
    public ResearchAreas createResearchArea(Long userId, ResearchAreas researchArea) {
        AdminUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        researchArea.setUser(user);
        return  ResearchRepository.save(researchArea);
    }

    // update research area
    public ResearchAreas updateResearchArea(Long id, ResearchAreas updatedResearchArea) {
        ResearchAreas existing = getResearchAreaById(id);
        existing.setResearchArea(updatedResearchArea.getResearchArea());
        existing.setDescription(updatedResearchArea.getDescription());
        return  ResearchRepository.save(existing);
    }

    //delete research area
    public void deleteResearchArea(Long id) {
        ResearchAreas researchArea = getResearchAreaById(id);
         ResearchRepository.delete(researchArea);
    }

    //get research areas for a user
    public List<ResearchAreas> getResearchAreasByUser(Long userId) {
        AdminUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return  ResearchRepository.findByUser(user);
    }
}