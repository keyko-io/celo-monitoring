package io.keyko.monitoring;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.keyko.monitoring.postprocessing.Output;
import io.keyko.monitoring.preprocessing.Filters;
import io.keyko.monitoring.preprocessing.Transformations;
import io.keyko.monitoring.schemas.*;
import io.keyko.monitoring.stream.BaseStreamManager;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

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

    Output.splitByEvent(eventBlockStream,celoConfig.getSinkSuffix());

    KStream<String, ViewBlockRecord> viewBlockStream = Transformations.joinViewWithBlock(viewStream, blockTable);
    Output.splitByView(viewBlockStream, celoConfig.getSinkSuffix());

    KStream<String, TimeSeriesRecord> timeSeriesStream = Transformations.transformToTimeSeries(viewBlockStream);
    Output.splitByTimeSeries(timeSeriesStream, celoConfig.getSinkSuffix());

    KStream<String, TimeSeriesRecord> timeSeriesEventStream = Transformations.transformEventToTimeSeries(eventBlockStream);
    Output.splitByTimeSeries(timeSeriesEventStream, celoConfig.getSinkSuffix());

  }

  public static void main(final String[] args) throws Exception {

    Config config = args.length > 0 ? ConfigFactory.load(args[0]) : ConfigFactory.load();

    CeloConfig celoConfig = new CeloConfig(config);
    new CeloStreamManager(celoConfig).initStream();

  }
}
