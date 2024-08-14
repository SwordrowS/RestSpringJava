package swordrows.services.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.*;
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

import swordrows.data.vo.v1.PersonVO;
import swordrows.exceptions.RequiredObjectIsNullException;
import swordrows.models.Person;
import swordrows.repositories.PersonRepository;
import swordrows.services.PersonServices;
import swordrows.unittests.mapper.mocks.MockPerson;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServicesTest {
	
	MockPerson input;
	
	@InjectMocks
	private PersonServices service;
	
	@Mock
	PersonRepository repository;

	@BeforeEach
	void setUpMocks() throws Exception {
		input = new MockPerson();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindAll() {
		List<Person> list = input.mockEntityList();
		
		
		when(repository.findAll()).thenReturn(list);
		var people = service.findAll();
		assertNotNull(people);
		assertEquals(14, people.size());
		
		var personOne = people.get(1);
		assertNotNull(personOne);
		assertNotNull(personOne.getKey());
		assertNotNull(personOne.getLinks());
		assertNotNull(personOne.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
		assertEquals("Address Test1", personOne.getAddress());
		assertEquals("First Name Test1", personOne.getFirstName());
		assertEquals("Last Name Test1", personOne.getLastName());
		assertEquals("Female", personOne.getGender());
		
		var personThree = people.get(3);
		assertNotNull(personThree);
		assertNotNull(personThree.getKey());
		assertNotNull(personThree.getLinks());
		assertNotNull(personThree.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
		assertEquals("Address Test3", personThree.getAddress());
		assertEquals("First Name Test3", personThree.getFirstName());
		assertEquals("Last Name Test3", personThree.getLastName());
		assertEquals("Female", personThree.getGender());
		
		
		
	}

	@Test
	void testFindByID() {
		Person entity = input.mockEntity(1);
		entity.setId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		var result = service.findByID(1L);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertNotNull(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
		assertEquals("Address Test1", result.getAddress());
		assertEquals("First Name Test1", result.getFirstName());
		assertEquals("Last Name Test1", result.getLastName());
		assertEquals("Female", result.getGender());
	}

	@Test
	void testCreate() {
		Person entity = input.mockEntity(1);
		
		Person persisted = entity;
		persisted.setId(1L);
		PersonVO vo = input.mockVO(1);
		vo.setKey(1L);
		
		when(repository.save(entity)).thenReturn(persisted);
		var result = service.create(vo);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertNotNull(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
		assertEquals("Address Test1", result.getAddress());
		assertEquals("First Name Test1", result.getFirstName());
		assertEquals("Last Name Test1", result.getLastName());
		assertEquals("Female", result.getGender());
	}
	
	@Test
	void testCreateNullPerson() {
		
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.create(null);
		});
		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		
		assertNotNull(actualMessage.contains(expectedMessage));
		
	}


	@Test
	void testUpdate() {
		Person entity = input.mockEntity(1);
		entity.setId(1L);
		
		Person persisted = entity;
		persisted.setId(1L);
		
		PersonVO vo = input.mockVO(1);
		vo.setKey(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		when(repository.save(entity)).thenReturn(persisted);
		
		var result = service.update(vo);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertNotNull(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
		assertEquals("Address Test1", result.getAddress());
		assertEquals("First Name Test1", result.getFirstName());
		assertEquals("Last Name Test1", result.getLastName());
		assertEquals("Female", result.getGender());
	}
	
	
	@Test
	void testUpdateNullPerson() {
		
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});
		String expectedMessage = "It is not allowed to persist a null object!";
		String actualMessage = exception.getMessage();
		
		assertNotNull(actualMessage.contains(expectedMessage));
		
	}

	@Test
	void testDelete() {
		Person entity = input.mockEntity(1);
		entity.setId(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		service.delete(1L);
	}

}
