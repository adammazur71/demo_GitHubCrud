package com.example.demo.repository;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branches_info")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class RepositoryEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column
    Long projectId;
    @Column
    String repoName;
    @Column
    String sha;
    @ManyToOne
    @JoinColumn(name = "project_id", insertable=false, updatable=false)
    RepositoryEntity repositoryEntity;

}
