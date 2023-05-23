package com.github.wyhasany.model;

import java.util.List;

public record StartupStep(String name, int id, Integer parentId, List<Tags> tags) {
    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }
}