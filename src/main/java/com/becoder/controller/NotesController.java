package com.becoder.controller;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.becoder.dto.FavouriteNoteDto;
import com.becoder.dto.NotesDto;
import com.becoder.dto.NotesResponse;
import com.becoder.entity.FileDetails;
import com.becoder.service.NotesService;
import com.becoder.util.CommonUtil;

@RestController
@RequestMapping("/api/v1/notes")
public class NotesController {

	@Autowired
	private NotesService notesService;

	@PostMapping("/")
	public ResponseEntity<?> saveNotes(@RequestParam String notes, @RequestParam(required = false) MultipartFile file)
			throws Exception {

		Boolean saveNotes = notesService.saveNotes(notes, file);
		if (saveNotes) {
			return CommonUtil.createBuildResponseMessage("Notes saved success", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Notes not saved", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<?> downloadFile(@PathVariable Integer id) throws Throwable {

		FileDetails fileDetails = notesService.getFileDetails(id);
		byte[] data = notesService.downloadFile(fileDetails);

		HttpHeaders headers = new HttpHeaders();
		String contentType = CommonUtil.getContentType(fileDetails.getOriginalFileName());
		headers.setContentType(MediaType.parseMediaType(contentType));
		headers.setContentDispositionFormData("attachment", fileDetails.getOriginalFileName());

		return ResponseEntity.ok().headers(headers).body(data);

	}

	@GetMapping("/")
	public ResponseEntity<?> getAllNotes() {

		List<NotesDto> allNotes = notesService.getAllNotes();
		if (CollectionUtils.isEmpty(allNotes)) {

			return ResponseEntity.noContent().build();
		}
		return CommonUtil.createBuildResponse(allNotes, HttpStatus.OK);
	}

	@GetMapping("/user-notes")
	public ResponseEntity<?> getAllNotesByUser(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		Integer userId = 1;
		NotesResponse allNotes = notesService.getAllNotesByUser(userId, pageNo, pageSize);
		return CommonUtil.createBuildResponse(allNotes, HttpStatus.OK);
	}

	@GetMapping("/delete/{id}")
	public ResponseEntity<?> deleteNotes(@PathVariable Integer id) throws Throwable {
		notesService.softDeleteNotes(id);
		return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);

	}

	@GetMapping("/restore/{id}")
	public ResponseEntity<?> restoreNotes(@PathVariable Integer id) throws Throwable {
		notesService.restoreNotes(id);
		return CommonUtil.createBuildResponseMessage("Restored Succesfully", HttpStatus.OK);

	}

	@GetMapping("/recycle-bin")
	public ResponseEntity<?> getUserRecycleBinNotes() throws Throwable {
		Integer userId = 1;
		List<NotesDto> notes = notesService.getUserRecycleBinNotes(userId);
		if (CollectionUtils.isEmpty(notes)) {
			return CommonUtil.createBuildResponseMessage("Notes not available in Recycle Bin", HttpStatus.OK);
		}
		return CommonUtil.createBuildResponse(notes, HttpStatus.OK);

	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> hardDeleteNotes(@PathVariable Integer id) throws Throwable {
		notesService.hardDeleteNotes(id);
		return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);

	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> emptyRecycleBin() throws Throwable {
		Integer userId = 1;
		notesService.emptyRecycleBin(userId);
		return CommonUtil.createBuildResponseMessage("Delete Success", HttpStatus.OK);

	}

	@GetMapping("/fav/{noteId}")
	public ResponseEntity<?> favouriteNote(@PathVariable Integer noteId) throws Throwable {
		notesService.favouriteNotes(noteId);
		return CommonUtil.createBuildResponseMessage("Notes added Favourite", HttpStatus.OK);

	}

	@DeleteMapping("/un-fav/{favNoteId}")
	public ResponseEntity<?> unFavourite(@PathVariable Integer favNoteId) throws Throwable {
		notesService.unFavouriteNotes(favNoteId);
		return CommonUtil.createBuildResponseMessage("Remove Favourite", HttpStatus.OK);

	}

	@GetMapping("/fav-note")
	public ResponseEntity<?> getUserFavouriteNote() throws Throwable {
		List<FavouriteNoteDto> userFavouriteNotes = notesService.getUserFavouriteNotes();
		if (CollectionUtils.isEmpty(userFavouriteNotes)) {
			return ResponseEntity.noContent().build();
		}
		return CommonUtil.createBuildResponse(userFavouriteNotes, HttpStatus.OK);

	}

	@GetMapping("/copy/{Id}")
	public ResponseEntity<?> CopyNotes(@PathVariable Integer Id) throws Throwable {
		Boolean copyNotes = notesService.copyNotes(Id);
		if (copyNotes) {
			return CommonUtil.createBuildResponseMessage("Copied success", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Copy Failed ! Try Again", HttpStatus.INTERNAL_SERVER_ERROR);

	}
}
