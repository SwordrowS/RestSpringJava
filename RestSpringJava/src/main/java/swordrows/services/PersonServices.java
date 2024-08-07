package swordrows.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import swordrows.data.vo.v1.PersonVO;
import swordrows.data.vo.v2.PersonVOV2;
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
		
		return DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
	}

	public PersonVO findByID(Long id) {
		logger.info("Finding one PersonVO!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Records Found For This ID"));
		
		return DozerMapper.parseObject(entity, PersonVO.class);
		
	}
	
	public PersonVO create(PersonVO person) {
		logger.info("Creating one Person!");
		var entity = DozerMapper.parseObject(person, Person.class);
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		return vo;
	}
	
	public PersonVOV2 createV2(PersonVOV2 person) {
		logger.info("Creating one Person!");
		var entity = mapper.convertVoToEntity(person);
		var vo = mapper.convertEntityToVO(repository.save(entity));
		return vo;
	}
	
	public PersonVO update(PersonVO person) {
		logger.info("Updating one Person!");
		
		 var entity = repository.findById(person.getId())
		.orElseThrow(() -> new ResourceNotFoundException("No Records Found For This ID"));

		
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		return vo;
	}
	
	
	public void  delete(Long id) {
		logger.info("Deleting one Person!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Records Found For This ID"));
		repository.delete(entity);
		
	}
	
}
