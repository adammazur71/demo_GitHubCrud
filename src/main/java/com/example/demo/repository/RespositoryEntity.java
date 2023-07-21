package com.example.demo.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class RespositoryEntity {
    @Id
    Long id;
    @Column(nullable = false)
    String owner;
    @Column(nullable = false)
    String name;

    public RespositoryEntity() {
    }

    @Override
    public String toString() {
        return "RespositoryEntity{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public RespositoryEntity(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
