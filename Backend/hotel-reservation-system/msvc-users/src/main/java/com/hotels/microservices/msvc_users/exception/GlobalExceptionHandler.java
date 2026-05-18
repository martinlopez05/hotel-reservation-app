package com.hotels.microservices.msvc_users.exception;

import com.hotels.microservices.msvc_users.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice // 👈 ¡CLAVE! Si no pones esto, Spring no lo detecta globalmente
public class GlobalExceptionHandler {

    // 1. Recursos no encontrados (404)
    @ExceptionHandler({
            UserNotFoundException.class,
            RoleNotFoundException.class // 👈 Agregamos el rol no encontrado acá
    })
    public ResponseEntity<ErrorResponseDTO> handleNotFoundExceptions(RuntimeException e){
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(System.currentTimeMillis())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    // 2. Credenciales incorrectas en el login (401 Unauthorized)
    @ExceptionHandler(BadCredentialsException.class) // 👈 Tu nueva excepción de Login
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .status(HttpStatus.UNAUTHORIZED.value()) // 401 es el estado correcto para auth fallida
                .timestamp(System.currentTimeMillis())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNAUTHORIZED);
    }

    // 3. Conflictos de datos como Email Duplicado (400 Bad Request o 409 Conflict)
    @ExceptionHandler(EmailAlreadyExistsException.class) // 👈 Tu nueva excepción de Registro
    public ResponseEntity<ErrorResponseDTO> handleEmailAlreadyExists(EmailAlreadyExistsException e) {
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value()) // Podés usar también HttpStatus.CONFLICT (409)
                .timestamp(System.currentTimeMillis())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    // 4. Argumentos ilegales (400)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException e){
        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message("Data invalid: " + e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(System.currentTimeMillis())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    // 5. Fallas de validaciones de Spring (@Valid, @NotBlank, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .message("Validation failed: [" + errorMessage + "]")
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(System.currentTimeMillis())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }
}