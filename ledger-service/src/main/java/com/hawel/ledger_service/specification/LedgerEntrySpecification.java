package com.hawel.ledger_service.specification;

import com.hawel.ledger_service.dto.StatementFilter;
import com.hawel.ledger_service.entity.LedgerEntry;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public final class LedgerEntrySpecification {

    private LedgerEntrySpecification() {
    }

    public static Specification<LedgerEntry> byFilter(StatementFilter filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Account
            predicates.add(cb.equal(root.get("account").get("id"), filter.getAccountId()));

            // From date
            if (filter.getFrom() != null) {

                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getFrom()));
            }

            // To date
            if (filter.getTo() != null) {

                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), filter.getTo()));
            }

            // Entry Type
            if (filter.getEntryType() != null) {

                predicates.add(cb.equal(root.get("entryType"), filter.getEntryType()));
            }

            // Journal Type
            if (filter.getJournalType() != null) {

                predicates.add(cb.equal(root.get("journal").get("type"), filter.getJournalType()));
            }

            // Reference
            if (filter.getReference() != null && !filter.getReference().isEmpty()) {

                predicates.add(cb.like(cb.lower(root.get("journal").get("reference")), "%" + filter.getReference().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}