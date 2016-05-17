package com.sauloaraujo.stubbymockmvctests;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Task {
    private Integer id;
    private String name;
    private String description;
    private List<Project> projects; 
}