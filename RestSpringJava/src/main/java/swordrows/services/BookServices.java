package swordrows.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

import swordrows.controllers.BookController;
import swordrows.data.vo.v1.BookVO;
import swordrows.exceptions.RequiredObjectIsNullException;
import swordrows.exceptions.ResourceNotFoundException;
import swordrows.mapper.BookModelMapper;
import swordrows.mapper.BookModelMapper;
//import swordrows.mapper.custom.BookMapper;
import swordrows.models.Book;
import swordrows.repositories.BookRepository;

@Service
public class BookServices {

	
	private Logger logger = Logger.getLogger(BookServices.class.getName());

	
	@Autowired
	BookRepository repository;
	
	//@Autowired
	//BookMapper mapper;
	
	
	public List<BookVO> findAll() {
		logger.info("Finding all Book!");
		
		var books = BookModelMapper.parseListObjects(repository.findAll(), BookVO.class);
		books.stream().forEach(p -> p.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(
				BookController.class).findById(p.getKey())).withSelfRel()));
		return books;
	}

	public BookVO findByID(Long id) {
		logger.info("Finding one BookVO!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Records Found For This ID"));
		
		BookVO vo = BookModelMapper.parseObject(entity, BookVO.class);
		vo.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(
				BookController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public BookVO create(BookVO book) {
		
		if (book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one Book!");
		var entity = BookModelMapper.parseObject(book, Book.class);
		var vo = BookModelMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(
				BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	
	public BookVO update(BookVO book) {
		
		if (book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one Book!");
		
		 var entity = repository.findById(book.getKey())
		.orElseThrow(() -> new ResourceNotFoundException("No Records Found For This ID"));

		
		entity.setAuthor(book.getAuthor());
		entity.setTitle(book.getTitle());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		
		var vo = BookModelMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(
				BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	
	public void  delete(Long id) {
		logger.info("Deleting one Book!");
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Records Found For This ID"));
		repository.delete(entity);
		
	}
	
}
