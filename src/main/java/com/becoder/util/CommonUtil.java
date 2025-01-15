package com.becoder.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.becoder.handler.GenericResponse;

public class CommonUtil {

	public static ResponseEntity<?> createBuildResponse(Object data, HttpStatus status) {
		GenericResponse response = GenericResponse.builder().responseStatus(status).status("success").message("message")
				.data(data).build();

		return response.create();

	}

	public static ResponseEntity<?> createBuildResponseMessage(String message, HttpStatus status) {
		GenericResponse response = GenericResponse.builder().responseStatus(status).status("success").message(message)
				.build();

		return response.create();

	}

	public static ResponseEntity<?> createErrorResponse(Object data, String message, HttpStatus status) {
		GenericResponse response = GenericResponse.builder().responseStatus(status).status("success").message("failed")
				.data(data).build();

		return response.create();

	}

	public static ResponseEntity<?> createErrorResponseMessage(String message, HttpStatus status) {
		GenericResponse response = GenericResponse.builder().responseStatus(status).status("failed").message(message)
				.build();

		return response.create();

	}

	public static String getContentType(String originalFileName) {
		String extension = FilenameUtils.getExtension(originalFileName); // java_programing.pdf

		switch (extension) {
		case "pdf":
			return "application/pdf";
		case "xlsx":
			return "application/vnd.openxmlformats-officedocument.spreadsheettml.sheet";
		case "txt":
			return "text/plan";
		case "png":
			return "image/png";
		case "jpeg":
			return "image/jpeg";
		default:
			return "application/octet-stream";
		}
	}
}