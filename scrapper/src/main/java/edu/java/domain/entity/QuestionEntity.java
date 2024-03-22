package edu.java.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "questions")
public class QuestionEntity {

    @Id
    @Column(name = "question_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionId;

    @ManyToOne
    @JoinColumn(name = "link_id")
    private LinkEntity link;

    @Column(name = "answer_count", nullable = false)
    private Integer answerCount;

    public QuestionEntity(Integer questionId, LinkEntity link, Integer answerCount) {
        this.questionId = questionId;
        this.link = link;
        this.answerCount = answerCount;
    }

    public QuestionEntity() {

    }
}
