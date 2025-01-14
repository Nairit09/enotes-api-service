package com.becoder.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.becoder.dto.NotesDto;

public interface NotesService {

	public List<NotesDto> getAllNotes();

	public Boolean saveNotes(String notes, MultipartFile file) throws Exception;
}
