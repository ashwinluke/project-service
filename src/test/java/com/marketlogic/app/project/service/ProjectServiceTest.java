package com.marketlogic.app.project.service;

import com.marketlogic.app.common.error.AppServiceException;
import com.marketlogic.app.project.constants.Status;
import com.marketlogic.app.project.constants.Type;
import com.marketlogic.app.project.dto.ProjectDTO;
import com.marketlogic.app.project.entity.Project;
import com.marketlogic.app.project.entity.Section;
import com.marketlogic.app.project.repository.ProjectRecordRepository;
import com.marketlogic.app.project.repository.ProjectRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@TestPropertySource(locations = "classpath:test.properties")
@EnableTransactionManagement
public class ProjectServiceTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRecordRepository projectRecordRepository;

    private static final ModelMapper MODEL_MAPPER = new ModelMapper();

    List<Project> projectList = new ArrayList<>();

    @Before
    public void initTestData() {
        var projectWithoutSection = new Project();
        projectWithoutSection.setTitle("title");
        projectWithoutSection.setDescription("description");
        projectWithoutSection.setType(Type.TYPE_1);
        projectWithoutSection.setStatus(Status.DRAFT);
        projectList.add(projectRepository.save(projectWithoutSection));

        var projectSection = new Project();
        projectSection.setTitle("projectTitle");
        projectSection.setDescription("setDescription");
        projectSection.setType(Type.TYPE_1);
        projectSection.setStatus(Status.DRAFT);
        var section = new Section();
        section.setTitle("SectionTitle");
        section.setDescription("SectionDescription");
        section.setProject(projectSection);
        projectSection.getSections().add(section);
        projectList.add(projectRepository.save(projectSection));
    }

    @After
    public void cleanTestData() {
        projectList.forEach(project -> {
            projectRepository.delete(project);
        });
    }

    @Test
    public void testFindAll() {
        Pageable pageable = PageRequest.of(
                0, 25, Sort.by("id").ascending()
        );
        // Asserting pagination related attributes
        var projectResponse = projectService.findAll(pageable);
        Assert.assertEquals(2, projectResponse.getTotalElements());
        Assert.assertEquals(2, projectResponse.getContent().size());
        Assert.assertEquals(25, projectResponse.getSize());
        Assert.assertEquals(2, projectResponse.getNumberOfElements());
        Assert.assertEquals(1, projectResponse.getTotalPages());
        Assert.assertTrue(projectResponse.getContent().get(0).getId() < projectResponse.getContent().get(1).getId());

        // Asserting pagination related attributes - sort

        pageable = PageRequest.of(
                0, 25, Sort.by("id").descending()
        );
        // Asserting pagination related attributes
        projectResponse = projectService.findAll(pageable);
        Assert.assertTrue(projectResponse.getContent().get(0).getId() > projectResponse.getContent().get(1).getId());
    }

    @Test(expected = AppServiceException.class)
    public void testFindById_Fail() {
        // Invalid Project ID
        projectService.findById(11111L);
    }

    @Test
    public void testFindById_Success() {
        var projectDTO = projectService.findById(projectList.get(0).getId());
        Assert.assertNotNull(projectDTO);
    }

    @Test(expected = AppServiceException.class)
    public void testUpdate_Failure() {
        projectService.update(1231231L, null);
    }

    @Test
    public void testUpdate_Success() {
        var project = projectList.get(0);
        Assert.assertEquals("title", project.getTitle());
        Assert.assertEquals("description", project.getDescription());
        Assert.assertEquals(Type.TYPE_1, project.getType());
        Assert.assertEquals(Status.DRAFT, project.getStatus());

        // Updating the existing project
        var projectDTO = MODEL_MAPPER.map(project, ProjectDTO.class);
        projectDTO.setTitle("updatedTitle");
        projectDTO.setDescription("updatedDescription");
        projectDTO.setType(Type.TYPE_3);
        projectDTO = projectService.update(project.getId(), projectDTO);

        Assert.assertEquals("updatedTitle", projectDTO.getTitle());
        Assert.assertEquals("updatedDescription", projectDTO.getDescription());
        Assert.assertEquals(Type.TYPE_3, projectDTO.getType());
    }

    @Test(expected = AppServiceException.class)
    public void testDeleteById_Error() {
        // Invalid Project ID
        projectService.deleteById(11111L);
    }

    @Test
    public void testDeleteById_Success() {
        projectService.deleteById(projectList.get(0).getId());
        projectList.remove(0);
        Assert.assertEquals(1, projectList.size());
    }

    @Test(expected = AppServiceException.class)
    public void publishById_Fail_InvalidId() {
        projectService.publishById(11111L);
    }

    @Test
    public void publishById_Success() {
        projectService.publishById(projectList.get(0).getId());
        var projectDTO = projectService.findById(projectList.get(0).getId());
        Assert.assertEquals(Status.PUBLISHED, projectDTO.getStatus());
        projectRecordRepository.findAll().forEach(projectRecord -> {
            Assert.assertEquals(projectRecord.getTitle(), projectDTO.getTitle());
            projectRecordRepository.delete(projectRecord);
        });
        var project = projectRepository.findById(projectList.get(0).getId());
        Assert.assertEquals(Status.PUBLISHED, project.getStatus());
    }

}
