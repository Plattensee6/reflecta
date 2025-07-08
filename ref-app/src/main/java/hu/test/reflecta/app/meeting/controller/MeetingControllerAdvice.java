package hu.test.reflecta.app.meeting.controller;

import hu.test.reflecta.app.exception.ErrorResponse;
import hu.test.reflecta.meeting.exception.MeetingAlreadyFinalizedException;
import hu.test.reflecta.meeting.exception.MeetingConflictException;
import hu.test.reflecta.meeting.exception.ParticipantAccessException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = MeetingController.class)
public class MeetingControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(final EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("MEETING_NOT_FOUND", ex.getLocalizedMessage()));
    }

    @ExceptionHandler(MeetingAlreadyFinalizedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyFinalized(final MeetingAlreadyFinalizedException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("MEETING_FINALIZED", ex.getLocalizedMessage()));
    }

    @ExceptionHandler(MeetingConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(final MeetingConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("MEETING_CONFLICT", ex.getLocalizedMessage()));
    }

    @ExceptionHandler(ParticipantAccessException.class)
    public ResponseEntity<ErrorResponse> handleAccess(final ParticipantAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("ACCESS_DENIED", ex.getLocalizedMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(final MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(final ConstraintViolationException ex) {
        String details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(final Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Unexpected error: " + ex.getLocalizedMessage()));
    }
}