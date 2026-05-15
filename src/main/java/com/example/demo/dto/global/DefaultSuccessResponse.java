package com.example.demo.dto.global;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultSuccessResponse {
  private int status;
  private String message;
  private LocalDateTime timestamp;

  public DefaultSuccessResponse(String message) {
    this.status = 200;
    this.message = message;
    this.timestamp = LocalDateTime.now();
  }
}
