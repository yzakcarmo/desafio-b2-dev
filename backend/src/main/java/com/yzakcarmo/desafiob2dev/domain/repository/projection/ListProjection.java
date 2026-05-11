package com.yzakcarmo.desafiob2dev.domain.repository.projection;

import java.util.UUID;

public interface ListProjection {
    String getExternalReference();
    String getName();
    UUID getId();

}
