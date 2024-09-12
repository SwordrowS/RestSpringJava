package swordrows.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import swordrows.controllers.PersonController;
import swordrows.data.vo.v1.PersonVO;
import swordrows.data.vo.v2.PersonVOV2;
import swordrows.exceptions.RequiredObjectIsNullException;
import swordrows.exceptions.ResourceNotFoundException;
import swordrows.mapper.DozerMapper;
import swordrows.mapper.custom.PersonMapper;
import swordrows.models.Person;
import swordrows.repositories.PersonRepository;

@Service
public class PersonServices {

	
	private Logger logger = Logger.getLogger(PersonServices.class.getName());

	
	@Autowired
	PersonRepository repository;
	
	@Autowired
	PersonMapper mapper;
	
	
	public List<PersonVO> findAll() {
		logger.info("Finding all People!");
		
		var persons = DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
		persons.stream().forEach(p -> p.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(
				PersonController.class).findById(p.getKey())).withSelfRel()));
		return persons;
	}

	public PersonVO findByID(Long id) {
		logger.info("Finding one PersonVO!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Records Found For This ID"));
		
		PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(
				PersonController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	
	public PersonVO create(PersonVO person) {
		
		if (person == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one Person!");
		var entity = DozerMapper.parseObject(person, Person.class);
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(
				PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public PersonVOV2 createV2(PersonVOV2 person) {
		logger.info("Creating one Person!");
		var entity = mapper.convertVoToEntity(person);
		var vo = mapper.convertEntityToVO(repository.save(entity));
		return vo;
	}
	
	public PersonVO update(PersonVO person) {
		
		if (person == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one Person!");
		
		 var entity = repository.findById(person.getKey())
		.orElseThrow(() -> new ResourceNotFoundException("No Records Found For This ID"));

		
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(
				PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	
	@Transactional
	public PersonVO disablePerson(Long id) {
		logger.info("Finding one PersonVO!");
		repository.disablePerson(id);
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Records Found For This ID"));
		
		PersonVO vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(
				PersonController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	
	public void  delete(Long id) {
		logger.info("Deleting one Person!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Records Found For This ID"));
		repository.delete(entity);
		
	}
	
}
