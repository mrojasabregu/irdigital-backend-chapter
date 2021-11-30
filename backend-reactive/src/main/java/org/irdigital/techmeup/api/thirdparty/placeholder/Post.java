package org.irdigital.techmeup.api.thirdparty.placeholder;

import lombok.Data;

@Data
public class Post {
    private Long userId;
    private Long id;
    private String title;
    private String body;
}
