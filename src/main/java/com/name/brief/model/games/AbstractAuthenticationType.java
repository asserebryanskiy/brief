package com.name.brief.model.games;

import javax.persistence.MappedSuperclass;
import javax.servlet.http.HttpServletRequest;

@MappedSuperclass
public abstract class AbstractAuthenticationType {
    /**
     * Determines if all necessary to authenticate player data is present.
     *
     * @param request - http servlet request that contains data in its attributes.
     * @return true if not all data necessary for player authentication is present.
     */
    abstract boolean needsAdditionalData(HttpServletRequest request);

    /**
     * Adds all necessary
     *
     * @param request
     */
    abstract void addFlashAttributes(HttpServletRequest request);
}
