package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.constant.Constant;
import com.argusoft.path.tht.systemconfiguration.constant.SearchType;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.filter.ComponentSearchFilter;
import com.argusoft.path.tht.testcasemanagement.filter.SpecificationSearchFilter;
import com.argusoft.path.tht.testcasemanagement.filter.TestcaseSearchFilter;
import com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity;
import com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity;
import com.argusoft.path.tht.testcasemanagement.service.ComponentService;
import com.argusoft.path.tht.testcasemanagement.service.SpecificationService;
import com.argusoft.path.tht.testcasemanagement.service.TestcaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implemantation of the CR Testing.
 * Reference
 *
 * @author dhruv
 * @since 2023-09-25
 */
@Component
public class TestcaseExecutionar {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private SpecificationService specificationService;

    @Autowired
    private TestcaseService testcaseService;

    public ValidationResultInfo executeAutomationTestingByTestRequest(
            String testRequestId,
            ContextInfo contextInfo) {
        return testRequest(testRequestId, contextInfo);
    }

    private ValidationResultInfo testRequest(String testRequestId,
                                             ContextInfo contextInfo) {
        try {
            //change ResultItem by testRequestId to inProgress

            List<ComponentEntity> activeComponents = componentService.searchComponents(
                    null,
                    new ComponentSearchFilter(null,
                            SearchType.CONTAINING,
                            ComponentServiceConstants.COMPONENT_STATUS_ACTIVE,
                            SearchType.EXACTLY),
                    Constant.FULL_PAGE,
                    contextInfo).getContent();

            //TODO: Filter activeComponents based on the testRequest as well

            //TODO: Create IGenericClient Based on the component's baseUrl instead of fix value
            IGenericClient client = getClient(null, null, null, null);

            for (ComponentEntity componentEntity : activeComponents) {
                return this.testComponent(componentEntity.getId(),
                        testRequestId,
                        client,
                        contextInfo);
            }

            return null;
            //change ResultItem by testRequestId to finished and add appropriate result message.
        } catch (Exception e) {
            //TODO add system failure for ResultItem by testRequestId
            return null;
        }
    }

    private ValidationResultInfo testComponent(String componentId,
                                               String testRequestId,
                                               IGenericClient client,
                                               ContextInfo contextInfo) {
        try {
            //change ResultItem by testRequestId, componentId to inProgress

            List<SpecificationEntity> activeSpecifications = specificationService.searchSpecifications(
                    null,
                    new SpecificationSearchFilter(null,
                            SearchType.CONTAINING,
                            SpecificationServiceConstants.SPECIFICATION_STATUS_ACTIVE,
                            SearchType.EXACTLY,
                            componentId),
                    Constant.FULL_PAGE,
                    contextInfo).getContent();

            for (SpecificationEntity specificationEntity : activeSpecifications) {
                return this.testSpecification(specificationEntity.getId(),
                        testRequestId,
                        client,
                        contextInfo);
            }

            return null;
            //change ResultItem by testRequestId, componentId to finished and add appropriate result message.
        } catch (Exception e) {
            //TODO add system failure for ResultItem and componentId by testRequestId
            return null;
        }
    }

    private ValidationResultInfo testSpecification(String specificationId,
                                                   String testRequestId,
                                                   IGenericClient client,
                                                   ContextInfo contextInfo) {
        try {
            //change ResultItem by testRequestId, specificationId to inProgress

            List<TestcaseEntity> activeTestcases = testcaseService.searchTestcases(
                    null,
                    new TestcaseSearchFilter(null,
                            SearchType.EXACTLY,
                            TestcaseServiceConstants.TESTCASE_STATUS_ACTIVE,
                            SearchType.EXACTLY,
                            specificationId),
                    Constant.FULL_PAGE,
                    contextInfo).getContent();

//            validationResultInfos.add(cRTestCases.test("specificationId", "testReqId", client, contextInfo));

//            if (ValidationUtils.containsErrors(validationResultInfos, ErrorLevel.ERROR)) {
//                System.out.println("Failed");
//                return new ValidationResultInfo("TestcaseExecutionar", ErrorLevel.OK, "Failed");
//            } else {
//                System.out.println("Passed");
//                return new ValidationResultInfo("TestcaseExecutionar", ErrorLevel.OK, "Passed");
//            }

            for (TestcaseEntity testcaseEntity : activeTestcases) {
                return this.executeTestcase(
                        testcaseEntity,
                        testRequestId,
                        client,
                        contextInfo);
            }

            return null;
            //change ResultItem by testRequestId, specificationId to finished and add appropriate result message.
        } catch (Exception e) {
            //TODO add system failure for ResultItem by testRequestId and specificationId
            return null;
        }
    }

    private ValidationResultInfo executeTestcase(TestcaseEntity testcaseEntity,
                                                 String testRequestId,
                                                 IGenericClient client,
                                                 ContextInfo contextInfo) {
        try {
            //change ResultItem by testRequestId, testcaseId to inProgress

            TestCase testCaseExecutionService = (TestCase) applicationContext.getBean(testcaseEntity.getBeanName());
            testCaseExecutionService.test(client, contextInfo);

            return null;
            //change ResultItem by testRequestId, testcaseId to finished and add appropriate result message.
        } catch (Exception e) {
            //TODO add system failure for ResultItem by testRequestId and testcaseId
            return null;
        }
    }

    private void updateTestCaseState(String refObjUri,
                                     String refId,
                                     String testRequestId,
                                     ContextInfo contextInfo) {

    }

    private void updateTestCaseResultByValidationResult(String refObjUri,
                                                        String refId,
                                                        String testRequestId,
                                                        ValidationResultInfo validationResultInfo,
                                                        ContextInfo contextInfo) {

    }

    private IGenericClient getClient(String contextType, String serverBaseURL, String username, String password) {
        //TODO: Remove this
        contextType = "R4"; //create enum for this
        serverBaseURL = "https://hapi.fhir.org/baseR4";
        //http://hapi.fhir.org/baseR4
        //https://hapi.fhir.org/baseDstu2
        //https://hapi.fhir.org/baseDstu3
        //http://hapi.fhir.org/search?serverId=home_r4&pretty=true&_summary=&resource=Patient&param.0.0=&param.0.1=&param.0.2=&param.0.3=&param.0.name=birthdate&param.0.type=date&sort_by=&sort_direction=&resource-search-limit=
        //http://localhost:8080/fhir

        FhirContext context;
        switch (contextType) {
            case "D2":
                context = FhirContext.forDstu2();
                break;
            case "D3":
                context = FhirContext.forDstu3();
                break;
            default:
                context = FhirContext.forR4();
        }

        context.getRestfulClientFactory().setConnectTimeout(60 * 1000); //fix configuration
        context.getRestfulClientFactory().setSocketTimeout(60 * 1000); //fix configuration
        IGenericClient client = context.newRestfulGenericClient(serverBaseURL);

        // Add authentication credentials to the client from test Request
        // client.registerInterceptor(new BasicAuthInterceptor(username, password));

        return client;
    }

}
