package com.example.demo.repository;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "Entity")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class RepositoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false)
    String owner;
    @Column(nullable = false)
    String name;
    @OneToMany
    List<RepositoryEntity2> repositoryEntity2;


    @Override
    public String toString() {
        return "RepositoryEntity{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", repositoryEntity2=" + repositoryEntity2 +
                '}';
    }

    public RepositoryEntity(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }
}
