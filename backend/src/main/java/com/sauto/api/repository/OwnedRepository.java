package com.sauto.api.repository;

import com.sauto.api.model.OwnedRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OwnedRepository<T extends OwnedRecord> extends JpaRepository<T, Long> {
    List<T> findAllByOwnerEmailOrderByIdDesc(String ownerEmail);
    Optional<T> findByIdAndOwnerEmail(Long id, String ownerEmail);
}
