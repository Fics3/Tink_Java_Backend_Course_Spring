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

@Entity
@Getter
@Table(name = "repositories")
public class GithubRepositoryEntity {
    @Id
    @Column(name = "repository_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer repositoryId;

    @OneToOne
    @JoinColumn(name = "link_id", referencedColumnName = "link_id")
    private LinkEntity link;

    private Integer subscribersCount;

    public GithubRepositoryEntity(LinkEntity link, Integer subscribersCount) {
        this.link = link;
        this.subscribersCount = subscribersCount;
    }

    public GithubRepositoryEntity() {
    }
}
