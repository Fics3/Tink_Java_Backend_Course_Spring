package edu.java.domain.repository.jpa.entitiesRepository;

import edu.java.domain.repository.jpa.entity.LinkEntity;
import edu.java.domain.repository.jpa.entity.StackoverflowQuestionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaStackoverflowQuestionEntityRepository extends JpaRepository<StackoverflowQuestionEntity, Integer> {
    StackoverflowQuestionEntity findByLink(LinkEntity link);

    @Modifying
    @Query("UPDATE StackoverflowQuestionEntity e set e.answerCount = :answerCount where e.link.linkId = :linkId")
    void updateAnswerCount(@Param("linkId") UUID linkId, @Param("answerCount") Integer answerCount);

}
