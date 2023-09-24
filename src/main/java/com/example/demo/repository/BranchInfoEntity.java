package com.example.demo.repository;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branches")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class BranchInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long branchId;
    @Column
    String branchName;
    @Column
    String sha;


    public BranchInfoEntity(String branchName, String sha) {
        this.branchName = branchName;
        this.sha = sha;
    }



}

