package swordrows.services.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import swordrows.data.vo.v1.BookVO;
import swordrows.exceptions.RequiredObjectIsNullException;
import swordrows.models.Book;
import swordrows.repositories.BookRepository;
import swordrows.services.BookServices;
import swordrows.unittests.mapper.mocks.MockBook;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServicesTest {
	
	MockBook input;
	
	@InjectMocks
	private BookServices service;
	
	@Mock
	BookRepository repository;

	@BeforeEach
	void setUpMocks() throws Exception {
		input = new MockBook();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindAll() {
		List<Book> list = input.mockEntityList();
		
		
		when(repository.findAll()).thenReturn(list);
		var people = service.findAll();
		assertNotNull(people);
		assertEquals(14, people.size());
		
		var bookOne = people.get(1);
		assertNotNull(bookOne);
		assertNotNull(bookOne.getKey());
		assertNotNull(bookOne.getLinks());
		assertNotNull(bookOne.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test1", bookOne.getAuthor());
		assertEquals("Title Test1", bookOne.getTitle());
		assertNotNull(bookOne.getLaunchDate());
		assertEquals(25D, bookOne.getPrice());
		
		var bookThree = people.get(3);
		assertNotNull(bookThree);
		assertNotNull(bookThree.getKey());
		assertNotNull(bookThree.getLinks());
		assertNotNull(bookThree.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test1", bookOne.getAuthor());
		assertEquals("Title Test1", bookOne.getTitle());
		assertNotNull(bookOne.getLaunchDate());
		assertEquals(25D, bookOne.getPrice());
		
		
		
	}

	@Test
	void testFindByID() {
		Book entity = input.mockEntity(1);
		entity.setId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		var result = service.findByID(1L);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertNotNull(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test1", result.getAuthor());
		assertEquals("Title Test1", result.getTitle());
		assertNotNull(result.getLaunchDate());
		assertEquals(25D, result.getPrice());
	}

	@Test
	void testCreate() {
		Book persisted = input.mockEntity(1);
		persisted.setId(1L);
		
		BookVO vo = input.mockVO(1);
		vo.setKey(1L);
		
		when(repository.save(any(Book.class))).thenReturn(persisted);
		var result = service.create(vo);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertNotNull(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test1", result.getAuthor());
		assertEquals("Title Test1", result.getTitle());
		assertNotNull(result.getLaunchDate());
		assertEquals(25D, result.getPrice());
	}
	
	@Test
	void testCreateNullBook() {
		
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.create(null);
		});
		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		
		assertNotNull(actualMessage.contains(expectedMessage));
		
	}


	@Test
	void testUpdate() {
		Book entity = input.mockEntity(1);
		entity.setId(1L);
		
		Book persisted = entity;
		persisted.setId(1L);
		
		BookVO vo = input.mockVO(1);
		vo.setKey(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		when(repository.save(entity)).thenReturn(persisted);
		
		var result = service.update(vo);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertNotNull(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		
		assertEquals("Author Test1", result.getAuthor());
		assertEquals("Title Test1", result.getTitle());
		assertNotNull(result.getLaunchDate());
		assertEquals(25D, result.getPrice());
	}
	
	
	@Test
	void testUpdateNullBook() {
		
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});
		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		
		assertNotNull(actualMessage.contains(expectedMessage));
		
	}

	@Test
	void testDelete() {
		Book entity = input.mockEntity(1);
		entity.setId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		service.delete(1L);
	}

}
