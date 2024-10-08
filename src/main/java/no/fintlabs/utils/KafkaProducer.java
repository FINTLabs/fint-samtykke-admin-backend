package no.fintlabs.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.fintlabs.adapter.models.OperationType;
import no.fintlabs.adapter.models.RequestFintEvent;
import no.fintlabs.kafka.event.EventProducer;
import no.fintlabs.kafka.event.EventProducerFactory;
import no.fintlabs.kafka.event.EventProducerRecord;
import no.fintlabs.kafka.event.topic.EventTopicNameParameters;
import no.fintlabs.kafka.event.topic.EventTopicService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaProducer {

    private static final int RETENTION_TIME_MS = 172800000;
    private final EventProducer<Object> eventProducer;
    private final EventTopicService eventTopicService;

    public KafkaProducer(EventProducerFactory eventProducerFactory, EventTopicService eventTopicService) {
        this.eventProducer = eventProducerFactory.createProducer(Object.class);
        this.eventTopicService = eventTopicService;
    }

    public RequestFintEvent sendEvent(OperationType operationType, String resourceName, String orgId, Object value) {
        RequestFintEvent requestEvent = createRequestEvent(orgId, resourceName, operationType, value);
        EventTopicNameParameters topicNameParameters = createTopicNameParameters(orgId, createEventName(resourceName));
        eventTopicService.ensureTopic(topicNameParameters, RETENTION_TIME_MS);

        eventProducer.send(
                EventProducerRecord.builder()
                        .topicNameParameters(topicNameParameters)
                        .value(requestEvent)
                        .build()
        );

        return requestEvent;
    }

    private String convertToJson(Object body) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writer().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private EventTopicNameParameters createTopicNameParameters(String orgId, String eventName) {
        return EventTopicNameParameters
                .builder()
                .orgId(OrgIdUtil.toKafkaTopic(orgId))
                .domainContext("fint-core")
                .eventName(eventName)
                .build();
    }

    private String createEventName(String resourceName) {
        return String.format("%s-%s-%s",
                "personvern-samtykke",
                resourceName,
                "request");
    }

    private RequestFintEvent createRequestEvent(String orgId, String resourceName, OperationType operationType, Object value) {
        RequestFintEvent requestFintEvent = new RequestFintEvent();
        requestFintEvent.setCorrId(UUID.randomUUID().toString());
        requestFintEvent.setOrgId(OrgIdUtil.toKafkaEvent(orgId));
        requestFintEvent.setDomainName("personvern");
        requestFintEvent.setPackageName("samtykke");
        requestFintEvent.setResourceName(resourceName);
        requestFintEvent.setOperationType(operationType);
        requestFintEvent.setCreated(System.currentTimeMillis());
        requestFintEvent.setValue(convertToJson(value));
        return requestFintEvent;
    }

}
