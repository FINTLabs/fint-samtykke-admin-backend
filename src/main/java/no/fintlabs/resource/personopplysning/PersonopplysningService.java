package no.fintlabs.resource.personopplysning;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.personvern.kodeverk.PersonopplysningResource;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class PersonopplysningService {

    private final Map<String, PersonopplysningResource> personopplysningResources;

    public PersonopplysningService() {
        personopplysningResources = new HashMap<>();
    }


    public List<Personopplysning> getPersonopplysning() {
       return personopplysningResources.values()
                .stream()
                .map(this::createPersonopplysning)
                .toList();
    }

    private Personopplysning createPersonopplysning(PersonopplysningResource personopplysningResource) {
        return Personopplysning.builder()
                .id(personopplysningResource.getSystemId().getIdentifikatorverdi())
                .kode(personopplysningResource.getKode())
                .navn(personopplysningResource.getNavn())
                .build();
    }

    public void addResource(PersonopplysningResource resource) {
        personopplysningResources.put(resource.getSystemId().getIdentifikatorverdi(), resource);
    }
}
