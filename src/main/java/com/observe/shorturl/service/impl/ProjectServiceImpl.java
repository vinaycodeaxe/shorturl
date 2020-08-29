package com.observe.shorturl.service.impl;

import com.crossover.codeserver.dao.entity.Project;
import com.crossover.codeserver.dao.entity.SdlcSystem;
import com.crossover.codeserver.dao.repository.ProjectRepository;
import com.crossover.codeserver.dao.repository.SdlcSystemRepository;
import com.crossover.codeserver.dto.request.ProjectRequest;
import com.crossover.codeserver.dto.request.UpdateProjectRequest;
import com.crossover.codeserver.exception.ConflictException;
import com.crossover.codeserver.exception.NoDataFoundException;
import com.crossover.codeserver.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;
	private final SdlcSystemRepository sdlcSystemRepository;

	public Project getProject(long id) {
		return projectRepository.findById(id).orElseThrow(() -> new NoDataFoundException(Project.class, id));

	}

	@Override
	public Project createProject(ProjectRequest projectRequest) {
		long sdlcSystemId = projectRequest.getSdlcSystem().getId();
		SdlcSystem sdlcSystem = sdlcSystemRepository.findById(sdlcSystemId).orElseThrow(
				() -> new NoDataFoundException(Project.class, sdlcSystemId)
		);
		boolean isPresent = projectRepository.findBySdlcSystemIdAndExternalId(sdlcSystemId, projectRequest.getExternalId()).isPresent();
		if (isPresent) {
			throw new ConflictException(Project.class, projectRequest.getExternalId());
		}

		Project project = Project
				.builder()
				.externalId(projectRequest.getExternalId())
				.name(projectRequest.getName())
				.sdlcSystem(sdlcSystem)
				.build();

		return projectRepository.save(project);
	}

	@Override
	public Project updateProject(long projectId, UpdateProjectRequest projectRequest) {
		Project project = projectRepository.findById(projectId).orElseThrow(
				() -> new NoDataFoundException(Project.class, projectId)
		);


		if (projectRequest.getSdlcSystem() != null) {
			SdlcSystem sdlcSystem = sdlcSystemRepository.findById(projectRequest.getSdlcSystem().getId()).orElseThrow(
					() -> new NoDataFoundException(Project.class, projectRequest.getSdlcSystem().getId())
			);
			project.setSdlcSystem(projectRequest.getSdlcSystem() == null ? project.getSdlcSystem() : sdlcSystem);
		}

		if (projectRequest.getExternalId() != null) {
			project.setExternalId(projectRequest.getExternalId());
		}

		project.setName(UpdateProjectRequest.NO_CHANGE.equalsIgnoreCase(projectRequest.getName()) ? project.getName() : projectRequest.getName());

		try {
			return projectRepository.save(project);
		} catch (DataIntegrityViolationException e) {
			throw new ConflictException(Project.class, String.valueOf(projectId));
		}

	}
}
