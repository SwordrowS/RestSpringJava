package swordrows.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import swordrows.models.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{}
