package kz.course.dto.exception;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProblemDTO {
    private int status;
    private String message;
}
