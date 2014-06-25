package com.onyem.khoj.core.domain;

/**
 * State for lifetime of a Node
 */
public enum State {

    // Unknown state
    TRANSIENT,

    // Being constructed
    PARTIAL,

    // Construction is complete
    COMPLETE;

}
