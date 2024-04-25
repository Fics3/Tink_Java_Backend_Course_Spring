package edu.java.domain.repository.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "questions")
@NoArgsConstructor
public class StackoverflowQuestionEntity {

    @Id
    @Column(name = "question_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionId;

    @OneToOne
    @JoinColumn(name = "link_id")
    private LinkEntity link;

    @Column(name = "answer_count", nullable = false)
    private Integer answerCount;

    public StackoverflowQuestionEntity(LinkEntity link, Integer answerCount) {
        this.link = link;
        this.answerCount = answerCount;
    }

}
