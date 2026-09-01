package com.gramconnect.modules.scheme.repository;

import com.gramconnect.modules.scheme.entity.SchemeEligibilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SchemeEligibilityRuleRepository extends JpaRepository<SchemeEligibilityRule, UUID> {

    List<SchemeEligibilityRule> findBySchemeId(UUID schemeId);
}
