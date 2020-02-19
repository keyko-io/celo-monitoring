package io.keyko.monitoring;

import com.typesafe.config.Config;
import io.keyko.monitoring.config.StreamerConfig;

public class CeloConfig extends StreamerConfig {

  private static final String ACCOUNTS_AGGREGATION_TOPIC = "kafka.accounts-aggregation-topic";
  private static final String SINK_SUFFIX = "kafka.sink-suffix";

  private String accountsAggregationTopic;
  private String sinkSuffix;


  public CeloConfig(Config config) {

    super(config);
    this.setAccountsAggregationTopic(config.getString(ACCOUNTS_AGGREGATION_TOPIC));
    this.setSinkSuffix(config.getString(SINK_SUFFIX));

  }

  public String getAccountsAggregationTopic() {
    return accountsAggregationTopic;
  }

  public void setAccountsAggregationTopic(String accountsAggregationTopic) {
    this.accountsAggregationTopic = accountsAggregationTopic;
  }


  public String getSinkSuffix() {
    return sinkSuffix;
  }

  public void setSinkSuffix(String sinkSuffix) {
    this.sinkSuffix = sinkSuffix;
  }

}
