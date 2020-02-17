package io.keyko.monitoring;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.keyko.monitoring.schemas.AlertRecord;
import io.keyko.monitoring.serde.Web3MonitoringSerdes;

public class CeloSerdes extends Web3MonitoringSerdes {

    // Web3MonitoringSerdes actually has an alertSerde but we use this as an example about how to add new serdes
    private final static SpecificAvroSerde<AlertRecord> alertAvroSerde = new SpecificAvroSerde<>();

    public static SpecificAvroSerde<AlertRecord> getAlertAvroSerde() {
        return alertAvroSerde;
    }

    public static void configureAllSerdes(String schemaRegistryUrl) {

        // we call the configureSerdes of Web3MonitoringSerdes
        configureSerdes(schemaRegistryUrl);

        //we configure the especific class
        configureSerde(alertAvroSerde);

    }

}
