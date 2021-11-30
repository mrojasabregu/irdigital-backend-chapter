package org.irdigital.techmeup.api.thirdparty.placeholder;

import lombok.Data;

@Data
public class Comment {
    private Long postId;
    private Long id;
    private String name;
    private String email;
    private String body;
}
