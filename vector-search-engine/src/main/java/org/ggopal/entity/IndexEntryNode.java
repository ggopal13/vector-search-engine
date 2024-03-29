package org.ggopal.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@ToString
@Builder
@RequiredArgsConstructor
@EqualsAndHashCode
public class IndexEntryNode implements Serializable {

    private final Integer lineNo;

    private final String documentName;

    private final String fullPath;

    private final String keyword;

}
