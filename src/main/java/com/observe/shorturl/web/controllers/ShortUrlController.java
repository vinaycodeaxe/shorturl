package com.observe.shorturl.web.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class ShortUrlController {


    @PostMapping("/createURL")
    public ResponseEntity<String> getShortenedURL() {
    }

//    public static final String ENDPOINT = "/api/v2/projects";
//    public static final String ENDPOINT_ID = "/{id}";
//    public static final String PATH_VARIABLE_ID = "id";
//    private static final String API_PARAM_ID = "ID";
//
//
//    private final ProjectService projectService;
//
//    @ApiOperation("Get a Project")
//    @GetMapping(ENDPOINT_ID)
//    public Project getProject(@ApiParam(name = API_PARAM_ID, required = true)
//                              @PathVariable(PATH_VARIABLE_ID) final long projectId) {
//        return projectService.getProject(projectId);
//    }
//
//
//    @PostMapping
//    @ApiOperation("Create a Project")
//    public ResponseEntity<String> createProject(
//            @Valid @RequestBody ProjectRequest projectRequest) {
//        Project project = projectService.createProject(projectRequest);
//        UriComponents uriComponents =
//                UriComponentsBuilder.fromUriString(ENDPOINT + ENDPOINT_ID).buildAndExpand(project.getId());
//        return ResponseEntity.created(uriComponents.toUri()).build();
//    }
//
//
//    @ApiOperation("Update a Project")
//    @PatchMapping(ENDPOINT_ID)
//    public ResponseEntity<Project> updateProject(@PathVariable(PATH_VARIABLE_ID) final long projectId,
//                                                 @RequestBody UpdateProjectRequest projectRequest) {
//        return ResponseEntity.ok(projectService.updateProject(projectId, projectRequest));
//    }
}
