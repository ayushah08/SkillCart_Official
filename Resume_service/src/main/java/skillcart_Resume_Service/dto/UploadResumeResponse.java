package skillcart_Resume_Service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadResumeResponse {

    private Long resumeId;
    private String resumeUrl;
    private String status;

}