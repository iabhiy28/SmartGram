package com.gramconnect.modules.hierarchy.repository;

import com.gramconnect.modules.hierarchy.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StateRepository extends JpaRepository<State, UUID> {

    List<State> findAllByOrderByNameAsc();

    Optional<State> findByCode(String code);
}
