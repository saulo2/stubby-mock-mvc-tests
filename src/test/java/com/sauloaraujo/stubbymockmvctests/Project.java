package com.sauloaraujo.stubbymockmvctests;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.scheduling.config.Task;

@Setter
@Getter
public class Project {
    private Integer id;
    private String name;
    private String description;
    private List<Task> tasks;
}