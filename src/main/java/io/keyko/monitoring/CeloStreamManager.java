package io.keyko.monitoring;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.keyko.monitoring.model.EpochRewardsAggregation;
import io.keyko.monitoring.postprocessing.Output;
import io.keyko.monitoring.preprocessing.Filters;
import io.keyko.monitoring.preprocessing.Transformations;
import io.keyko.monitoring.schemas.*;
import io.keyko.monitoring.serde.Web3MonitoringSerdes;
import io.keyko.monitoring.stream.BaseStreamManager;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.internals.Topic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

public class CeloStreamManager extends BaseStreamManager {

  CeloConfig celoConfig;

  public CeloStreamManager(CeloConfig config) {
    super(config);
    this.celoConfig = config;
  }

  @Override
  protected void processStreams(KStream<String, EventRecord> eventStream, KStream<String, ViewRecord> viewStream, KTable<String, BlockRecord> blockTable) {

    final KStream<String, EventRecord> eventAvroStream = Filters.filterConfirmed(eventStream);
    KStream<String, EventBlockRecord> eventBlockStream = Transformations.joinEventWithBlock(eventAvroStream, blockTable);

    Output.splitByEvent(eventBlockStream, celoConfig.getSinkSuffix());

    KStream<String, ViewBlockRecord> viewBlockStream = Transformations.joinViewWithBlock(viewStream, blockTable);
    Output.splitByView(viewBlockStream, celoConfig.getSinkSuffix());

    KStream<String, TimeSeriesRecord> timeSeriesStream = Transformations.transformToTimeSeries(viewBlockStream);
    Output.splitByTimeSeries(timeSeriesStream, celoConfig.getSinkSuffix());

    KStream<String, TimeSeriesRecord> timeSeriesEventStream = Transformations.transformEventToTimeSeries(eventBlockStream);
    Output.splitByTimeSeries(timeSeriesEventStream, celoConfig.getSinkSuffix());

    // Create topics that are going to be needed for the joins.
    Properties props = new Properties();
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, celoConfig.getKafkaServer());
    AdminClient a = AdminClient.create(props);
    a.createTopics(Arrays.asList(
      new NewTopic(celoConfig.getEpochRewardsAggregationTopic(), 1, (short) 1),
      new NewTopic(celoConfig.getEpochRewardsGetTargetGetGoldTotalSupplyTopic(), 1, (short) 1),
      new NewTopic(celoConfig.getGoldTokenTotalSupplyTopic(),1, (short) 1)
      )
    );

    KStream<String, TimeSeriesRecord> epochRewardsGetTargetGetGoldTotalSupplyStream = this.builder.stream(
      celoConfig.getEpochRewardsGetTargetGetGoldTotalSupplyTopic(), Consumed.with(Serdes.String(), Web3MonitoringSerdes.getTimeSerieserde()));
    KStream<String, TimeSeriesRecord> goldTokenTotalSupplyStream = this.builder.stream(
      celoConfig.getGoldTokenTotalSupplyTopic(), Consumed.with(Serdes.String(), Web3MonitoringSerdes.getTimeSerieserde()));

    EpochRewardsAggregation.joinEpochGetArgGetGoldTotalSupplyWithGoldTokenTotalSupply(
      epochRewardsGetTargetGetGoldTotalSupplyStream, goldTokenTotalSupplyStream)
      .to(celoConfig.getEpochRewardsAggregationTopic(), Produced.with(Serdes.Long(), Web3MonitoringSerdes.getTimeSerieserde()));

  }

  public static void main(final String[] args) throws Exception {

    Config config = args.length > 0 ? ConfigFactory.load(args[0]) : ConfigFactory.load();

    CeloConfig celoConfig = new CeloConfig(config);
    new CeloStreamManager(celoConfig).initStream();

  }
}
