package edu.bellevue.cis530.week5;

import edu.bellevue.cis530.week5.repository.StudentProfileRepository;
import edu.bellevue.cis530.week5.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:cis530_week5_test;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa",
		"spring.datasource.password="
})
class Cis530Week5ApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private StudentProfileRepository studentProfileRepository;

	@BeforeEach
	void clearDatabase() {
		studentRepository.deleteAll();
		studentProfileRepository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void createsStudentAndProfileWithoutRecursiveJson() throws Exception {
		mockMvc.perform(post("/api/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content(validStudent()))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.profile.id").isNumber())
				.andExpect(jsonPath("$.profile.major").value("Computer Science"))
				.andExpect(jsonPath("$.profile.student").doesNotExist());

		assertEquals(1, studentRepository.count());
		assertEquals(1, studentProfileRepository.count());
	}

	@Test
	void validatesStudentAndNestedProfile() throws Exception {
		mockMvc.perform(post("/api/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "firstName": "",
								  "lastName": "Morgan",
								  "email": "not-an-email",
								  "profile": {
								    "phoneNumber": "",
								    "address": "1 Campus Way",
								    "major": "Biology",
								    "dateOfBirth": "2999-01-01"
								  }
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.validationErrors.firstName").exists())
				.andExpect(jsonPath("$.validationErrors.email").exists())
				.andExpect(jsonPath("$.validationErrors['profile.phoneNumber']").exists())
				.andExpect(jsonPath("$.validationErrors['profile.dateOfBirth']").exists());
	}

	@Test
	void updatesAndDeletesProfileWithStudent() throws Exception {
		String response = mockMvc.perform(post("/api/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content(validStudent()))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();
		String id = tools.jackson.databind.json.JsonMapper.builder().build()
				.readTree(response).get("id").asText();

		mockMvc.perform(put("/api/students/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(validStudent().replace("Computer Science", "Mathematics")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.profile.major").value("Mathematics"));
		assertEquals(1, studentProfileRepository.count());

		mockMvc.perform(delete("/api/students/" + id))
				.andExpect(status().isNoContent());
		assertEquals(0, studentRepository.count());
		assertEquals(0, studentProfileRepository.count());
	}

	@Test
	void returnsStructuredNotFoundResponse() throws Exception {
		mockMvc.perform(get("/api/students/987654"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message", containsString("987654")))
				.andExpect(jsonPath("$.path").value("/api/students/987654"));
	}

	private static String validStudent() {
		return """
				{
				  "firstName": "Taylor",
				  "lastName": "Morgan",
				  "email": "taylor.morgan@example.edu",
				  "profile": {
				    "phoneNumber": "555-0100",
				    "address": "1 Campus Way",
				    "major": "Computer Science",
				    "dateOfBirth": "2003-06-15"
				  }
				}
				""";
	}
}
