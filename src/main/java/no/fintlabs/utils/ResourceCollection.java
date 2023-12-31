package no.fintlabs.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResourceCollection<T> {
    private final Map<String, Map<String, T>> resources;

    public ResourceCollection() {
        resources = new HashMap<>();
    }

    public void put(String orgId, String identifikatorverdi, T resource) {
        resources.computeIfAbsent(orgId, test -> new HashMap<>())
                .put(identifikatorverdi, resource);
    }

    public List<T> getResources(String orgName) {
        return resources.computeIfAbsent(orgName, test -> new HashMap<>())
                .values().stream().toList();
    }

    public Optional<T> getResource(String orgName, String id) {
        return Optional.of(resources.computeIfAbsent(orgName, test -> new HashMap<>()).get(id));
    }
}
