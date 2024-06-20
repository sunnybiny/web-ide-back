package org.goorm.webide.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Container {
    @Id
    private String id;

    private String name;

    private String image;

    public static Container createContainer(String id, String name, String image) {
        Container container = new Container();
        container.setId(id);
        container.setName(name);
        container.setImage(image);
        return container;
    }


}

