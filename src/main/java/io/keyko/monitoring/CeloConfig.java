package io.keyko.monitoring;

import com.typesafe.config.Config;
import io.keyko.monitoring.config.StreamerConfig;

public class CeloConfig extends StreamerConfig {

    private static final String ACCOUNTS_AGGREGATION_TOPIC = "kafka.accounts-aggregation-topic";
    private String accountsAggregationTopic;

    public CeloConfig(Config config) {

        super(config);
        this.setAccountsAggregationTopic(config.getString(ACCOUNTS_AGGREGATION_TOPIC));

    }

    public String getAccountsAggregationTopic() {
        return accountsAggregationTopic;
    }

    public void setAccountsAggregationTopic(String accountsAggregationTopic) {
        this.accountsAggregationTopic = accountsAggregationTopic;
    }

}
