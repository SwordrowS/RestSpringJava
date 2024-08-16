package swordrows.mapper;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;

import swordrows.data.vo.v1.BookVO;
import swordrows.data.vo.v1.PersonVO;
import swordrows.models.Book;
import swordrows.models.Person;

public class BookModelMapper {
	
	private static ModelMapper mapper = new ModelMapper();
	
	static {
		mapper.createTypeMap(Book.class, BookVO.class)
		.addMapping(Book::getId, BookVO::setKey);
		mapper.createTypeMap(BookVO.class, Book.class)
		.addMapping(BookVO::getKey, Book::setId);
		
	}
	
	public static <O, D> D parseObject(O origin, Class<D> destination) {
		
		return mapper.map(origin, destination);
	}
	
	
	
	public static <O, D> List<D> parseListObjects(List<O> origin, Class<D> destination) {
		List<D> destinationObjects = new ArrayList<D>();
		for (O o : origin) {
			destinationObjects.add(mapper.map(o, destination));
		}		
		return destinationObjects;
	}

}
