package com.gramconnect.modules.scheme.service;

import com.gramconnect.common.dto.PageResponse;
import com.gramconnect.common.exception.ConflictException;
import com.gramconnect.common.exception.ResourceNotFoundException;
import com.gramconnect.modules.scheme.dto.EligibilityCheckRequest;
import com.gramconnect.modules.scheme.dto.SchemeResponse;
import com.gramconnect.modules.scheme.entity.GovernmentScheme;
import com.gramconnect.modules.scheme.entity.SchemeEligibilityRule;
import com.gramconnect.modules.scheme.entity.UserSavedScheme;
import com.gramconnect.modules.scheme.repository.GovernmentSchemeRepository;
import com.gramconnect.modules.scheme.repository.SchemeEligibilityRuleRepository;
import com.gramconnect.modules.scheme.repository.UserSavedSchemeRepository;
import com.gramconnect.modules.user.entity.User;
import com.gramconnect.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Government Scheme Service with Eligibility Screening Engine.
 *
 * The eligibility engine evaluates user-provided profile attributes against
 * each scheme's key-value eligibility rules. Supported rule keys:
 *   MIN_AGE, MAX_AGE, GENDER, MAX_INCOME, CATEGORY, ECONOMIC_STATUS,
 *   OCCUPATION, STATE, IS_DISABLED, IS_MINORITY
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchemeService {

    private final GovernmentSchemeRepository schemeRepository;
    private final SchemeEligibilityRuleRepository ruleRepository;
    private final UserSavedSchemeRepository savedSchemeRepository;
    private final UserRepository userRepository;

    // ========================================================================
    // SCHEME BROWSING
    // ========================================================================

    @Transactional(readOnly = true)
    public PageResponse<SchemeResponse> searchSchemes(String schemeType, String department, String keyword, Pageable pageable) {
        Page<GovernmentScheme> page = schemeRepository.searchSchemes(schemeType, department, keyword, pageable);
        return PageResponse.from(page.map(SchemeResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public SchemeResponse getSchemeById(UUID schemeId) {
        GovernmentScheme scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new ResourceNotFoundException("GovernmentScheme", "id", schemeId));
        return SchemeResponse.fromEntity(scheme);
    }

    // ========================================================================
    // ELIGIBILITY SCREENING ENGINE
    // ========================================================================

    /**
     * Check a single scheme's eligibility against user profile.
     * Returns true if ALL rules pass, false if any rule fails.
     */
    @Transactional(readOnly = true)
    public boolean checkEligibility(UUID schemeId, EligibilityCheckRequest profile) {
        List<SchemeEligibilityRule> rules = ruleRepository.findBySchemeId(schemeId);
        if (rules.isEmpty()) return true; // No rules = universally eligible

        for (SchemeEligibilityRule rule : rules) {
            if (!evaluateRule(rule, profile)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Discover all schemes the user is eligible for based on their profile.
     * Iterates through all active schemes and filters by eligibility.
     */
    @Transactional(readOnly = true)
    public List<SchemeResponse> discoverEligibleSchemes(EligibilityCheckRequest profile, Pageable pageable) {
        Page<GovernmentScheme> allSchemes = schemeRepository.findByIsActiveTrue(pageable);
        List<SchemeResponse> eligible = new ArrayList<>();

        for (GovernmentScheme scheme : allSchemes.getContent()) {
            List<SchemeEligibilityRule> rules = scheme.getEligibilityRules();
            boolean passes = true;
            if (rules != null) {
                for (SchemeEligibilityRule rule : rules) {
                    if (!evaluateRule(rule, profile)) {
                        passes = false;
                        break;
                    }
                }
            }
            if (passes) {
                eligible.add(SchemeResponse.fromEntity(scheme));
            }
        }

        log.info("Eligibility screening: {}/{} schemes matched user profile",
                eligible.size(), allSchemes.getTotalElements());
        return eligible;
    }

    /**
     * Core rule evaluation engine.
     * Evaluates a single eligibility rule against user profile attributes.
     */
    private boolean evaluateRule(SchemeEligibilityRule rule, EligibilityCheckRequest profile) {
        String key = rule.getRuleKey().toUpperCase();
        String value = rule.getRuleValue();

        return switch (key) {
            case "MIN_AGE" -> profile.getAge() != null && profile.getAge() >= Integer.parseInt(value);
            case "MAX_AGE" -> profile.getAge() != null && profile.getAge() <= Integer.parseInt(value);
            case "GENDER" -> profile.getGender() != null && profile.getGender().equalsIgnoreCase(value);
            case "MAX_INCOME" -> profile.getAnnualIncome() != null && profile.getAnnualIncome() <= Double.parseDouble(value);
            case "CATEGORY" -> profile.getCategory() != null && value.toUpperCase().contains(profile.getCategory().toUpperCase());
            case "ECONOMIC_STATUS" -> profile.getEconomicStatus() != null && profile.getEconomicStatus().equalsIgnoreCase(value);
            case "OCCUPATION" -> profile.getOccupation() != null && value.toUpperCase().contains(profile.getOccupation().toUpperCase());
            case "STATE" -> profile.getState() != null && value.toUpperCase().contains(profile.getState().toUpperCase());
            case "IS_DISABLED" -> profile.getIsDisabled() != null && profile.getIsDisabled().toString().equalsIgnoreCase(value);
            case "IS_MINORITY" -> profile.getIsMinority() != null && profile.getIsMinority().toString().equalsIgnoreCase(value);
            default -> {
                log.warn("Unknown eligibility rule key: '{}'. Skipping evaluation.", key);
                yield true; // Unknown rules don't block eligibility
            }
        };
    }

    // ========================================================================
    // SAVE / BOOKMARK SCHEMES
    // ========================================================================

    @Transactional
    public void saveScheme(UUID userId, UUID schemeId) {
        if (savedSchemeRepository.existsByUserIdAndSchemeId(userId, schemeId)) {
            throw new ConflictException("Scheme is already saved");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        GovernmentScheme scheme = schemeRepository.findById(schemeId)
                .orElseThrow(() -> new ResourceNotFoundException("GovernmentScheme", "id", schemeId));

        UserSavedScheme saved = UserSavedScheme.builder().user(user).scheme(scheme).build();
        savedSchemeRepository.save(saved);
        log.info("User [ID: {}] saved scheme [ID: {}]", userId, schemeId);
    }

    @Transactional
    public void unsaveScheme(UUID userId, UUID schemeId) {
        UserSavedScheme saved = savedSchemeRepository.findByUserIdAndSchemeId(userId, schemeId)
                .orElseThrow(() -> new ResourceNotFoundException("UserSavedScheme", "userId+schemeId", userId));
        savedSchemeRepository.delete(saved);
        log.info("User [ID: {}] unsaved scheme [ID: {}]", userId, schemeId);
    }

    @Transactional(readOnly = true)
    public PageResponse<SchemeResponse> getSavedSchemes(UUID userId, Pageable pageable) {
        Page<UserSavedScheme> page = savedSchemeRepository.findByUserId(userId, pageable);
        return PageResponse.from(page.map(s -> SchemeResponse.fromEntity(s.getScheme())));
    }
}
