package io.keyko.monitoring;

import io.keyko.monitoring.examples.celo.model.AccountCreatedAggregation;
import io.keyko.monitoring.schemas.*;
import io.keyko.monitoring.time.EventBlockTimestampExtractor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class CeloProcessor {

    /**
     * Aggregate accounts created events using a daily window
     *
     * @param topicsToAggregate
     * @param builder
     * @return
     */
    public static KStream<String, AccountCreatedAggregation> accountDailyAggregation(List<String> topicsToAggregate, StreamsBuilder builder) {

        final int windowStartHour = 0;
        final ZoneId zone = ZoneOffset.UTC;
        final Duration gracePeriod = Duration.ofMinutes(60L);

        List<String> accountsTopics = topicsToAggregate;//, "VoteSignerAuthorized".toLowerCase(), "AttestationSignerAuthorized".toLowerCase());

        KStream<String, EventBlockRecord> accountsCreatedStream = builder.stream(accountsTopics,
                Consumed.with(Serdes.String(), CeloSerdes.getEventBlockSerde())
                        .withTimestampExtractor(new EventBlockTimestampExtractor()));

        KTable<Windowed<String>, Long> accountsCreatedDayTable =
                accountsCreatedStream
                        .selectKey((key, event) -> event.getDetails().getName())
                        .groupByKey(Grouped.with(Serdes.String(), CeloSerdes.getEventBlockSerde()))
                        //.windowedBy(new DailyTimeWindows(zone, windowStartHour, gracePeriod))
                        .windowedBy(TimeWindows.of(Duration.ofSeconds(60)))
                        .count()//Materialized.<String, Long, WindowStore<Bytes, byte[]>>with(Serdes.String(), Serdes.Long())
                // the default store retention time is 1 day;
                // need to explicitly increase the retention time
                // to allow for a 1-day window plus configured grace period
                //.withRetention(Duration.ofDays(1L).plus(gracePeriod)))
                // emits the final count when the window is closed.
                //.suppress(Suppressed.untilWindowCloses(unbounded()));
                ;

        return formatAccountCreatedAggregation(accountsCreatedDayTable, zone);

    }


    /**
     * Transforms a Windowed Aggregation Table in a Stream of formatted data
     *
     * @param accountsCreatedDayTable
     * @param zone
     * @return
     */
    private static KStream<String, AccountCreatedAggregation> formatAccountCreatedAggregation(KTable<Windowed<String>, Long> accountsCreatedDayTable, ZoneId zone) {

        return accountsCreatedDayTable
                .toStream()
                .map((windowed, count) -> {

                    AccountCreatedAggregation accountCreatedAggregation = new AccountCreatedAggregation(
                            windowed.window().startTime().atZone(zone),
                            count,
                            windowed.key()

                    );

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss Z");
                    String formattedString = windowed.window().startTime().atZone(ZoneOffset.UTC).format(formatter);

                    String key = windowed.key().concat("-").concat(formattedString);

                    //String key = windowed.key().concat("-").concat(accountCreatedAggregation.getDate());
                    return KeyValue.pair(key, accountCreatedAggregation);

                });

    }

    public static KStream<String, AlertRecord> alertNoEpochRewardsDistributed(StreamsBuilder builder, List<String> EpochRewardsDistributedToVoters) {
        return builder.stream(EpochRewardsDistributedToVoters, Consumed.with(Serdes.String(), CeloSerdes.getEventBlockSerde()))
                .filter((key, event) -> ((NumberParameter) event.getDetails().getNonIndexedParameters().get(0)).getValue().equals("0"))
                .map((key, event) ->
                        KeyValue.pair(key,
                                AlertRecord.newBuilder()
                                        .setName("alertNoEpochRewardsDistributed")
                                        .setReference(event.getId())
                                        .setStatus(AlertEventStatus.ERROR)
                                        .setTimestamp(event.getDetailsBlock().getTimestamp())
                                        .setDescription("NoEpochRewardsDistributed for group: " + ((StringParameter) event.getDetails().getIndexedParameters().get(0)).getValue())
                                        .build())
                );
//      .to("w3m-alerts");
//      .foreach((x, y) -> System.out.println("NoEpochRewardsDistributed for group: " + ((StringParameter) y.getDetails().getIndexedParameters().get(0)).getValue()));
    }

}
