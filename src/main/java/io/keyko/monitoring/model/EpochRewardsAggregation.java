package io.keyko.monitoring.model;


import io.keyko.monitoring.schemas.TimeSeriesParameter;
import io.keyko.monitoring.schemas.TimeSeriesRecord;
import io.keyko.monitoring.serde.Web3MonitoringSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;

import java.time.Duration;

public class EpochRewardsAggregation {

  public static KStream<Long, TimeSeriesRecord> joinEpochGetArgGetGoldTotalSupplyWithGoldTokenTotalSupply(KStream<String, TimeSeriesRecord> epochRewardsGetTargetGetGoldTotalSupply, KStream<String, TimeSeriesRecord> goldTokenTotalSupply) {

    KStream<Long, TimeSeriesRecord> e = epochRewardsGetTargetGetGoldTotalSupply.selectKey((k, v) -> v.getBlockNumber());
    KStream<Long, TimeSeriesRecord> g = goldTokenTotalSupply.selectKey((k, v) -> v.getBlockNumber());

    return e.join(g, (epoch, goldToken) -> {
        TimeSeriesParameter param0 = new TimeSeriesParameter();
        param0.setValue(epoch.getParam0().getValue());
        param0.setNumberValue(epoch.getParam0().getNumberValue());
        param0.setLabel("EpochRewards.GetTargetGoldTotalSupply");

        TimeSeriesParameter param1 = new TimeSeriesParameter();
        param1.setValue(goldToken.getParam0().getValue());
        param1.setNumberValue(goldToken.getParam0().getNumberValue());
        param1.setLabel("GoldToken.TotalSupply");

        TimeSeriesRecord t = new TimeSeriesRecord();

        t.setBlockNumber(epoch.getBlockNumber());
        t.setTimestamp(epoch.getTimestamp());
        t.setMethodName("EpochRewardsAggregation");
        t.setContractName("EpochRewards_GoldToken");
        t.setParam0(param0);
        t.setParam1(param1);
        return t;
      }
      , JoinWindows.of(Duration.ofMinutes(5))
      , StreamJoined.with(Serdes.Long(), Web3MonitoringSerdes.getTimeSerieserde(), Web3MonitoringSerdes.getTimeSerieserde())
    );
  }
}
