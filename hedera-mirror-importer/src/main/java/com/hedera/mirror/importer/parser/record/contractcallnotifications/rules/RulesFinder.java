package com.hedera.mirror.importer.parser.record.contractcallnotifications.rules;

import org.springframework.stereotype.Service;

import java.util.Arrays;

/** An object that can find all rules for given contract ids */
@Service
public class RulesFinder {
    private final StreamingRulesStore _rulesStore;

    public RulesFinder(StreamingRulesStore rulesStore) {
        _rulesStore = rulesStore;
    }

    /** Find all rules for the given list of contract ids */
    public String[] getMatchedRuleIds(String[] contractIds) {
        return Arrays.stream(contractIds)
                .flatMap(contractId -> _rulesStore.getRules(contractId).stream())
                .toArray(String[]::new);
    }
}
