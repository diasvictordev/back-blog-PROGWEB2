package com.progweb2.blog.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;


public record AuthResponseDTO(String token, Long id) {
}
