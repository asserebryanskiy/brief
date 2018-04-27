package com.name.brief.model.games.roleplay;

import com.name.brief.model.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class DoctorRolesSet extends BaseEntity {
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<PharmaRole> roles = new HashSet<>();

    public DoctorRolesSet() {
        super();
    }
}
