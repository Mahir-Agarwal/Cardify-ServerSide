package in.sp.main.core.exception;

import in.sp.main.core.constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;
    private LocalDateTime timestamp;
}
