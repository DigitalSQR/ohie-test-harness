package com.argusoft.path.tht.dynamicclassgenerator.executor;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.argusoft.path.tht.systemconfiguration.models.dto.ValidationResultInfo;
import org.codehaus.janino.SimpleCompiler;

import java.lang.reflect.Method;
import java.util.List;

import static com.argusoft.path.tht.testprocessmanagement.automationtestcaseexecutionar.testcases.clientregistry.CRWF1TestCase1.getClient;

public class DynamicClassExecutor {

    public static ValidationResultInfo executeDynamicCode(String methodBody, List<String> input) {
        try {
            SimpleCompiler compiler = new SimpleCompiler();
            compiler.cook(methodBody);

            Class<?> compiledClass = compiler.getClassLoader().loadClass("DynamicTestClass");
            Object instance = compiledClass.getDeclaredConstructor().newInstance();

//            // Assuming the dynamically generated class has an "add" method
//            Method addMethod = compiledClass.getDeclaredMethod("add", int.class, int.class);
//            Object result = addMethod.invoke(instance, inputA, inputB);

            // Assuming the dynamically generated class has an "addElementToList" method
            Method executeTestMethod = compiledClass.getDeclaredMethod("executeTestMethod", IGenericClient.class);
            IGenericClient client = getClient("R4","http://hapi.fhir.org/baseR4","root@intrahealth.com","intrahealth");
            Object result = executeTestMethod.invoke(instance, client);


            return new ValidationResultInfo(result.toString());

        } catch (Exception e) {
            throw new RuntimeException("Error executing dynamically generated code", e);
        }
    }

}
