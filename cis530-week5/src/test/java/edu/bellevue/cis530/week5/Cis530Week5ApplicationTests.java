package edu.bellevue.cis530.week5;

import edu.bellevue.cis530.week5.repository.StudentProfileRepository;
import edu.bellevue.cis530.week5.repository.StudentRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

	@Autowired
	private JdbcTemplate jdbcTemplate;

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
				.andExpect(jsonPath("$.major").value("Computer Science"))
				.andExpect(jsonPath("$.gpa").value(3.75))
				.andExpect(jsonPath("$.profile.state").value("NE"))
				.andExpect(jsonPath("$.profile.student").doesNotExist());

		assertEquals(1, studentRepository.count());
		assertEquals(1, studentProfileRepository.count());
	}

	@Test
	void createsTablesWithRequiredColumnNamesAndTypes() {
		assertColumn("students", "id", "bigint", null, null);
		assertColumn("students", "first_name", "character varying", null, null);
		assertColumn("students", "last_name", "character varying", null, null);
		assertColumn("students", "email", "character varying", null, null);
		assertColumn("students", "major", "character varying", null, null);
		assertColumn("students", "gpa", "numeric", 3, 2);
		assertColumn("students", "enrollment_year", "integer", null, null);
		assertColumn("students", "profile_id", "bigint", null, null);

		assertColumn("student_profiles", "id", "bigint", null, null);
		assertColumn("student_profiles", "phone", "character varying", null, null);
		assertColumn("student_profiles", "address", "character varying", null, null);
		assertColumn("student_profiles", "city", "character varying", null, null);
		assertColumn("student_profiles", "state", "character varying", null, null);
		assertColumn("student_profiles", "zip", "character varying", null, null);
		assertColumn("student_profiles", "emergency_contact_name", "character varying", null, null);
		assertColumn("student_profiles", "emergency_contact_phone", "character varying", null, null);
		assertIdentity("students", "id");
		assertIdentity("student_profiles", "id");
		assertPrimaryKey("students", "id");
		assertPrimaryKey("student_profiles", "id");
		assertColumnLength("student_profiles", "state", 2);
		assertColumnLength("student_profiles", "zip", 5);
		assertUnique("students", "email");
		assertForeignKey("students", "profile_id", "student_profiles", "id");
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
								  "major": "",
								  "gpa": 5.25,
								  "enrollmentYear": 2025,
								  "profile": {
								    "phone": "",
								    "address": "1 Campus Way",
								    "city": "Omaha",
								    "state": "Nebraska",
								    "zip": "123456",
								    "emergencyContactName": "Morgan Taylor",
								    "emergencyContactPhone": "555-0101"
								  }
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.validationErrors.firstName").exists())
				.andExpect(jsonPath("$.validationErrors.email").exists())
				.andExpect(jsonPath("$.validationErrors.gpa").exists())
				.andExpect(jsonPath("$.validationErrors['profile.phone']").exists())
				.andExpect(jsonPath("$.validationErrors['profile.state']").exists())
				.andExpect(jsonPath("$.validationErrors['profile.zip']").exists());
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
				.andExpect(jsonPath("$.major").value("Mathematics"));
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
				  "major": "Computer Science",
				  "gpa": 3.75,
				  "enrollmentYear": 2025,
				  "profile": {
				    "phone": "555-0100",
				    "address": "1 Campus Way",
				    "city": "Omaha",
				    "state": "NE",
				    "zip": "68123",
				    "emergencyContactName": "Morgan Taylor",
				    "emergencyContactPhone": "555-0101"
				  }
				}
				""";
	}

	private void assertColumn(
			String table, String column, String expectedType, Integer expectedPrecision, Integer expectedScale) {
		var metadata = jdbcTemplate.queryForMap("""
				select data_type, numeric_precision, numeric_scale
				from information_schema.columns
				where lower(table_name) = ? and lower(column_name) = ?
				""", table, column);

		assertEquals(expectedType, metadata.get("data_type").toString().toLowerCase(java.util.Locale.ROOT),
				table + "." + column + " type");
		if (expectedPrecision != null) {
			assertEquals(expectedPrecision, ((Number) metadata.get("numeric_precision")).intValue(),
					table + "." + column + " precision");
			assertEquals(expectedScale, ((Number) metadata.get("numeric_scale")).intValue(),
					table + "." + column + " scale");
		}
	}

	private void assertColumnLength(String table, String column, int expectedLength) {
		Integer length = jdbcTemplate.queryForObject("""
				select character_maximum_length
				from information_schema.columns
				where lower(table_name) = ? and lower(column_name) = ?
				""", Integer.class, table, column);
		assertEquals(expectedLength, length, table + "." + column + " length");
	}

	private void assertIdentity(String table, String column) {
		String isIdentity = jdbcTemplate.queryForObject("""
				select is_identity
				from information_schema.columns
				where lower(table_name) = ? and lower(column_name) = ?
				""", String.class, table, column);
		assertEquals("YES", isIdentity, table + "." + column + " identity");
	}

	private void assertPrimaryKey(String table, String column) {
		assertConstraintOnColumn(table, column, "primary key");
	}

	private void assertUnique(String table, String column) {
		assertConstraintOnColumn(table, column, "unique");
	}

	private void assertConstraintOnColumn(String table, String column, String constraintType) {
		Integer count = jdbcTemplate.queryForObject("""
				select count(*)
				from information_schema.table_constraints tc
				join information_schema.key_column_usage kcu
				  on tc.constraint_catalog = kcu.constraint_catalog
				 and tc.constraint_schema = kcu.constraint_schema
				 and tc.constraint_name = kcu.constraint_name
				where lower(tc.table_name) = ?
				  and lower(tc.constraint_type) = ?
				  and lower(kcu.column_name) = ?
				""", Integer.class, table, constraintType, column);
		assertTrue(count != null && count > 0,
				table + "." + column + " should have a " + constraintType + " constraint");
	}

	private void assertForeignKey(String table, String column, String referencedTable, String referencedColumn) {
		Boolean found = jdbcTemplate.execute((ConnectionCallback<Boolean>) connection -> {
			try (var keys = connection.getMetaData().getImportedKeys(
					connection.getCatalog(), null, table.toUpperCase(java.util.Locale.ROOT))) {
				while (keys.next()) {
					if (column.equalsIgnoreCase(keys.getString("FKCOLUMN_NAME"))
							&& referencedTable.equalsIgnoreCase(keys.getString("PKTABLE_NAME"))
							&& referencedColumn.equalsIgnoreCase(keys.getString("PKCOLUMN_NAME"))) {
						return true;
					}
				}
				return false;
			}
		});
		assertTrue(Boolean.TRUE.equals(found),
				table + "." + column + " should reference " + referencedTable + "." + referencedColumn);
	}
}
