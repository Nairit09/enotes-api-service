package com.becoder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.becoder.entity.Notes;

public interface NotesRepository extends JpaRepository<Notes, Integer> {

}