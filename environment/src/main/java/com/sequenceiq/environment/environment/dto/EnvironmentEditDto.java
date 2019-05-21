package com.sequenceiq.environment.environment.dto;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

public class EnvironmentEditDto {

    private final String description;

    private final Set<String> regions;

    private final LocationDto location;

    public EnvironmentEditDto(String description, Set<String> regions, LocationDto location) {
        this.description = description;
        if (CollectionUtils.isEmpty(regions)) {
            this.regions = new HashSet<>();
        } else {
            this.regions = regions;
        }
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getRegions() {
        return regions;
    }

    public LocationDto getLocation() {
        return location;
    }


    public static final class EnvironmentEditDtoBuilder {
        private String description;
        private Set<String> regions;
        private LocationDto location;

        private EnvironmentEditDtoBuilder() {
        }

        public static EnvironmentEditDtoBuilder anEnvironmentEditDto() {
            return new EnvironmentEditDtoBuilder();
        }

        public EnvironmentEditDtoBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EnvironmentEditDtoBuilder withRegions(Set<String> regions) {
            this.regions = regions;
            return this;
        }

        public EnvironmentEditDtoBuilder withLocation(LocationDto location) {
            this.location = location;
            return this;
        }

        public EnvironmentEditDto build() {
            return new EnvironmentEditDto(description, regions, location);
        }
    }
}
