@network @fullsuite
Feature: Network Stake Coverage Feature

    @networkstake @acceptance
    Scenario Outline: Get network stake
        When I query the network stake
        Then the mirror node REST API returns the network stake
