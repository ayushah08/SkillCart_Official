package skillcart_Resume_Service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skillcart_Resume_Service.entity.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
}