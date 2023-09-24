package com.example.demo.repository;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Entity
@Table(name = "repos")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class RepositoryEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String owner;
    @Column(nullable = false)
    String name;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "repository_entity_id")
    Set<BranchInfoEntity> branchInfoEntity;


    public RepositoryEntity(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }


    public RepositoryEntity(String owner, String name, Set<BranchInfoEntity> branchInfoEntity) {
        this.owner = owner;
        this.name = name;
        this.branchInfoEntity = branchInfoEntity;
    }

//    @Override
//    public String toString() {
//        return "RepositoryEntity{" +
//                "id=" + id +
//                ", owner='" + owner + '\'' +
//                ", name='" + name + '\'' +
//                ", branchInfoEntity=" + branchInfoEntity +
//                '}';
//    }

}
