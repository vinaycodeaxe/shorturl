package com.observe.shorturl.service;

import com.crossover.codeserver.dao.entity.Project;
import com.crossover.codeserver.dto.request.ProjectRequest;
import com.crossover.codeserver.dto.request.UpdateProjectRequest;

public interface ProjectService {

    Project getProject(long id);

    Project createProject(ProjectRequest projectRequest);

    Project updateProject(long projectId, UpdateProjectRequest projectRequest);
}
