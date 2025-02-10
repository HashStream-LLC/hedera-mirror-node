package com.hedera.mirror.importer.parser.record.contractcallnotifications.rules;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.Hashtable;
import java.util.LinkedHashSet;

@Service
public class StreamingRulesStore {
    private final Hashtable<String, Set<String>> _predicateToRuleMappings;

    public StreamingRulesStore() {
        _predicateToRuleMappings = new Hashtable<>();
    }

    public Set<String> getRules(String predicateValue) {
        return _predicateToRuleMappings.getOrDefault(predicateValue, new LinkedHashSet<>());
    }

    public void addRule(StreamingRule rule) {
        Set<String> ruleIds = getRules(rule.predicateValue());
        ruleIds.add(rule.ruleId());
        _predicateToRuleMappings.put(rule.predicateValue(), ruleIds);
    }

    public void addRules(List<StreamingRule> rules) {
        rules.forEach(this::addRule);
    }
}