package com.argusoft.path.tht.testprocessmanagement.mock;

import com.argusoft.path.tht.testcasemanagement.repository.ComponentRepository;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity;
import com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestUrlEntity;
import com.argusoft.path.tht.testprocessmanagement.repository.TestRequestRepository;
import com.argusoft.path.tht.usermanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TestRequestServiceMockImpl {
    @Autowired
    TestRequestRepository testRequestRepository;

    @Autowired
    ComponentRepository componentRepository;

    @Autowired
    UserRepository userRepository;

    public void init()
    {
        createTestRequest("TestRequest.01","TestRequest 1", "user.08",
                "user.07","TestRequest 1","test.request.status.accepted","component.02","https://hapi.fhir.org/baseR4","dummyuser7@testmail.com",
                "password","R4","");
        createTestRequest("TestRequest.02","TestRequest 2", "user.08",
                "user.07","TestRequest 2","test.request.status.accepted","component.02","https://hapi.fhir.org/baseR4","dummyuser8@testmail.com",
                "password","R4","");
        createTestRequest("TestRequest.03","TestRequest 3", "user.01",
                "SYSTEM_USER","TestRequest 3","test.request.status.pending","component.02","https://hapi.fhir.org/baseR4","dummyuser8@testmail.com",
                "password","R4","");
        createTestRequest("TestRequest.04","TestRequest 4", "user.01",
                "SYSTEM_USER","TestRequest 4","test.request.status.pending","component.06","https://hapi.fhir.org/baseR4","dummyuser8@testmail.com",
                "password","R4","");
        createTestRequest("TestRequest.05","TestRequest 5", "user.01",
                "SYSTEM_USER","TestRequest 5","test.request.status.pending","component.08","https://hapi.fhir.org/baseR4","dummyuser8@testmail.com",
                "password","R4","");
        createTestRequest("TestRequest.06","TestRequest 6", "user.01",
                "SYSTEM_USER","TestRequest 6","test.request.status.pending","component.02","https://hapi.fhir.org/baseR4","dummyuser8@testmail.com",
                "password","R4","");
        createTestRequest("TestRequest.10","TestRequest 10", "user.01",
                "SYSTEM_USER","TestRequest 10","test.request.status.pending","component.02","https://hapi.fhir.org/baseR4","dummyuser8@testmail.com",
                "password","R4","");

    }

    public TestRequestEntity createTestRequest(String testRequestId, String name, String assesseeId, String approverId, String description,
                                               String state, String componentId, String fhirApiBaseUrl, String username,
                                               String password, String fhirVersion, String websiteUIBaseUrl) {
        TestRequestEntity testRequestEntity = new TestRequestEntity();
        HashSet<TestRequestUrlEntity> testRequestUrlEntities = new HashSet<>();
        if(!testRequestId.equals("TestRequest.03")) {
            TestRequestUrlEntity testRequestUrlEntity = new TestRequestUrlEntity();
            testRequestUrlEntity.setTestRequestId(testRequestId);
            testRequestUrlEntity.setFhirApiBaseUrl(fhirApiBaseUrl);
            testRequestUrlEntity.setUsername(username);
            testRequestUrlEntity.setPassword(password);
            testRequestUrlEntity.setFhirVersion(fhirVersion);
            testRequestUrlEntity.setWebsiteUIBaseUrl(websiteUIBaseUrl);
            testRequestUrlEntity.setComponent(componentRepository.findById(componentId).get());
            if(testRequestId.equals("TestRequest.06")){
                testRequestUrlEntity.setFhirApiBaseUrl(null);
            }
            if(testRequestId.equals("TestRequest.10")){
                TestRequestUrlEntity testRequestUrlEntity10 = new TestRequestUrlEntity();
                testRequestUrlEntity10.setTestRequestId(testRequestId);
                testRequestUrlEntity10.setFhirApiBaseUrl(fhirApiBaseUrl);
                testRequestUrlEntity10.setUsername(username);
                testRequestUrlEntity10.setPassword(password);
                testRequestUrlEntity10.setFhirVersion(fhirVersion);
                testRequestUrlEntity10.setWebsiteUIBaseUrl(websiteUIBaseUrl);
                testRequestUrlEntity10.setComponent(componentRepository.findById("component.04").get());

                testRequestUrlEntities.add(testRequestUrlEntity10);
            }
            testRequestUrlEntities.add(testRequestUrlEntity);
        }
        testRequestEntity.setAssessee(userRepository.findById(assesseeId).get());
        testRequestEntity.setApprover(userRepository.findById(approverId).get());
        testRequestEntity.setName(name);
        testRequestEntity.setId(testRequestId);
        testRequestEntity.setDescription(description);
        testRequestEntity.setState(state);
        testRequestEntity.setCreatedBy(assesseeId);
        testRequestEntity.setCreatedAt(new Date());
        testRequestEntity.setUpdatedBy(assesseeId);
        testRequestEntity.setUpdatedAt(new Date());
        testRequestEntity.setTestRequestUrls(testRequestUrlEntities);
        testRequestRepository.save(testRequestEntity);
        return testRequestEntity;
    }
    public void clear() {
        testRequestRepository.deleteAll();
    }
}
