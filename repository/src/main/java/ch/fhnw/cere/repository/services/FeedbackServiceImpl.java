package ch.fhnw.cere.repository.services;

import ch.fhnw.cere.repository.models.Feedback;
import ch.fhnw.cere.repository.repositories.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    public List<Feedback> findAll(){
        return feedbackRepository.findAll();
    }

    public Feedback save(Feedback feedback){
        return feedbackRepository.save(feedback);
    }

    public Feedback find(long id){
        return feedbackRepository.findOne(id);
    }

    public void delete(long id){
        feedbackRepository.delete(id);
    }

    public List<Feedback> findByApplicationId(long applicationId) {
        return feedbackRepository.findByApplicationId(applicationId);
    }

    public List<Feedback> findByUserIdentification(String userIdentification) {
        return feedbackRepository.findByUserIdentification(userIdentification);
    }

    @Override
    public long countByUserIdentifictation(String userIdentification) {
        return feedbackRepository.countByUserIdentification(userIdentification);
    }

    public List<Feedback> findByIsPublic(boolean isPublic){
        return feedbackRepository.findByIsPublic(isPublic);
    }
}