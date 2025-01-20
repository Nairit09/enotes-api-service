package com.becoder.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import com.becoder.dto.FavouriteNoteDto;
import com.becoder.dto.NotesDto;
import com.becoder.dto.NotesDto.CategoryDto;
import com.becoder.dto.NotesDto.FilesDto;
import com.becoder.dto.NotesResponse;
import com.becoder.entity.FavouriteNote;
import com.becoder.entity.FileDetails;
import com.becoder.entity.Notes;
import com.becoder.exception.ResourceNotFoundException;
import com.becoder.repository.CategoryRepository;
import com.becoder.repository.FavouriteNoteRepository;
import com.becoder.repository.FileRepository;
import com.becoder.repository.NotesRepository;
import com.becoder.service.NotesService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class NotesServiceImpl implements NotesService {

	@Autowired
	private NotesRepository notesRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private CategoryRepository categoryRepo;

	@Autowired
	private FavouriteNoteRepository favouriteNoteRepo;

	@Autowired
	private NotesRepository notesRepository;

	@Value("${file.upload.path")
	private String uploadpath;

	@Autowired
	private FileRepository fileRepo;

	@Override
	public Boolean saveNotes(String notes, MultipartFile file) throws Exception {

		ObjectMapper ob = new ObjectMapper();
		NotesDto notesDto = ob.readValue(notes, NotesDto.class);
		notesDto.setIsDeleted(false);
		notesDto.setDeletedOn(null);
		// update notes if id is given in request
		if (!ObjectUtils.isEmpty(notesDto.getId())) {
			updateNotes(notesDto, file);
		}

		// validation notes
		checkCategoryExist(notesDto.getCategory());

		Notes notesMap = mapper.map(notesDto, Notes.class);

		FileDetails filedetails = saveFileDetails(file);

		if (!ObjectUtils.isEmpty(filedetails)) {
			notesMap.setFileDetails(filedetails);
		} else {
			if (ObjectUtils.isEmpty(notesDto.getId())) {
				notesMap.setFileDetails(null);
			}
		}

		Notes saveNotes = notesRepo.save(notesMap);
		if (!ObjectUtils.isEmpty(saveNotes)) {
			return true;
		}
		return false;
	}

	private void updateNotes(NotesDto notesDto, MultipartFile file) throws Exception {

		Notes existNotes = notesRepo.findById(notesDto.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Invalid Notes id"));
		notesDto.setCreatedBy(existNotes.getCreatedBy());
		notesDto.setCreatedOn(existNotes.getCreatedOn());

		notesDto.setUpdatedBy(1);
		// user not choose any file at update time
		if (ObjectUtils.isEmpty(file)) {
			notesDto.setFileDtls(mapper.map(existNotes.getFileDetails(), FilesDto.class));

		}

	}

	private FileDetails saveFileDetails(MultipartFile file) throws IOException {

		if (!ObjectUtils.isEmpty(file) && !file.isEmpty()) {

			String originalFilename = file.getOriginalFilename();
			String extension = FilenameUtils.getExtension(originalFilename);

			List<String> extensionAllow = Arrays.asList("pdf", "xlsx", "jpg", "png", "docx");
			if (!extensionAllow.contains(extension)) {
				throw new IllegalArgumentException("invalid file format ! Upload only .pdf , .xlsx,.jpg");
			}

			String rndString = UUID.randomUUID().toString();
			String uploadfileName = rndString + "." + extension; // sdfsafbhkljsf.pdf

			File saveFile = new File(uploadpath);
			if (!saveFile.exists()) {
				saveFile.mkdir();
			}
			// path : enotesapiservice/notes/java.pdf
			String storePath = uploadpath.concat(uploadfileName);

			// upload file
			long upload = Files.copy(file.getInputStream(), Paths.get(storePath));
			if (upload != 0) {
				FileDetails fileDtls = new FileDetails();
				fileDtls.setOriginalFileName(originalFilename);
				fileDtls.setDisplayFileName(getDisplayName(originalFilename));
				fileDtls.setUploadFileName(uploadfileName);
				fileDtls.setFileSize(file.getSize());
				fileDtls.setPath(storePath);
				FileDetails saveFileDtls = fileRepo.save(fileDtls);
				return saveFileDtls;
			}
		}

		return null;
	}

	private String getDisplayName(String originalFileName) {
		// java_programming_tutorials.pdf
		// java_prog.pdf
		String extension = FilenameUtils.getExtension(originalFileName);
		String fileName = FilenameUtils.removeExtension(originalFileName);

		if (fileName.length() > 8) {
			fileName = fileName.substring(0, 7);
		}
		fileName = fileName + "." + extension;
		return fileName;
	}

	private void checkCategoryExist(CategoryDto category) throws Exception {

		categoryRepo.findById(category.getId())
				.orElseThrow(() -> new ResourceNotFoundException("category id` invalid"));
	}

	@Override
	public List<NotesDto> getAllNotes() {

		return notesRepo.findAll().stream().map(note -> mapper.map(note, NotesDto.class)).toList();

	}

	@Override
	public byte[] downloadFile(FileDetails fileDetails) throws Exception {

		InputStream io = new FileInputStream(fileDetails.getPath());
		return StreamUtils.copyToByteArray(io);
	}

	@Override
	public FileDetails getFileDetails(Integer id) throws Throwable {
		FileDetails fileDetails = fileRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("File is not available"));
		return fileDetails;
	}

	@Override
	public NotesResponse getAllNotesByUser(Integer userId, Integer pageNo, Integer pageSize) {

		if (userId == null || pageNo == null || pageSize == null) {
			throw new IllegalArgumentException("User ID, Page Number, and Page Size must not be null.");
		}

		if (pageNo > 0) {
			pageNo -= 1; // Convert to zero-based index for Pageable
		}

		// Create Pageable object
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		// Fetch data from repository
		Page<Notes> pageNotes = notesRepo.findByCreatedByAndIsDeletedFalse(userId, pageable);

		// Convert entities to DTOs
		List<NotesDto> notesDtoList = pageNotes.getContent().stream().map(note -> mapper.map(note, NotesDto.class))
				.toList();

		// Build the response object
		NotesResponse notesResponse = NotesResponse.builder().notes(notesDtoList).pageNo(pageNotes.getNumber())
				.pageSize(pageNotes.getSize()).totalElements(pageNotes.getTotalElements())
				.totalPages(pageNotes.getTotalPages()).isFirst(pageNotes.isFirst()).isLast(pageNotes.isLast()).build();

		return notesResponse;
	}

	@Override
	public void softDeleteNotes(Integer id) throws Throwable {
		Notes notes = notesRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notes id invalid ! Not Found"));

		notes.setIsDeleted(true);
		notes.setDeletedOn(LocalDate.now());
		notesRepo.save(notes);
	}

	@Override
	public void restoreNotes(Integer id) throws Throwable {
		Notes notes = notesRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notes id invalid ! Not Found"));

		notes.setIsDeleted(false);
		notes.setDeletedOn(null);
		notesRepo.save(notes);
	}

	@Override
	public List<NotesDto> getUserRecycleBinNotes(Integer userId) {
		List<Notes> recycleNotes = notesRepo.findByCreatedByAndIsDeletedTrue(userId);
		List<NotesDto> notesDtoList = recycleNotes.stream().map(note -> mapper.map(note, NotesDto.class)).toList();
		return notesDtoList;
	}

	@Override
	public void hardDeleteNotes(Integer id) throws Throwable {
		Notes notes = notesRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notes not found"));

		if (notes.getIsDeleted()) {
			notesRepo.delete(notes);
		} else {
			throw new IllegalArgumentException("Sorry You cant hard delete Directly");
		}

	}

	@Override
	public void emptyRecycleBin(Integer userId) {
		List<Notes> recycleNotes = notesRepo.findByCreatedByAndIsDeletedTrue(userId);

		if (!CollectionUtils.isEmpty(recycleNotes)) {
			notesRepo.deleteAll(recycleNotes);
		}
	}

	@Override
	public void favouriteNotes(Integer noteId) throws Exception {
		int userId = 1;
		Notes notes = notesRepo.findById(noteId)
				.orElseThrow(() -> new ResourceNotFoundException("Notes Not found & Id invalid "));

		FavouriteNote favouriteNote = FavouriteNote.builder().note(notes).userId(noteId).build();

		favouriteNoteRepo.save(favouriteNote);
	}

	@Override
	public void unFavouriteNotes(Integer FavouriteNoteId) throws Exception {
		FavouriteNote favouriteNote = favouriteNoteRepo.findById(FavouriteNoteId)
				.orElseThrow(() -> new ResourceNotFoundException("Favourite Notes Not found & Id invalid "));
		favouriteNoteRepo.delete(favouriteNote);

	}

	@Override
	public List<FavouriteNoteDto> getUserFavouriteNotes() {
		int userId = 1;
		List<FavouriteNote> favouriteNotes = favouriteNoteRepo.findByUserId(userId);
		List<FavouriteNoteDto> favouriteNoteList = favouriteNotes.stream()
				.map(fn -> mapper.map(fn, FavouriteNoteDto.class)).toList();
		return favouriteNoteList;
	}

	@Override
	public Boolean copyNotes(Integer id) throws Exception {
		Notes notes = notesRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Notes id invalid ! Not Found"));

		Notes copyNote = Notes.builder().title(notes.getTitle()).description(notes.getDescription())
				.category(notes.getCategory()).isDeleted(false).fileDetails(null).build();

		Notes saveCopyNotes = notesRepo.save(copyNote);
		if (!ObjectUtils.isEmpty(saveCopyNotes)) {
			return true;
		}
		return false;
	}

}
