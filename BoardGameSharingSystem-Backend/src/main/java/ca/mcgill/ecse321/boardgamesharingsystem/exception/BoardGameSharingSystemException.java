package ca.mcgill.ecse321.boardgamesharingsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public class BoardGameSharingSystemException extends RuntimeException{
    private HttpStatus status;

	public BoardGameSharingSystemException(@NonNull HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
