package skillcart_Resume_Service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resumeId;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    private ResumeStatus status;

    private String resumeName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String resumeUrl;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String aiResponseJson;

    private LocalDateTime uploadedAt;
}