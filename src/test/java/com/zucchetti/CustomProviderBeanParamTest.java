package com.zucchetti;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import com.zucchetti.model.Operation;
import com.zucchetti.model.Operator;
import com.zucchetti.provider.OperationConverterProvider;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(CustomProviderBeanParamTest.Profile.class)
class CustomProviderBeanParamTest
{
	public enum MyEnum
	{
		ONE, TWO, THREE
	}

	static public class Profile implements QuarkusTestProfile
	{
		@Override
		public Map<String, String> getConfigOverrides()
		{
			return Map.of("quarkus.rest-client.client.url", "http://localhost:${quarkus.http.test-port}/resource");
		}
	}

	@RegisterRestClient(configKey = "client")
	interface Client
	{
		@Path("call-with-bean")
		@GET
		void callWithBean(@BeanParam MultipleFilterRuleBean bean);
	}

	@Path("resource")
	static public class Resource
	{
		@Path("call-with-bean")
		@GET
		public Response callWithBean(@BeanParam MultipleFilterRuleBean bean)
		{
			return Response.ok().build();
		}
	}

	static class MultipleFilterRuleBean
	{
		@OperationParam(value = "int_from_annot")
		@QueryParam("i")
		public Operation<Integer> integer;
		@OperationParam(value = "enum_from_annot", boundedType = MyEnum.class)
		@QueryParam("e")
		public Operation myEnum;
	}

	@Inject
	@RestClient
	Client client;

	/**
	 * I was expecting to extract info in OperationConverterProvider from
	 * OperationParam on MultipleFilterRuleBean#integer (or #myEnum) but
	 * only @BeanParam from client interface is available in my custom
	 * param-converter-provider and I was also expecting annotation on bean
	 * properties.
	 * 
	 * @see OperationConverterProvider
	 */
	@Test
	void test()
	{
		final MultipleFilterRuleBean bean = new MultipleFilterRuleBean();
		bean.integer = Operation.of("int", Operator.of("eq"), 10);
		bean.myEnum = Operation.of("enum", Operator.of("neq"), MyEnum.ONE);
		// I was expecting to extract info in OperationConverterProvider from
		// @OperationParam on MultipleFilterRuleBean#integer (or #myEnum)
		// but only @BeanParam from client interface is available in my
		// custom param-converter-provider and I was also expecting
		// annotation on bean properties
		client.callWithBean(bean);
		assertTrue(true);
	}
}
