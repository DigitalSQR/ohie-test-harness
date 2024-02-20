package com.argusoft.path.tht.Audit.constant;

import com.argusoft.path.tht.common.configurations.ModelDtoMapper;
import com.argusoft.path.tht.reportmanagement.constant.TestcaseResultServiceConstants;
import com.argusoft.path.tht.reportmanagement.models.mapper.TestcaseResultMapperImpl;
import com.argusoft.path.tht.testcasemanagement.constant.ComponentServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.SpecificationServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseOptionServiceConstants;
import com.argusoft.path.tht.testcasemanagement.constant.TestcaseServiceConstants;
import com.argusoft.path.tht.testcasemanagement.models.mapper.ComponentMapperImpl;
import com.argusoft.path.tht.testcasemanagement.models.mapper.SpecificationMapperImpl;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseMapperImpl;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseOptionMapperImpl;
import com.argusoft.path.tht.testprocessmanagement.constant.TestRequestServiceConstants;
import com.argusoft.path.tht.testprocessmanagement.models.mapper.TestRequestMapperImpl;
import com.argusoft.path.tht.usermanagement.constant.UserServiceConstants;
import com.argusoft.path.tht.usermanagement.models.mapper.UserMapperImpl;

import java.security.InvalidParameterException;
import java.util.Arrays;


public final class AuditServiceConstant {


        public enum EntityType {
            USER(com.argusoft.path.tht.usermanagement.models.entity.UserEntity.class,com.argusoft.path.tht.usermanagement.models.mapper.UserMapperImpl.class, UserServiceConstants.USER_REF_OBJ_URI),
            COMPONENT(com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity.class,com.argusoft.path.tht.testcasemanagement.models.mapper.ComponentMapperImpl.class, ComponentServiceConstants.COMPONENT_REF_OBJ_URI),
            SPECIFICATION(com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity.class,com.argusoft.path.tht.testcasemanagement.models.mapper.SpecificationMapperImpl.class, SpecificationServiceConstants.SPECIFICATION_REF_OBJ_URI),
            TESTCASE(com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity.class,com.argusoft.path.tht.testcasemanagement.models.mapper.SpecificationMapperImpl.class, TestcaseServiceConstants.TESTCASE_REF_OBJ_URI),
            TESTCASE_OPTION(com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity.class,com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseOptionMapperImpl.class, TestcaseOptionServiceConstants.TESTCASE_OPTION_REF_OBJ_URI),
            TESTCASE_RESULT(com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity.class,com.argusoft.path.tht.reportmanagement.models.mapper.TestcaseResultMapperImpl.class, TestcaseResultServiceConstants.TESTCASE_RESULT_REF_OBJ_URI),
            TEST_REQUEST(com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity.class,com.argusoft.path.tht.testprocessmanagement.models.mapper.TestRequestMapperImpl.class, TestRequestServiceConstants.TEST_REQUEST_REF_OBJ_URI);

            private final Class<?> entityClass;
            private final Class<? extends ModelDtoMapper<?,?>> mapperImplClass;

            private final String refObjectUri;

            EntityType(Class<?> entityClass,Class<? extends ModelDtoMapper<?, ?>> mapperImplClass, String refObjectUri) {
                this.entityClass = entityClass;
                this.mapperImplClass = mapperImplClass;
                this.refObjectUri = refObjectUri;
            }

            public static EntityType getEntityTypeBasedOnRefObjectUri(String refObjectUri) {
                return Arrays.stream(EntityType.values())
                        .filter(entityType -> entityType.refObjectUri.equals(refObjectUri))
                        .findFirst()
                        .orElseThrow(() -> new InvalidParameterException("No entity type found for refObjectUri: " + refObjectUri));
            }


            public Class<?> getEntityClass() {
                return entityClass;
            }

            public String getRefObjectUri() {
                return refObjectUri;
            }

            public Class<? extends ModelDtoMapper<?, ?>> getMapperImplClass() {
                return mapperImplClass;
            }

            public ModelDtoMapper createMapperObject(EntityType entityType){
                return switch (entityType) {
                    case USER -> new UserMapperImpl();
                    case COMPONENT -> new ComponentMapperImpl();
                    case SPECIFICATION -> new SpecificationMapperImpl();
                    case TESTCASE -> new TestcaseMapperImpl();
                    case TESTCASE_OPTION -> new TestcaseOptionMapperImpl();
                    case TESTCASE_RESULT -> new TestcaseResultMapperImpl();
                    case TEST_REQUEST -> new TestRequestMapperImpl();
                    default -> throw new InvalidParameterException("Unsupported entity type: " + entityType);
                };

            }
        }

    }

