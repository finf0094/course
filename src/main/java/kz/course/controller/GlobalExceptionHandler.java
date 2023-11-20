package kz.course.controller;

import kz.course.dto.exception.ProblemDTO;
import kz.course.exceptions.AuthenticationException;
import kz.course.exceptions.NotFoundException;
import kz.course.exceptions.RequestExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDTO> handleAuthenticationException(AuthenticationException ex) {
        ProblemDTO problem = new ProblemDTO(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(problem);
    }

    @ExceptionHandler(RequestExistException.class)
    public ResponseEntity<ProblemDTO> handleRequestExistException(RequestExistException ex) {
        ProblemDTO problem = new ProblemDTO(HttpStatus.CONFLICT.value(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(problem);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDTO> handleNotFoundException(NotFoundException ex) {
        ProblemDTO problem = new ProblemDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(problem);
    }
}
