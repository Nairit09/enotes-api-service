package com.becoder.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.becoder.dto.NotesDto;
import com.becoder.dto.NotesResponse;
import com.becoder.entity.FileDetails;

public interface NotesService {

	public List<NotesDto> getAllNotes();

	public Boolean saveNotes(String notes, MultipartFile file) throws Exception;

	public byte[] downloadFile(FileDetails fileDetails) throws Exception;

	public FileDetails getFileDetails(Integer id) throws Throwable;

	public NotesResponse getAllNotesByUser(Integer userId, Integer pageNo, Integer pageSize);

	public void softDeleteNotes(Integer id) throws Throwable;

	public void restoreNotes(Integer id) throws Throwable;

	public List<NotesDto> getUserRecycleBinNotes(Integer userId);

	public void hardDeleteNotes(Integer id) throws Throwable;

	public void emptyRecycleBin(Integer userId);

}
