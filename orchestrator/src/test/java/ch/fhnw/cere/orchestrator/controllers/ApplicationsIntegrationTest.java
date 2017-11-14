package ch.fhnw.cere.orchestrator.controllers;


import ch.fhnw.cere.orchestrator.controllers.helpers.ApplicationTreeBuilder;
import ch.fhnw.cere.orchestrator.helpers.JsonHelper;
import ch.fhnw.cere.orchestrator.models.*;
import ch.fhnw.cere.orchestrator.repositories.ApiUserPermissionRepository;
import ch.fhnw.cere.orchestrator.repositories.ApplicationRepository;
import ch.fhnw.cere.orchestrator.serialization.ParameterSerializerModifier;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.nullValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.ServletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class ApplicationsIntegrationTest extends BaseIntegrationTest {

    private Application application1;
    private Application application2;
    private Application application3;

    @Autowired
    private ApplicationRepository applicationRepository;
    private String basePathEn = "/orchestrator/feedback/en/applications";
    private String basePathDe = "/orchestrator/feedback/de/applications";
    @Autowired
    private ApplicationTreeBuilder applicationTreeBuilder;
    @Autowired
    private ApiUserPermissionRepository apiUserPermissionRepository;
    @Autowired
    private JsonHelper jsonHelper;

    @Before
    public void setup() throws Exception {
        super.setup();
        this.applicationRepository.deleteAllInBatch();

        this.application1 = applicationRepository.save(new Application("Test App 1", 1, new Date(), new Date(), null));
        this.application2 = applicationRepository.save(new Application("Test App 2", 1, new Date(), new Date(), null));
        this.application3 = applicationRepository.save(applicationTreeBuilder.buildApplicationTree("Test application 3"));

        apiUserPermissionRepository.save(new ApiUserPermission(appAdminUser, application2, true));
    }

    @After
    public void cleanUp() {
        super.cleanUp();
        this.applicationRepository.deleteAllInBatch();
        this.apiUserPermissionRepository.deleteAllInBatch();
    }

    @Test
    public void applicationNotFound() throws Exception {
        this.mockMvc.perform(get(basePathEn + "/9999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getApplications() throws Exception {
        mockMvc.perform(get(basePathEn + "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void getApplication() throws Exception {
        mockMvc.perform(get(basePathEn + "/" + application1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is((int) this.application1.getId())))
                .andExpect(jsonPath("$.name", is("Test App 1")))
                .andExpect(jsonPath("$.state", is(1)))
                .andExpect(jsonPath("$.configurations", is(Matchers.empty())));


        mockMvc.perform(get(basePathEn + "/" + application3.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is((int) this.application3.getId())))
                .andExpect(jsonPath("$.name", is("Test application 3")))
                .andExpect(jsonPath("$.state", is(1)))
                .andExpect(jsonPath("$.generalConfiguration.name", is("General configuration Test application 3")))
                .andExpect(jsonPath("$.generalConfiguration.parameters", hasSize(1)))
                .andExpect(jsonPath("$.generalConfiguration.parameters[0].key", is("reviewActive")))
                .andExpect(jsonPath("$.generalConfiguration.parameters[0].value", is(true)))
                .andExpect(jsonPath("$.configurations", hasSize(2)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].active", is(true)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].order", is(1)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters", hasSize(3)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[0].language", is("en")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[1].language", is("en")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[2].language", is("en")))
                .andExpect(jsonPath("$.configurations[0].generalConfiguration.name", is("General configuration for push configuration")))
                .andExpect(jsonPath("$.configurations[0].generalConfiguration.parameters", hasSize(1)))
                .andExpect(jsonPath("$.configurations[0].generalConfiguration.parameters[0].key", is("reviewActive")))
                .andExpect(jsonPath("$.configurations[0].generalConfiguration.parameters[0].value", is(false)));

        mockMvc.perform(get(basePathDe + "/" + application3.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is((int) this.application3.getId())))
                .andExpect(jsonPath("$.name", is("Test application 3")))
                .andExpect(jsonPath("$.state", is(1)))
                .andExpect(jsonPath("$.configurations", hasSize(2)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters", hasSize(3)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[0].key", is("options")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[0].language", is("en")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[1].key", is("font-size")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[1].language", is("en")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[2].key", is("title")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[2].language", is("de")));
    }

    @Test
    public void getApplicationWithNestedParameters() throws Exception {
        mockMvc.perform(get(basePathEn + "/" + application3.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is((int) this.application3.getId())))
                .andExpect(jsonPath("$.name", is("Test application 3")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].active", is(true)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].order", is(1)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters", hasSize(3)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[0].key", is("options")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[0].value[0].key", is("CAT_1")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[0].language", is("en")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[1].key", is("font-size")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[1].value", is("10")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[1].language", is("en")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[2].key", is("title")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[2].value", is("Title EN")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[2].language", is("en")));
    }

    @Test(expected = ServletException.class)
    public void postApplicationUnauthorized() throws Exception {
        Application application = new Application("Test App 4", 1, new Date(), new Date(), null);
        String applicationJson = toJson(application);

        this.mockMvc.perform(post(basePathEn)
                .contentType(contentType)
                .content(applicationJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postApplication() throws Exception {
        Application application = new Application("Test App 4", 1, new Date(), new Date(), null);
        String applicationJson = toJson(application);

        String adminJWTToken = requestAdminJWTToken();

        this.mockMvc.perform(post(basePathEn)
                .contentType(contentType)
                .header("Authorization", adminJWTToken)
                .content(applicationJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void postApplicationObjectTree() throws Exception {
        Application application = applicationTreeBuilder.buildApplicationTree("Test App 4");
        application.setConfigurations(Lists.reverse(application.getConfigurations()));

        String applicationJson = toJson(application);

        String adminJWTToken = requestAdminJWTToken();

        MvcResult result = this.mockMvc.perform(post(basePathEn)
                .contentType(contentType)
                .header("Authorization", adminJWTToken)
                .content(applicationJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test App 4")))
                .andExpect(jsonPath("$.state", is(1)))
                .andExpect(jsonPath("$.configurations", hasSize(2)))
                .andExpect(jsonPath("$.generalConfiguration.name", is("General configuration Test App 4")))
                .andExpect(jsonPath("$.generalConfiguration.parameters", hasSize(1)))
                .andExpect(jsonPath("$.generalConfiguration.parameters[0].key", is("reviewActive")))
                .andExpect(jsonPath("$.generalConfiguration.parameters[0].value", is(true)))
                .andExpect(jsonPath("$.configurations[1].name", is("Push configuration 1 of Test App 4")))
                .andExpect(jsonPath("$.configurations[1].type", is("PUSH")))
                .andExpect(jsonPath("$.configurations[1].mechanisms[0].type", is("TEXT_TYPE")))
                .andExpect(jsonPath("$.configurations[1].mechanisms[0].active", is(true)))
                .andExpect(jsonPath("$.configurations[1].mechanisms[0].order", is(1)))
                .andExpect(jsonPath("$.configurations[1].mechanisms[0].parameters", hasSize(4)))
                .andExpect(jsonPath("$.configurations[1].generalConfiguration.name", is("General configuration for push configuration")))
                .andExpect(jsonPath("$.configurations[1].generalConfiguration.parameters", hasSize(1)))
                .andExpect(jsonPath("$.configurations[1].generalConfiguration.parameters[0].key", is("reviewActive")))
                .andExpect(jsonPath("$.configurations[1].generalConfiguration.parameters[0].value", is(false))).andReturn();


        String createdApplicationString = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Application createdApplication = mapper.readValue(createdApplicationString, Application.class);
        assertEquals(createdApplication.getName(), "Test App 4");

        mockMvc.perform(get(basePathEn + "/" + createdApplication.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name", is("Test App 4")))
                .andExpect(jsonPath("$.state", is(1)))
                .andExpect(jsonPath("$.configurations", hasSize(2)))
                .andExpect(jsonPath("$.generalConfiguration.name", is("General configuration Test App 4")))
                .andExpect(jsonPath("$.generalConfiguration.parameters", hasSize(1)))
                .andExpect(jsonPath("$.generalConfiguration.parameters[0].key", is("reviewActive")))
                .andExpect(jsonPath("$.generalConfiguration.parameters[0].value", is(true)))
                .andExpect(jsonPath("$.configurations[1].name", is("Push configuration 1 of Test App 4")))
                .andExpect(jsonPath("$.configurations[1].type", is("PUSH")))
                .andExpect(jsonPath("$.configurations[1].mechanisms[0].type", is("TEXT_TYPE")))
                .andExpect(jsonPath("$.configurations[1].mechanisms[0].active", is(true)))
                .andExpect(jsonPath("$.configurations[1].mechanisms[0].order", is(1)))
                .andExpect(jsonPath("$.configurations[1].mechanisms[0].parameters", hasSize(3)))
                .andExpect(jsonPath("$.configurations[1].generalConfiguration.name", is("General configuration for push configuration")))
                .andExpect(jsonPath("$.configurations[1].generalConfiguration.parameters", hasSize(1)))
                .andExpect(jsonPath("$.configurations[1].generalConfiguration.parameters[0].key", is("reviewActive")))
                .andExpect(jsonPath("$.configurations[1].generalConfiguration.parameters[0].value", is(false)));
    }

    @Test
    public void postJsonTree() throws Exception {
        String applicationJson = "{\"name\":\"Grid Data UI\",\"state\":1,\"createdAt\":null,\"updatedAt\":null,\"generalConfiguration\":{\"name\":\"Push Configuration Grid\",\"createdAt\":null,\"updatedAt\":null,\"parameters\":null},\"configurations\":[{\"name\":\"Grid Data 2\",\"type\":\"PUSH\",\"createdAt\":null,\"updatedAt\":null,\"generalConfiguration\":null,\"pullDefault\":true,\"pushDefault\":true,\"mechanisms\":[{\"type\":\"TEXT_TYPE\",\"createdAt\":null,\"updatedAt\":null,\"active\":true,\"order\":1,\"parameters\":[{\"key\":\"label\",\"value\":\"Please give your feedback\",\"createdAt\":null,\"updatedAt\":null,\"language\":\"en\",\"parameters\":null}]},{\"type\":\"RATING_TYPE\",\"createdAt\":null,\"updatedAt\":null,\"active\":true,\"order\":2,\"canBeActivated\":true,\"parameters\":[{\"key\":\"title\",\"value\":\"Rate Grid Data UI\",\"defaultValue\":\"\",\"editableByUser\":false,\"language\":\"en\",\"createdAt\":null,\"updatedAt\":null,\"parameters\":null},{\"key\":\"defaultRating\",\"value\":0,\"defaultValue\":\"\",\"editableByUser\":false,\"createdAt\":null,\"updatedAt\":null,\"parameters\":null},{\"key\":\"maxRating\",\"value\":5,\"defaultValue\":\"\",\"editableByUser\":false,\"language\":\"en\",\"createdAt\":null,\"updatedAt\":null,\"parameters\":null}]},{\"type\":\"SCREENSHOT_TYPE\",\"createdAt\":null,\"updatedAt\":null,\"active\":true,\"order\":3,\"parameters\":null},{\"type\":\"ATTACHMENT_TYPE\",\"active\":true,\"order\":4,\"canBeActivated\":true,\"createdAt\":null,\"updatedAt\":null,\"parameters\":[{\"key\":\"title\",\"value\":\"Attach files (optional)\",\"language\":\"en\",\"createdAt\":null,\"updatedAt\":null,\"parameters\":null}]}],\"userGroups\":[]}],\"users\":null,\"userGroups\":null,\"apiUserPermissions\":null}";

        String adminJWTToken = requestAdminJWTToken();

        MvcResult result = this.mockMvc.perform(post(basePathEn)
                .contentType(contentType)
                .header("Authorization", adminJWTToken)
                .content(applicationJson))
                .andExpect(status().isCreated()).andReturn();

        String createdApplicationString = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        Application createdApplication = mapper.readValue(createdApplicationString, Application.class);
        assertEquals(createdApplication.getName(), "Grid Data UI");

        mockMvc.perform(get(basePathEn + "/" + createdApplication.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.name", is("Grid Data UI")))
                .andExpect(jsonPath("$.state", is(1)))
                .andExpect(jsonPath("$.generalConfiguration.name", is("Push Configuration Grid")))
                .andExpect(jsonPath("$.generalConfiguration.parameters", hasSize(0)))
                .andExpect(jsonPath("$.configurations", hasSize(1)))
                .andExpect(jsonPath("$.configurations[0].name", is("Grid Data 2")))
                .andExpect(jsonPath("$.configurations[0].type", is("PUSH")))
                .andExpect(jsonPath("$.configurations[0].mechanisms", hasSize(4)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].type", is("TEXT_TYPE")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].active", is(true)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].order", is(1)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters", hasSize(1)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[0].key", is("label")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[0].parameters[0].value", is("Please give your feedback")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[1].type", is("RATING_TYPE")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[1].active", is(true)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[1].order", is(2)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[1].parameters", hasSize(3)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[1].parameters[0].key", is("defaultRating")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[1].parameters[0].value", is("0")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[1].parameters[1].key", is("maxRating")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[1].parameters[1].value", is("5")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[1].parameters[2].key", is("title")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[1].parameters[2].value", is("Rate Grid Data UI")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[2].type", is("SCREENSHOT_TYPE")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[2].active", is(true)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[2].order", is(3)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[3].type", is("ATTACHMENT_TYPE")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[3].active", is(true)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[3].order", is(4)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[3].parameters", hasSize(1)))
                .andExpect(jsonPath("$.configurations[0].mechanisms[3].parameters[0].key", is("title")))
                .andExpect(jsonPath("$.configurations[0].mechanisms[3].parameters[0].value", is("Attach files (optional)")));
    }

    @Test(expected = ServletException.class)
    public void deleteApplicationUnauthorized() throws Exception {

        this.mockMvc.perform(delete(basePathEn + "/" + application1.getId()))
                .andExpect(status().isOk());
    }

    @Test(expected = ServletException.class)
    public void deleteApplicationWithoutPermission() throws Exception {
        String adminJWTToken = requestAdminJWTToken();

        this.mockMvc.perform(delete(basePathEn + "/" + application1.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk());
    }

    @Test(expected = ServletException.class)
    public void deleteApplicationOtherPermission() throws Exception {
        String adminJWTToken = requestAppAdminJWTToken();

        this.mockMvc.perform(delete(basePathEn + "/" + application1.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteApplication() throws Exception {
        String adminJWTToken = requestAppAdminJWTToken();

        this.mockMvc.perform(delete(basePathEn + "/" + application2.getId())
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk());
    }

    @Test(expected = ServletException.class)
    public void updateApplicationUnauthorized() throws Exception {
        this.application2.setName("Updated name for App 2");
        this.application2.setState(0);
        String applicationJson = toJson(this.application2);

        this.mockMvc.perform(put(basePathEn + "/")
                .contentType(contentType)
                .content(applicationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) this.application2.getId())))
                .andExpect(jsonPath("$.name", is("Updated name for App 2")))
                .andExpect(jsonPath("$.state", is(0)))
                .andExpect(jsonPath("$.configurations", is(nullValue())));
    }

    @Test(expected = ServletException.class)
    public void updateApplicationWithoutPermission() throws Exception {
        this.application2.setName("Updated name for App 2");
        this.application2.setState(0);
        String applicationJson = toJson(this.application2);

        String adminJWTToken = requestAdminJWTToken();

        this.mockMvc.perform(put(basePathEn + "/")
                .contentType(contentType)
                .content(applicationJson)
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) this.application2.getId())))
                .andExpect(jsonPath("$.name", is("Updated name for App 2")))
                .andExpect(jsonPath("$.state", is(0)))
                .andExpect(jsonPath("$.configurations", is(nullValue())));
    }

    @Test
    public void updateApplication() throws Exception {
        this.application2.setName("Updated name for App 2");
        this.application2.setState(0);
        String applicationJson = toJson(this.application2);

        String adminJWTToken = requestAppAdminJWTToken();

        this.mockMvc.perform(put(basePathEn + "/")
                .contentType(contentType)
                .content(applicationJson)
                .header("Authorization", adminJWTToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) this.application2.getId())))
                .andExpect(jsonPath("$.name", is("Updated name for App 2")))
                .andExpect(jsonPath("$.state", is(0)))
                .andExpect(jsonPath("$.configurations", is(nullValue())));
    }

    @Test
    public void importApplications() throws Exception {
        this.applicationRepository.deleteAllInBatch();

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("applications.json").getFile());
        String applicationsJson = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);

        String superAdminJWTToken = requestSuperAdminJWTToken();

        this.mockMvc.perform(post(basePathEn + "/import")
                .contentType(contentType)
                .header("Authorization", superAdminJWTToken)
                .content(applicationsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(7)))
                .andExpect(jsonPath("$[0].name", is("Test")))
                .andExpect(jsonPath("$[0].configurations[0].type", is("PUSH")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].type", is("CATEGORY_TYPE")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters", hasSize(6)))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[0].key", is("mandatoryReminder")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[0].value", is("Please select at least one category")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[1].key", is("options")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[1].value[0].key", is("SLOW_CONNECTION")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[1].value[0].value", is("Slow connection")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[1].value[1].key", is("VIDEO_IS_FREEZING")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[1].value[1].value", is("Video is freezing")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[1].value[2].key", is("BUG_CATEGORY")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[1].value[2].value", is("Bug")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[1].value[3].key", is("FEATURE_REQUEST_CATEGORY")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[1].value[3].value", is("Feature request")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[1].value[4].key", is("GENERAL_CATEGORY")))
                .andExpect(jsonPath("$[0].configurations[0].mechanisms[0].parameters[1].value[4].value", is("General feedback")));


        mockMvc.perform(get(basePathEn + "/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)));
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    @Test
    public void stripOffAttributes() {
        String payload = "{\n" +
                "    \"id\": 3,\n" +
                "    \"name\": \"Test Website 21\",\n" +
                "    \"state\": 0,\n" +
                "    \"generalConfiguration\": {\n" +
                "      \"id\": 6,\n" +
                "      \"parameters\": [\n" +
                "        {\n" +
                "          \"id\": 194,\n" +
                "          \"key\": \"test\",\n" +
                "          \"value\": \"test\",\n" +
                "          \"language\": \"en\",\n" +
                "          \"createdAt\": \"2016-11-03 11:15:16.0\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": 41,\n" +
                "          \"key\": \"mainColor\",\n" +
                "          \"value\": \"#00ff0f\",\n" +
                "          \"language\": \"en\",\n" +
                "          \"createdAt\": \"2016-11-10 10:33:35.0\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"createdAt\": \"2017-02-20 06:08:25.577\"\n" +
                "    }}";

        payload = payload.replaceAll("\"createdAt\":.*", "");

        System.err.println(payload);
    }

    @Test
    public void stripOffInvalidCommas() {
        String payload = "{\n" +
                "    \"name\": \"Test Website 21\",\n" +
                "    \"state\": 0,\n" +
                "    \"generalConfiguration\": {\n" +
                "      \"parameters\": [\n" +
                "        {\n" +
                "          \"key\": \"test\",\n" +
                "          \"value\": \"test\",\n" +
                "          \"language\": \"en\",\n" +
                "        },\n" +
                "        {\n" +
                "          \"key\": \"mainColor\",\n" +
                "          \"value\": \"#00ff0f\",\n" +
                "          \"language\": \"en\",\n" +
                "        }\n" +
                "      ],\n" +
                "    }}";

        payload = payload.replaceAll(",\\n\\s*}", "\n}");
        System.err.println(payload);
    }

    @Test
    public void applicationsDeserialization() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("applications.json").getFile());
        String payload = readFile(file.getAbsolutePath(), StandardCharsets.UTF_8);

        payload = jsonHelper.stripOffIds(payload);
        payload = jsonHelper.stripOffTimestamps(payload);
        payload = jsonHelper.stripOffInvalidCommas(payload);
        payload = jsonHelper.stripOffEmptyLines(payload);

        ObjectMapper mapper = new ObjectMapper();
        List<Application> applications = mapper.readValue(payload, new TypeReference<List<Application>>(){});

        assertEquals(7, applications.size());
    }
}