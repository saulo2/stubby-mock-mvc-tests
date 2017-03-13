package com.sauloaraujo.stubbymockmvctests;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project")
public class ProjectController {
    @RequestMapping(method = RequestMethod.GET, value = "/search")
    public ProjectSearchResource search(
                @RequestParam(required=false, name="name") String name,
                @RequestParam(required=false, name="description") String description,
                @RequestParam(required=false, name="task") List<Integer> tasks,
                @RequestParam(required=false, name="page") Integer page,
                @RequestParam(required=false, name="size") Integer size) {
        ProjectSearchResource resource = new ProjectSearchResource();

        resource.add(linkTo(methodOn(getClass()).search(name, description, tasks, page, size)).withSelfRel().expand());

        return resource;
    }
}