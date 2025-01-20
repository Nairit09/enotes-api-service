package com.becoder.dto;

import com.becoder.entity.Notes;

import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FavouriteNoteDto {

	private Integer id;

	private Notes note;

	private Integer userId;
}
