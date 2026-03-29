package com.sad.myadvice.administering.service;

import com.sad.myadvice.entity.ResearchAreas;
import com.sad.myadvice.entity.User;
import com.sad.myadvice.repository.ResearchRepository;
import com.sad.myadvice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Uses the shared User entity instead of AdminUser — same users table.
 * Only faculty/staff users should be passed to research area methods.
 */
@Service
public class ResearchAreasService {
    private final ResearchRepository researchRepository;
    private final UserRepository userRepository;
    public ResearchAreasService(ResearchRepository researchRepository,
                                UserRepository userRepository) {
        this.researchRepository = researchRepository;
        this.userRepository = userRepository;
    }

    public List<ResearchAreas> getAllResearchAreas() {
        return researchRepository.findAll();
    }

    public ResearchAreas getResearchAreaById(Long id) {
        return researchRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Research area not found: " + id));
    }

    public ResearchAreas createResearchArea(Long userId, ResearchAreas researchArea) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        researchArea.setUser(user);
        return researchRepository.save(researchArea);
    }

    public ResearchAreas updateResearchArea(Long id, ResearchAreas updated) {
        ResearchAreas existing = getResearchAreaById(id);
        existing.setResearchArea(updated.getResearchArea());
        existing.setDescription(updated.getDescription());
        return researchRepository.save(existing);
    }

    public void deleteResearchArea(Long id) {
        researchRepository.deleteById(id);
    }

    public List<ResearchAreas> getResearchAreasByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        return researchRepository.findByUser(user);
    }
}