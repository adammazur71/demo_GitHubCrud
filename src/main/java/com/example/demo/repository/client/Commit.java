package com.example.demo.repository.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


public record Commit(String sha) {
}
