package swordrows.integrationtests.controller.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

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
public class BookControllerXMLTest extends AbstractIntegrationTest{

	private static RequestSpecification specification;
	private static XmlMapper objectMapper;

	private static BookVO book;

	
	@BeforeAll
	public static void setup() {
		objectMapper = new XmlMapper();
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
					.contentType(TestConfigs.CONTENT_TYPE_XML)
					.accept(TestConfigs.CONTENT_TYPE_XML)
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
		mockBook();
		
		
		var content =given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(book)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		book = objectMapper.readValue(content, BookVO.class);
		
		
		assertNotNull(book);
		assertNotNull(book.getId());
		assertNotNull(book.getAuthor());
		assertNotNull(book.getTitle());
		assertNotNull(book.getLaunchDate());
		assertNotNull(book.getPrice());
		
		assertTrue(book.getId()>0);
		
		assertEquals("J. R. R. Tolkien", book.getAuthor());
		assertEquals("The Fellowship of the Ring", book.getTitle());
		assertEquals(11D, book.getPrice());
	}
	
	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		book.setTitle("The Two Towers");
		
		
		var content =given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(book)
					.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		BookVO book = objectMapper.readValue(content, BookVO.class);
		book = book;
		
		assertNotNull(book);
		assertNotNull(book.getId());
		assertNotNull(book.getAuthor());
		assertNotNull(book.getTitle());
		assertNotNull(book.getLaunchDate());
		assertNotNull(book.getPrice());
		
		assertTrue(book.getId()>0);
		
		assertEquals("J. R. R. Tolkien", book.getAuthor());
		assertEquals("The Two Towers", book.getTitle());
		assertEquals(11D, book.getPrice());
	}

	
	
	
	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockBook();
		
		var content =given()
				.spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SWORDROWS)
					.pathParam("id", book.getId())
					.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		
		
		
		BookVO book = objectMapper.readValue(content, BookVO.class);
		book = book;
		
		assertNotNull(book);
		assertNotNull(book.getId());
		assertNotNull(book.getAuthor());
		assertNotNull(book.getTitle());
		assertNotNull(book.getLaunchDate());
		assertNotNull(book.getPrice());
		
		assertEquals(book.getId(), book.getId());
		
		assertEquals("J. R. R. Tolkien", book.getAuthor());
		assertEquals("The Two Towers", book.getTitle());
		assertEquals(11D, book.getPrice());
	}	
	
	@Test
	@Order(4)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		
		given()
			.spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
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
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
								//.as(new TypeRef<List<BookVO>>() {});
		
		List<BookVO> books = objectMapper.readValue(content, new TypeReference<List<BookVO>>() {});
		BookVO bookOne = books.get(0);
		
			assertNotNull(bookOne.getId());
			assertNotNull(bookOne.getAuthor());
			assertNotNull(bookOne.getTitle());
			assertNotNull(bookOne.getLaunchDate());
			assertNotNull(bookOne.getPrice());
			
			assertEquals(1, bookOne.getId());
			
			assertEquals("Michael C. Feathers", bookOne.getAuthor());
			assertEquals("Working effectively with legacy code", bookOne.getTitle());
			assertEquals(49D, bookOne.getPrice());
			
			BookVO bookFour = books.get(4);
		
			assertNotNull(bookFour.getId());
			assertNotNull(bookFour.getAuthor());
			assertNotNull(bookFour.getTitle());
			assertNotNull(bookFour.getLaunchDate());
			assertNotNull(bookFour.getPrice());
			
			assertEquals(5, bookFour.getId());
			
			assertEquals("Steve McConnell", bookFour.getAuthor());
			assertEquals("Code complete", bookFour.getTitle());
			assertEquals(58D, bookFour.getPrice());
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
				.contentType(TestConfigs.CONTENT_TYPE_XML)
					.when()
					.get()
				.then()
					.statusCode(403);		
			}
	

	private void mockBook() {
		book.setAuthor("J. R. R. Tolkien");
		book.setTitle("The Fellowship of the Ring");
		book.setLaunchDate(new Date());
		book.setPrice(11D);	
		}
}
