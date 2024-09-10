package swordrows.integrationtests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import swordrows.configs.TestConfigs;
import swordrows.data.vo.v1.security.TokenVO;
import swordrows.integrationtests.testcontainers.AbstractIntegrationTest;
import swordrows.integrationtests.vo.AccountCredentialsVO;
import swordrows.integrationtests.vo.BookVO;




@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest{

	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;

	private static BookVO book;

	
	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		book = new BookVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("SwordrowS", "admin234");
		
		var accessToken = given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.body(user)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class)
							.getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}
	
	
	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockBooK();
		
		
		var content =given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.body(book)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		book = persistedBook;
		
		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getTitle());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		
		assertTrue(persistedBook.getId()>0);
		
		assertEquals("J. R. R. Tolkien", persistedBook.getAuthor());
		assertEquals("The Fellowship of the Ring", persistedBook.getTitle());
		assertEquals(11D, persistedBook.getPrice());
	}
	
	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		book.setTitle("The Two Towers");
		
		
		var content =given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.body(book)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		book = persistedBook;
		
		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getTitle());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		
		assertTrue(persistedBook.getId()>0);
		
		assertEquals("J. R. R. Tolkien", persistedBook.getAuthor());
		assertEquals("The Two Towers", persistedBook.getTitle());
		assertEquals(11D, persistedBook.getPrice());
	}

	
	
	
	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockBooK();
		
		var content =given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SWORDROWS)
					.pathParam("id", book.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		
		
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		book = persistedBook;
		
		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getTitle());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		
		assertEquals(book.getId(), persistedBook.getId());
		
		assertEquals("J. R. R. Tolkien", persistedBook.getAuthor());
		assertEquals("The Two Towers", persistedBook.getTitle());
		assertEquals(11D, persistedBook.getPrice());
	}	
	
	@Test
	@Order(4)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		
		given()
			.spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", book.getId())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
						
		
	}	
	
	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		
		var content =given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
								//.as(new TypeRef<List<BookVO>>() {});
		
		List <BookVO> people = objectMapper.readValue(content, new TypeReference<List<BookVO>>() {});
		BookVO persistedBookOne = people.get(0);
		
		assertNotNull(persistedBookOne.getId());
		assertNotNull(persistedBookOne.getAuthor());
		assertNotNull(persistedBookOne.getTitle());
		assertNotNull(persistedBookOne.getLaunchDate());
		assertNotNull(persistedBookOne.getPrice());
		
		assertEquals(1, persistedBookOne.getId());
		
		assertEquals("Michael C. Feathers", persistedBookOne.getAuthor());
		assertEquals("Working effectively with legacy code", persistedBookOne.getTitle());
		assertEquals(49D, persistedBookOne.getPrice());
		
		BookVO persistedBookFour = people.get(4);
	
		assertNotNull(persistedBookFour.getId());
		assertNotNull(persistedBookFour.getAuthor());
		assertNotNull(persistedBookFour.getTitle());
		assertNotNull(persistedBookFour.getLaunchDate());
		assertNotNull(persistedBookFour.getPrice());
		
		assertEquals(5, persistedBookFour.getId());
		
		assertEquals("Steve McConnell", persistedBookFour.getAuthor());
		assertEquals("Code complete", persistedBookFour.getTitle());
		assertEquals(58D, persistedBookFour.getPrice());
	}
	
	@Test
	@Order(5)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		
		
		RequestSpecification specificationWithoutToken;
		specificationWithoutToken = new RequestSpecBuilder()
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		var content =given()
				.spec(specificationWithoutToken)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.when()
					.get()
				.then()
					.statusCode(403);		
			}
	

	private void mockBooK() {
		book.setAuthor("J. R. R. Tolkien");
		book.setTitle("The Fellowship of the Ring");
		book.setLaunchDate(new Date());
		book.setPrice(11D);
	}

}
