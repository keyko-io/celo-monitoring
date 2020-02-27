package io.keyko.monitoring;

import com.typesafe.config.Config;
import io.keyko.monitoring.config.StreamerConfig;

public class CeloConfig extends StreamerConfig {
  private static final String EPOCH_REWARDS_GET_TARGET_GET_GOLD_TOTAL_SUPPLY_TOPIC = "kafka.epoch-rewards-get-target-get-gold-total-supply-topic";
  private static final String GOLD_TOKEN_TOTAL_SUPPLY_TOPIC = "kafka.gold-token-total-supply-topic";
  private static final String EPOCH_REWARDS_AGGREGATION_TOPIC = "kafka.epoch-rewards-aggregation-topic";
  private static final String SINK_SUFFIX = "kafka.sink-suffix";

  private String epochRewardsGetTargetGetGoldTotalSupplyTopic;
  private String goldTokenTotalSupplyTopic;
  private String epochRewardsAggregationTopic;
  private String sinkSuffix;


  public CeloConfig(Config config) {

    super(config);
    this.setEpochRewardsGetTargetGetGoldTotalSupplyTopic(config.getString(EPOCH_REWARDS_GET_TARGET_GET_GOLD_TOTAL_SUPPLY_TOPIC));
    this.setGoldTokenTotalSupplyTopic(config.getString(GOLD_TOKEN_TOTAL_SUPPLY_TOPIC));
    this.setEpochRewardsAggregationTopic(config.getString(EPOCH_REWARDS_AGGREGATION_TOPIC));
    this.setSinkSuffix(config.getString(SINK_SUFFIX));

  }

  public String getGoldTokenTotalSupplyTopic() {
    return goldTokenTotalSupplyTopic;
  }

  public void setGoldTokenTotalSupplyTopic(String goldTokenTotalSupplyTopic) {
    this.goldTokenTotalSupplyTopic = goldTokenTotalSupplyTopic;
  }

  public String getEpochRewardsGetTargetGetGoldTotalSupplyTopic() {
    return epochRewardsGetTargetGetGoldTotalSupplyTopic;
  }

  public void setEpochRewardsGetTargetGetGoldTotalSupplyTopic(String epochRewardsGetTargetGetGoldTotalSupplyTopic) {
    this.epochRewardsGetTargetGetGoldTotalSupplyTopic = epochRewardsGetTargetGetGoldTotalSupplyTopic;
  }

  public String getEpochRewardsAggregationTopic() {
    return epochRewardsAggregationTopic;
  }

  public void setEpochRewardsAggregationTopic(String epochRewardsAggregationTopic) {
    this.epochRewardsAggregationTopic = epochRewardsAggregationTopic;
  }

  public String getSinkSuffix() {
    return sinkSuffix;
  }

  public void setSinkSuffix(String sinkSuffix) {
    this.sinkSuffix = sinkSuffix;
  }

}
