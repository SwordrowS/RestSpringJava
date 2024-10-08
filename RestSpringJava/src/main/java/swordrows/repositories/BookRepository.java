package swordrows.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swordrows.models.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{}
