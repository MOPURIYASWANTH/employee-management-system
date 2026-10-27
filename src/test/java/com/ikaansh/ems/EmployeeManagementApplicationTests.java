
package com.ikaansh.ems;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeeManagementApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void applicationClassLoads() {
		// Verifies that the Spring application context loads.
	}

	@Test
	void signupWithValidDetailsReturnsCreatedAndJwt() throws Exception {
		JsonNode user = signup("signup-test@example.com");

		assertTrue(user.path("accessToken").asText().length() > 0);
		assertTrue(user.path("user").path("id").asLong() > 0);
	}

	@Test
	void signupWithInvalidEmailReturnsBadRequest() throws Exception {
		String request = """
				{
				  "fullName": "Test User",
				  "email": "invalid-email",
				  "password": "StrongPass123"
				}
				""";

		mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isBadRequest());
	}

	@Test
	void loginWithValidCredentialsReturnsJwt() throws Exception {
		String email = "login-test@example.com";
		signup(email);

		String request = """
				{
				  "email": "login-test@example.com",
				  "password": "StrongPass123"
				}
				""";

		mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isOk()).andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.user.email").value(email));
	}

	@Test
	void employeesWithoutTokenReturnUnauthorized() throws Exception {
		mockMvc.perform(get("/api/employees")).andExpect(status().isUnauthorized());
	}

	@Test
	void employeesWithInvalidTokenReturnUnauthorized() throws Exception {
		mockMvc.perform(get("/api/employees").header("Authorization", "Bearer invalid-token"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void authenticatedUserCanCreateEmployee() throws Exception {
		String token = signup("create-test@example.com").path("accessToken").asText();

		createEmployee(token, "Ananya Rao", "ananya@example.com").andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber()).andExpect(jsonPath("$.name").value("Ananya Rao"))
				.andExpect(jsonPath("$.email").value("ananya@example.com"))
				.andExpect(jsonPath("$.department").value("Engineering"));
	}

	@Test
	void authenticatedUserCanListEmployees() throws Exception {
		String token = signup("list-test@example.com").path("accessToken").asText();

		createEmployee(token, "List User Employee", "list-employee@example.com").andExpect(status().isCreated());

		mockMvc.perform(get("/api/employees").header("Authorization", "Bearer " + token)).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray()).andExpect(jsonPath("$.content.length()").value(1))
				.andExpect(jsonPath("$.content[0].name").value("List User Employee"));
	}

	@Test
	void authenticatedUserCanRetrieveTheirEmployee() throws Exception {
		String token = signup("retrieve-test@example.com").path("accessToken").asText();

		MvcResult result = createEmployee(token, "Rahul Kumar", "rahul@example.com").andExpect(status().isCreated())
				.andReturn();

		long id = objectMapper.readTree(result.getResponse().getContentAsString()).path("id").asLong();

		mockMvc.perform(get("/api/employees/{id}", id).header("Authorization", "Bearer " + token))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.name").value("Rahul Kumar"));
	}

	@Test
	void authenticatedUserCanUpdateTheirEmployee() throws Exception {
		String token = signup("update-test@example.com").path("accessToken").asText();

		MvcResult result = createEmployee(token, "Original Name", "original@example.com")
				.andExpect(status().isCreated()).andReturn();

		long id = objectMapper.readTree(result.getResponse().getContentAsString()).path("id").asLong();

		String updateRequest = """
				{
				  "name": "Updated Name",
				  "email": "updated@example.com",
				  "role": "Senior Developer",
				  "department": "Engineering",
				  "hireDate": "2026-10-01"
				}
				""";

		mockMvc.perform(put("/api/employees/{id}", id).header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content(updateRequest)).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Updated Name"))
				.andExpect(jsonPath("$.email").value("updated@example.com"));
	}

	@Test
	void authenticatedUserCanDeleteTheirEmployee() throws Exception {
		String token = signup("delete-test@example.com").path("accessToken").asText();

		MvcResult result = createEmployee(token, "Delete Me", "delete-me@example.com").andExpect(status().isCreated())
				.andReturn();

		long id = objectMapper.readTree(result.getResponse().getContentAsString()).path("id").asLong();

		mockMvc.perform(delete("/api/employees/{id}", id).header("Authorization", "Bearer " + token))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/employees/{id}", id).header("Authorization", "Bearer " + token))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void userCannotAccessAnotherUsersEmployee() throws Exception {
		String firstToken = signup("owner-test@example.com").path("accessToken").asText();

		String secondToken = signup("other-user@example.com").path("accessToken").asText();

		MvcResult result = createEmployee(firstToken, "Private Employee", "private@example.com")
				.andExpect(status().isCreated()).andReturn();

		long id = objectMapper.readTree(result.getResponse().getContentAsString()).path("id").asLong();

		mockMvc.perform(get("/api/employees/{id}", id).header("Authorization", "Bearer " + secondToken))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void canFilterAndPaginateEmployees() throws Exception {
		String token = signup("pagination-test@example.com").path("accessToken").asText();

		createEmployee(token, "Engineering Employee", "eng@example.com").andExpect(status().isCreated());

		mockMvc.perform(get("/api/employees").param("page", "0").param("size", "5").param("department", "Engineering")
				.header("Authorization", "Bearer " + token)).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray()).andExpect(jsonPath("$.content.length()").value(1))
				.andExpect(jsonPath("$.content[0].department").value("Engineering"));
	}

	private JsonNode signup(String email) throws Exception {
		String request = """
				{
				  "fullName": "Automated Test User",
				  "email": "%s",
				  "password": "StrongPass123"
				}
				""".formatted(email);

		MvcResult result = mockMvc
				.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(request))
				.andExpect(status().isCreated()).andReturn();

		return objectMapper.readTree(result.getResponse().getContentAsString());
	}

	private org.springframework.test.web.servlet.ResultActions createEmployee(String token, String name, String email)
			throws Exception {

		String request = """
				{
				  "name": "%s",
				  "email": "%s",
				  "role": "Software Engineer",
				  "department": "Engineering",
				  "hireDate": "2026-10-01"
				}
				""".formatted(name, email);

		return mockMvc.perform(post("/api/employees").header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON).content(request));
	}
}
