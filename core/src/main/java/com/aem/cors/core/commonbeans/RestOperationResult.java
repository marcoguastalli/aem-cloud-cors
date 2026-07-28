package com.aem.cors.core.commonbeans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Bean used as json response for rest operations
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class RestOperationResult implements Serializable {

    private static final long serialVersionUID = 2048489816933242953L;

    private transient Object result;

}
