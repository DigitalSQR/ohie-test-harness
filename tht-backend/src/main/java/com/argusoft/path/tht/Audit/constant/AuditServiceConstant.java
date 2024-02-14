package com.argusoft.path.tht.Audit.constant;

import com.argusoft.path.tht.common.configurations.ModelDtoMapper;
import com.argusoft.path.tht.reportmanagement.models.mapper.TestcaseResultMapperImpl;
import com.argusoft.path.tht.testcasemanagement.models.mapper.ComponentMapperImpl;
import com.argusoft.path.tht.testcasemanagement.models.mapper.SpecificationMapperImpl;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseMapperImpl;
import com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseOptionMapperImpl;
import com.argusoft.path.tht.testprocessmanagement.models.mapper.TestRequestMapperImpl;
import com.argusoft.path.tht.usermanagement.models.mapper.UserMapperImpl;


public final class AuditServiceConstant {


        public enum EntityType {
            USER(com.argusoft.path.tht.usermanagement.models.entity.UserEntity.class,com.argusoft.path.tht.usermanagement.models.mapper.UserMapperImpl.class),
            COMPONENT(com.argusoft.path.tht.testcasemanagement.models.entity.ComponentEntity.class,com.argusoft.path.tht.testcasemanagement.models.mapper.ComponentMapperImpl.class),
            SPECIFICATION(com.argusoft.path.tht.testcasemanagement.models.entity.SpecificationEntity.class,com.argusoft.path.tht.testcasemanagement.models.mapper.SpecificationMapperImpl.class),
            TESTCASE(com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseEntity.class,com.argusoft.path.tht.testcasemanagement.models.mapper.SpecificationMapperImpl.class),
            TESTCASE_OPTION(com.argusoft.path.tht.testcasemanagement.models.entity.TestcaseOptionEntity.class,com.argusoft.path.tht.testcasemanagement.models.mapper.TestcaseOptionMapperImpl.class),
            TESTCASE_RESULT(com.argusoft.path.tht.reportmanagement.models.entity.TestcaseResultEntity.class,com.argusoft.path.tht.reportmanagement.models.mapper.TestcaseResultMapperImpl.class),
            TEST_REQUEST(com.argusoft.path.tht.testprocessmanagement.models.entity.TestRequestEntity.class,com.argusoft.path.tht.testprocessmanagement.models.mapper.TestRequestMapperImpl.class);

            private final Class<?> entityClass;
            private final Class<? extends ModelDtoMapper<?,?>> mapperImplClass;

            EntityType(Class<?> entityClass,Class<? extends ModelDtoMapper<?, ?>> mapperImplClass) {
                this.entityClass = entityClass;
                this.mapperImplClass = mapperImplClass;
            }

            public Class<?> getEntityClass() {
                return entityClass;
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
                    default -> throw new IllegalArgumentException("Unsupported entity type: " + entityType);
                };

            }
//            public <E> List<Object> modelToDto(List<E> entity) {
//                try {
//                    ModelDtoMapper<E, ?> mapper =(ModelDtoMapper<E, ?>) mapperImplClass.getDeclaredConstructor().newInstance();
//                    return (List<Object>) mapper.modelToDto(entity);
//                } catch (Exception e) {
//                    throw new RuntimeException("Error mapping entity to DTO", e);
//                }
//            }
        }

    }

