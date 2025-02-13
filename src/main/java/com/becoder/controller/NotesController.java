package com.becoder.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.becoder.dto.FavouriteNoteDto;
import com.becoder.dto.NotesDto;
import com.becoder.dto.NotesResponse;
import com.becoder.endpoint.NotesEndpoint;
import com.becoder.entity.FileDetails;
import com.becoder.service.NotesService;
import com.becoder.util.CommonUtil;

@RestController
public class NotesController implements NotesEndpoint {

	@Autowired
	private NotesService notesService;

	@Override
	public ResponseEntity<?> saveNotes(String notes, MultipartFile file) throws Exception {

		Boolean saveNotes = notesService.saveNotes(notes, file);
		if (saveNotes) {
			return CommonUtil.createBuildResponseMessage("Notes saved success", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Notes not saved", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<?> downloadFile(Integer id) throws Exception {

		FileDetails fileDetails = notesService.getFileDetails(id);
		byte[] data = notesService.downloadFile(fileDetails);

		HttpHeaders headers = new HttpHeaders();
		String contentType = CommonUtil.getContentType(fileDetails.getOriginalFileName());
		headers.setContentType(MediaType.parseMediaType(contentType));
		headers.setContentDispositionFormData("attachment", fileDetails.getOriginalFileName());

		return ResponseEntity.ok().headers(headers).body(data);

	}

	@Override
	public ResponseEntity<?> getAllNotes() {

		List<NotesDto> allNotes = notesService.getAllNotes();
		if (CollectionUtils.isEmpty(allNotes)) {

			return ResponseEntity.noContent().build();
		}
		return CommonUtil.createBuildResponse(allNotes, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getAllNotesByUser(Integer pageNo, Integer pageSize) {
		NotesResponse allNotes = notesService.getAllNotesByUser(pageNo, pageSize);
		return CommonUtil.createBuildResponse(allNotes, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> deleteNotes(Integer id) throws Exception {
		notesService.softDeleteNotes(id);
		return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> restoreNotes(Integer id) throws Exception {
		notesService.restoreNotes(id);
		return CommonUtil.createBuildResponseMessage("Restored Succesfully", HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> getUserRecycleBinNotes() throws Exception {
		List<NotesDto> notes = notesService.getUserRecycleBinNotes();
		if (CollectionUtils.isEmpty(notes)) {
			return CommonUtil.createBuildResponseMessage("Notes not available in Recycle Bin", HttpStatus.OK);
		}
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> hardDeleteNotes(Integer id) throws Throwable {
		notesService.hardDeleteNotes(id);
		return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> emptyUserRecycleBin() throws Exception {
		notesService.emptyRecycleBin();
		return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> favouriteNote(Integer noteId) throws Exception {
		notesService.favouriteNotes(noteId);
		return CommonUtil.createBuildResponseMessage("Notes added Favourite", HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> unFavourite(Integer favNoteId) throws Exception {
		notesService.unFavouriteNotes(favNoteId);
		return CommonUtil.createBuildResponseMessage("Remove Favourite", HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> getUserFavouriteNote() throws Exception {
		List<FavouriteNoteDto> userFavouriteNotes = notesService.getUserFavouriteNotes();
		if (CollectionUtils.isEmpty(userFavouriteNotes)) {
			return ResponseEntity.noContent().build();
		}
		return CommonUtil.createBuildResponse(userFavouriteNotes, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> CopyNotes(Integer Id) throws Exception {
		Boolean copyNotes = notesService.copyNotes(Id);
		if (copyNotes) {
			return CommonUtil.createBuildResponseMessage("Copied success", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Copy Failed ! Try Again", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@Override
	public ResponseEntity<?> searchNotes(String key, Integer pageNo, Integer pageSize) {
		NotesResponse notes = notesService.getNotesByUserSearch(pageNo, pageSize, key);
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);
	}
}
