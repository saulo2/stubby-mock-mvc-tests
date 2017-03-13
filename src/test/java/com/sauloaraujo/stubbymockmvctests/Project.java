package com.sauloaraujo.stubbymockmvctests;

import java.util.List;

import org.springframework.scheduling.config.Task;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Project {
    private Integer id;
    private String name;
    private String description;
    private List<Task> tasks;
}