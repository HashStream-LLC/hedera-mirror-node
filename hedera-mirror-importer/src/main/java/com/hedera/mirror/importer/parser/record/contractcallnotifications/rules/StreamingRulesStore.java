package com.hedera.mirror.importer.parser.record.contractcallnotifications.rules;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Hashtable;

@Service
public class StreamingRulesStore {
    private final Hashtable<String, Hashtable<String, StreamingRule>> _predicateToRuleMappings;

    public StreamingRulesStore() {
        _predicateToRuleMappings = new Hashtable<>();
    }

    /**
     * Get all rules matching the given predicate value in a map keyed by rule id
     * @param predicateValue The predicate to match (e.g. contract id)
     * @return All rules matching that predicate in a hashtable keyed by rule id
     */
    public Hashtable<String, StreamingRule> getRulesKeyedById(String predicateValue) {
        return _predicateToRuleMappings.getOrDefault(predicateValue, new Hashtable<>());
    }

    /**
     * Get all rules matching the given predicate value as a list of rules
     * @param predicateValue The predicate to match (e.g. contract id)
     * @return All rules matching that predicate in a list
     */
    public List<StreamingRule> getRules(String predicateValue) {
        Hashtable<String, StreamingRule> matchedRules = getRulesKeyedById(predicateValue);
        return matchedRules.values().stream().toList();
    }

    public void putRule(StreamingRule rule) {
        Hashtable<String, StreamingRule> matchedRulesByPredicate = getRulesKeyedById(rule.predicateValue());
        matchedRulesByPredicate.put(rule.ruleId(), rule);
        _predicateToRuleMappings.put(rule.predicateValue(), matchedRulesByPredicate);
    }

    public void removeRule(StreamingRule rule) {
        Hashtable<String, StreamingRule> matchedRulesByPredicate = getRulesKeyedById(rule.predicateValue());
        matchedRulesByPredicate.remove(rule.ruleId());
        _predicateToRuleMappings.put(rule.predicateValue(), matchedRulesByPredicate);
    }

    /** Either include a rule update in the store or remove a rule that no longer needs
     * to be tracked (disabled, deleted, etc.)
     */
    public void processRule(StreamingRule rule) {
        if (rule.disabled()) {
            removeRule(rule);
        } else {
            putRule(rule);
        }
    }

    /** Either include a rule in the store or remove a rule that no longer needs
     * to be tracked (disabled, deleted, etc.)
     */
    public void processRules(List<StreamingRule> rules) {
        rules.forEach(this::processRule);
    }
}