package com.example.mall.auth.model.dto;

import lombok.Data;

@Data
public class Plan {
    private String name;
    private int space;
    private int collaborators;
    private int private_repos;
}
