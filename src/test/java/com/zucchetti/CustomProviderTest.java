package com.zucchetti;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import com.zucchetti.model.Operation;
import com.zucchetti.model.Operator;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestHTTPEndpoint(CustomProviderTest.TestResource.class)
@TestProfile(CustomProviderTest.Profile.class)
class CustomProviderTest
{
	public enum MyEnum
	{
		ONE, TWO, THREE
	}

	@RegisterRestClient(configKey = "test-client")
	interface TestClient
	{
		@Path("list-of-int")
		@GET
		String listOfInteger(
				@OperationParam(value = "int", boundedType = Integer.class) @QueryParam("l") List<Operation<Integer>> list);

		@Path("list-of-enum")
		@GET
		String listOfEnum(
				@OperationParam(value = "enum", boundedType = Integer.class) @QueryParam("l") List<Operation<Integer>> list);
	}

	@Path("test")
	static public class TestResource
	{
		@Path("list-of-int")
		@GET
		public String listOfInt(@OperationParam("int") @QueryParam("l") List<Operation<Integer>> list)
		{
			return "list-of-int";
		}

		@Path("list-of-enum")
		@GET
		public String listOfenum(@OperationParam("enum") @QueryParam("l") List<Operation<MyEnum>> list)
		{
			return "list-of-enum";
		}
	}

	static public class Profile implements QuarkusTestProfile
	{
		@Override
		public Map<String, String> getConfigOverrides()
		{
			return Map.of("quarkus.rest-client.t.url", "http://localhost:${quarkus.http.test-port}/test");
		}
	}

	@Inject
	@RestClient
	TestClient testClient;

	@Test
	void test()
	{
		testClient.listOfInteger(
				List.of(Operation.of("name", Operator.of("eq"), 10), Operation.of("name", Operator.of("neq"), 20)));
		assertTrue(true);
	}
}
