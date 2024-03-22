package edu.java.domain.repository.jpa;

import edu.java.domain.entity.LinkEntity;
import edu.java.domain.entity.QuestionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaQuestionRepository extends JpaRepository<QuestionEntity, Integer> {
    QuestionEntity findByLink(LinkEntity link);

    @Modifying
    @Query("UPDATE QuestionEntity e set e.answerCount = :answerCount where e.link.linkId = :linkId")
    void updateAnswerCount(@Param("linkId") UUID linkId, @Param("answerCount") Integer answerCount);

}
