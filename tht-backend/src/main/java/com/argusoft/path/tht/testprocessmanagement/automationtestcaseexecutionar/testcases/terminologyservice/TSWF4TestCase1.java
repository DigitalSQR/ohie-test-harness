package com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.terminologyservice;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.argusoft.path.tht.systemconfiguration.constant.ErrorLevel;
import com.argusoft.path.tht.systemconfiguration.constant.ValidateConstant;
import com.argusoft.path.tht.systemconfiguration.exceptioncontroller.exception.OperationFailedException;
import com.argusoft.path.tht.systemconfiguration.models.dto.ContextInfo;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import com.argusoft.path.tht.systemconfiguration.utils.FHIRUtils;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.TestCase;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class TSWF4TestCase1 implements TestCase {

    public static final Logger LOGGER = LoggerFactory.getLogger(TSWF4TestCase1.class);

    @Override
    public ValidationResultInfo test(Map<String, IGenericClient> iGenericClientMap, ContextInfo contextInfo) throws Exception {

        try {
            IGenericClient client = iGenericClientMap.get(ComponentServiceConstants.COMPONENT_TERMINOLOGY_SERVICE_ID);
            String testCaseName = this.getClass().getSimpleName();
            if (client == null) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get IGenericClient");
            }

            LOGGER.info("Start testing " + testCaseName);

            String uniqueId = UUID.randomUUID().toString();

            String sourceCode1 = "abcd" + uniqueId;
            String sourceUrl1 = "http://example.com/SNOMED-CT" + UUID.randomUUID();
            CodeSystem codeSystem1 = FHIRUtils.createCodeSystem(sourceUrl1, "2.0", sourceCode1, "Systematized Nomenclature of Medicine - Clinical Terms", "ACTIVE",
                    "International Health Terminology Standards Development Organisation", "COMPLETE", "9876", "Clinical finding", "Another definition here");

            MethodOutcome outcome1 = client.create()
                    .resource(codeSystem1)
                    .execute();

            // check if codeSystem got created
            if (!outcome1.getCreated()) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create codeSystem");
            }

            String targetCode1 = "1234" + uniqueId;
            String targetUrl1 = "http://example.com/ICD-10" + UUID.randomUUID();
            CodeSystem codeSystem2 = FHIRUtils.createCodeSystem(targetUrl1, "1.0", targetCode1, "International Classification of Diseases, 10th Edition", "ACTIVE",
                    "World Health Organization", "COMPLETE", "A001", "Infectious and parasitic diseases", "Some definition here");

            MethodOutcome outcome2 = client.create()
                    .resource(codeSystem2)
                    .execute();

            // check if codeSystem got created
            if (!outcome2.getCreated()) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create codeSystem");
            }

            String sourceCode2 = "pqrs" + uniqueId;
            String sourceUrl2 = "http://example.com/LOINC" + UUID.randomUUID();
            CodeSystem codeSystem3 = FHIRUtils.createCodeSystem(sourceUrl2, "2.0", sourceCode2, "Systematized Nomenclature of Medicine - Clinical Terms", "ACTIVE",
                    "International Health Terminology Standards Development Organisation", "COMPLETE", "9876", "Clinical finding", "Another definition here");

            MethodOutcome outcome3 = client.create()
                    .resource(codeSystem3)
                    .execute();

            // check if codeSystem got created
            if (!outcome3.getCreated()) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create codeSystem");
            }

            String targetCode2 = "6789" + uniqueId;
            String targetUrl2 = "http://example.com/CPT" + UUID.randomUUID();
            CodeSystem codeSystem4 = FHIRUtils.createCodeSystem(targetUrl2, "1.0", targetCode2, "International Classification of Diseases, 10th Edition", "ACTIVE",
                    "World Health Organization", "COMPLETE", "A001", "Infectious and parasitic diseases", "Some definition here");

            MethodOutcome outcome4 = client.create()
                    .resource(codeSystem4)
                    .execute();

            // check if codeSystem got created
            if (!outcome4.getCreated()) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create codeSystem");
            }

            LOGGER.info("Creating coneptMaps");
            String name1 = "MyConceptMap"+ uniqueId;
            String name2 = "MyConceptMap2"+ uniqueId;

            String url = "http://example.com/testing/myconceptmap1" + "/" + uniqueId;
            String url2 = "http://example.com/testing/myconceptmap2" + "/" + uniqueId;

            ConceptMap conceptMap = FHIRUtils.createConceptMap(name1, url,"ACTIVE", sourceUrl1, targetUrl1, sourceCode1, targetCode1,"Blood Pressure","BP");

            MethodOutcome outcome5 = client.create()
                    .resource(conceptMap)
                    .execute();

            // check if conceptMap got created
            if (!outcome5.getCreated()) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create conceptMap");
            }

            ConceptMap conceptMap2 = FHIRUtils.createConceptMap(name2, url2,"ACTIVE", sourceUrl2, targetUrl2, sourceCode2, targetCode2,"Asthma","AS");


            MethodOutcome outcome6 = client.create()
                    .resource(conceptMap2)
                    .execute();

            // check if conceptMap got created
            if (!outcome6.getCreated()) {
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to create conceptMap");
            }


            // Search by Url
            LOGGER.info("Search concept map by URL");
            Bundle bundle1 = client.search()
                    .forResource(ConceptMap.class)
                    .where(new StringClientParam("url").matchesExactly().value(url))
                    .returnBundle(Bundle.class)
                    .execute();

            List<ConceptMap> maps1 = FHIRUtils.processBundle(ConceptMap.class, bundle1);
            if(maps1.size() != 1){
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get concept map by url");
            }

            // Search by Status and name
            LOGGER.info("Search concept map by status and name");
            Bundle bundle2 = client.search()
                    .forResource(ConceptMap.class)
                    .where(new StringClientParam("status").matchesExactly().value("active"))
                    .and(new StringClientParam("name").contains().value(uniqueId))
                    .returnBundle(Bundle.class)
                    .execute();

            List<ConceptMap> maps2 = FHIRUtils.processBundle(ConceptMap.class, bundle2);
            if(maps2.size() != 2){
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get concept map by status and name");
            }

//          Search by source code
            LOGGER.info("Search concept map by source code");
            Bundle bundle3 = client.search()
                    .forResource(ConceptMap.class)
                    .where(new StringClientParam("source-code").matchesExactly().value(sourceCode1))
                    .returnBundle(Bundle.class)
                    .execute();

            List<ConceptMap> maps3 = FHIRUtils.processBundle(ConceptMap.class, bundle3);
            if(maps3.size() != 1){
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get concept map by source code");
            }

//            Search by target code
            LOGGER.info("Search concept map by target code");
            Bundle bundle4 = client.search()
                    .forResource(ConceptMap.class)
                    .where(new StringClientParam("target-code").matchesExactly().value(targetCode1))
                    .returnBundle(Bundle.class)
                    .execute();

            List<ConceptMap> maps4 = FHIRUtils.processBundle(ConceptMap.class, bundle4);
            if(maps4.size() != 1){
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get concept map by target code");
            }

//             Search by source uri
            LOGGER.info("Search concept map by source URL");
            Bundle bundle5 = client.search()
                    .forResource(ConceptMap.class)
                    .where(new StringClientParam("source-system").matchesExactly().value(sourceUrl1))
                    .returnBundle(Bundle.class)
                    .execute();

            List<ConceptMap> maps5 = FHIRUtils.processBundle(ConceptMap.class, bundle5);
            if(maps5.size() != 1){
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get concept map by source uri");
            }

            // Search by target uri
            LOGGER.info("Search concept map by targetURL");
            Bundle bundle6 = client.search()
                    .forResource(ConceptMap.class)
                    .where(new StringClientParam("target-system").matchesExactly().value(targetUrl1))
                    .returnBundle(Bundle.class)
                    .execute();

            List<ConceptMap> maps6 = FHIRUtils.processBundle(ConceptMap.class, bundle6);
            if(maps6.size() != 1){
                return new ValidationResultInfo(ErrorLevel.ERROR, "Failed to get concept map by target uri");
            }
            LOGGER.info("Test case Passed");
            return new ValidationResultInfo(ErrorLevel.OK, "Passed");

        }
        catch(Exception ex){
            LOGGER.error(ValidateConstant.EXCEPTION + TSWF4TestCase1.class.getSimpleName(), ex);
            throw new OperationFailedException(ex.getMessage(), ex);
        }

    }
}
