package ru.volkov.batch.partitioning.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.file.transform.LineAggregator;

public class RandLineAggregator implements LineAggregator<Rand> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String aggregate(Rand rand) {
        try {
            return mapper.writeValueAsString(rand);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Uable to serialize object: " + e.getMessage());
        }
    }
}
